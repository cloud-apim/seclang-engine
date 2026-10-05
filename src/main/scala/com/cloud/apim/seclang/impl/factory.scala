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

  private def isPreset(line: String): Boolean = line.trim.startsWith("@import_preset ")

  /**
   * One element of a rule list, compiled, from the host's cache when it holds it.
   *
   * A parse error is an error like a compile error: it used to be a `None.get` thrown from here,
   * which made `engineSafe` throw on exactly what it exists to report.
   */
  private def programOf(line: String): Either[SecLangError, CompiledProgram] = {
    val hash = HashUtilsFast.sha512Hex(line)
    integration.getCachedProgram(hash) match {
      case Some(p) => Right(p)
      case None    =>
        val log = (msg: String) => integration.logDebug(msg)
        AntlrParser.parse(line, config.includeRawRule, config.includeComments, log).flatMap(Compiler.compile(_, log)).map { compiled =>
          integration.putCachedProgram(hash, compiled, cacheTtl)
          compiled
        }
    }
  }

  private def presetOf(line: String): Option[(CompiledProgram, Map[String, String])] = {
    val presetName = line.replaceFirst("@import_preset ", "").trim
    presets.get(presetName).orElse(integration.getExternalPreset(presetName)).map(p => (p.program, p.files))
  }

  private def build(programsAndFiles: List[(CompiledProgram, Map[String, String])]): SecLangEngine = {
    val programs = programsAndFiles.map(_._1)
    val files = programsAndFiles.map(_._2).flatMap(_.toList).toMap
    val program = ComposedCompiledProgram(programs)
    val txMap = new TrieMap[String, String]()
    new SecLangEngine(program, config: SecLangEngineConfig, files, Some(txMap), integration)
  }

  def precompileAndCache(configs: List[String]): Unit = {
    configs.filterNot(isPreset).foreach(line => programOf(line).fold(err => throw err.throwable, _ => ()))
  }

  def engine(configs: List[String]): SecLangEngine = {
    build(configs.flatMap {
      case line if isPreset(line) => presetOf(line)
      case line => Some((programOf(line).fold(err => throw err.throwable, identity), Map.empty[String, String]))
    })
  }

  def engineSafe(configs: List[String]): Either[List[SecLangError], SecLangEngine] = {
    val programsAndFilesE: List[Either[SecLangError, (CompiledProgram, Map[String, String])]] = configs.flatMap {
      case line if isPreset(line) => presetOf(line).map(Right(_))
      case line => Some(programOf(line).map(p => (p, Map.empty[String, String])))
    }
    val errors = programsAndFilesE.collect { case Left(err) => err }
    if (errors.nonEmpty) Left(errors)
    else Right(build(programsAndFilesE.collect { case Right(paf) => paf }))
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
