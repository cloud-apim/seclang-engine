package com.cloud.apim.seclang.impl.factory

import com.cloud.apim.seclang.impl.compiler.Compiler
import com.cloud.apim.seclang.impl.engine.SecLangEngine
import com.cloud.apim.seclang.impl.parser.AntlrParser
import com.cloud.apim.seclang.impl.utils.HashUtilsFast
import com.cloud.apim.seclang.model._

import scala.concurrent.duration.{DurationInt, FiniteDuration}

class SecLangEngineFactory(
  presets: Map[String, SecLangPreset],
  config: SecLangEngineConfig = SecLangEngineConfig.default,
  integration: SecLangIntegration = DefaultSecLangIntegration.default,
  cacheTtl: FiniteDuration = 10.minutes,
) {

  def evaluate(configs: List[String], ctx: RequestContext, phases: List[Int] = List(1, 2)): EngineResult = {
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
            val compiled = Compiler.compile(parsed)
            integration.putCachedProgram(hash, compiled, cacheTtl)
            Some((compiled, Map.empty[String, String]))
          }
        }
      }
    }
    val programs = programsAndFiles.map(_._1)
    val files = programsAndFiles.map(_._2).flatMap(_.toList).toMap
    val program = ComposedCompiledProgram(programs)
    new SecLangEngine(program, config: SecLangEngineConfig, files, integration).evaluate(ctx, phases)
  }
}
