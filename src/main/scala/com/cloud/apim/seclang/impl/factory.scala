package com.cloud.apim.seclang.impl.factory

import com.cloud.apim.seclang.impl.compiler.Compiler
import com.cloud.apim.seclang.impl.engine.SecLangEngine
import com.cloud.apim.seclang.impl.parser.AntlrParser
import com.cloud.apim.seclang.impl.utils.HashUtilsFast
import com.cloud.apim.seclang.model._

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration.{DurationInt, FiniteDuration}

class SecLangEngineFactory(
  presets: Map[String, SecLangPreset],
  config: SecLangEngineConfig = SecLangEngineConfig.default,
  integration: SecLangIntegration = DefaultSecLangIntegration.default,
  cacheTtl: FiniteDuration = 10.minutes,
) {

  def precompileAndCache(configs: List[String]): Unit = {
    configs.foreach {
      case line if line.trim.startsWith("@import_preset ") => ()
      case line => {
        val hash = HashUtilsFast.sha512Hex(line)
        integration.getCachedProgram(hash) match {
          case Some(p) => Some((p, Map.empty[String, String]))
          case None => {
            val compiled = Compiler.compileUnsafe(AntlrParser.parse(line).right.get)
            integration.putCachedProgram(hash, compiled, cacheTtl)
          }
        }
      }
    }
  }

  def engine(configs: List[String]): SecLangEngine = {
    val programsAndFiles: List[(CompiledProgram, Map[String, String])] = configs.flatMap {
      case line if line.trim.startsWith("@import_preset ") => {
        val presetName = line.replaceFirst("@import_preset ", "").trim
        presets.get(presetName).orElse(integration.getExternalPreset(presetName)).map(p => (p.program, p.files))
      }
      case line => {
        val hash = HashUtilsFast.sha512Hex(line)
        integration.getCachedProgram(hash) match {
          case Some(p) => Some((p, Map.empty[String, String]))
          case None => {
            val parsed = AntlrParser.parse(line).right.get
            val compiled = Compiler.compileUnsafe(parsed)
            integration.putCachedProgram(hash, compiled, cacheTtl)
            Some((compiled, Map.empty[String, String]))
          }
        }
      }
    }
    val programs = programsAndFiles.map(_._1)
    val files = programsAndFiles.map(_._2).flatMap(_.toList).toMap
    val program = ComposedCompiledProgram(programs)
    val txMap = new TrieMap[String, String]()
    new SecLangEngine(program, config: SecLangEngineConfig, files, Some(txMap), integration)
  }
  def engineSafe(configs: List[String]): Either[List[SecLangError], SecLangEngine] = {
    val programsAndFilesE: List[Either[SecLangError, (CompiledProgram, Map[String, String])]] = configs.flatMap {
      case line if line.trim.startsWith("@import_preset ") => {
        val presetName = line.replaceFirst("@import_preset ", "").trim
        presets.get(presetName).orElse(integration.getExternalPreset(presetName)).map(p => Right((p.program, p.files)))
      }
      case line => {
        val hash = HashUtilsFast.sha512Hex(line)
        integration.getCachedProgram(hash) match {
          case Some(p) => Some(Right((p, Map.empty[String, String])))
          case None => {
            val parsed = AntlrParser.parse(line).right.get
            Compiler.compile(parsed) match {
              case Left(err) => Some(Left(err))
              case Right(compiled) => {
                integration.putCachedProgram(hash, compiled, cacheTtl)
                Some(Right((compiled, Map.empty[String, String])))
              }
            }
          }
        }
      }
    }
    val (errors, programsAndFiles) = programsAndFilesE.partition(_.isLeft)
    if (errors.nonEmpty) {
      Left(errors.map(_.left.get))
    } else {
      val pafs = programsAndFiles.map(_.right.get)
      val programs = pafs.map(_._1)
      val files = pafs.map(_._2).flatMap(_.toList).toMap
      val program = ComposedCompiledProgram(programs)
      val txMap = new TrieMap[String, String]()
      Right(new SecLangEngine(program, config: SecLangEngineConfig, files, Some(txMap), integration))
    }
  }

  def evaluate(configs: List[String], ctx: RequestContext, phases: List[Int] = List(1, 2), txMap: Option[TrieMap[String, String]] = None): EngineResult = {
    engine(configs).evaluate(ctx, phases, txMap)
  }

  def evaluateSafe(configs: List[String], ctx: RequestContext, phases: List[Int] = List(1, 2), txMap: Option[TrieMap[String, String]] = None): Either[List[SecLangError], EngineResult] = {
    engineSafe(configs) match {
      case Left(err) => Left(err)
      case Right(engine) => engine.evaluateSafe(ctx, phases, txMap) match {
        case Left(err) => Left(List(err))
        case Right(r) => Right(r)
      }
    }
  }
}
