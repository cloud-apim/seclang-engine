package com.cloud.apim.seclang.impl.factory

import com.cloud.apim.seclang.impl.compiler.Compiler
import com.cloud.apim.seclang.impl.engine.SecLangEngine
import com.cloud.apim.seclang.impl.parser.AntlrParser
import com.cloud.apim.seclang.impl.utils.HashUtilsFast
import com.cloud.apim.seclang.model._

import scala.collection.concurrent.TrieMap

class SecLangEngineFactory(
  presets: Map[String, SecLangPreset],
  config: SecLangEngineConfig = SecLangEngineConfig.default,
  integration: SecLangEngineFactoryIntegration
) {

  private val cache = new TrieMap[String, CompiledProgram]()

  def evaluate(configs: List[String], ctx: RequestContext, phases: List[Int] = List(1, 2)): EngineResult = {
    val programsAndFiles: List[(CompiledProgram, Map[String, String])] = configs.map {
      case line if line.trim.startsWith("@import_preset ") => {
        val presetName = line.replaceFirst("@import_preset ", "").trim
        val p = presets(presetName)
        (p.program, p.files)
      }
      case line => {
        val hash = HashUtilsFast.sha512Hex(line)
        (cache.getOrElseUpdate(hash, {
          val parsed = AntlrParser.parse(line).right.get
          Compiler.compile(parsed)
        }), Map.empty[String, String])
      }
    }
    val programs = programsAndFiles.map(_._1)
    val files = programsAndFiles.map(_._2).flatMap(_.toList).toMap
    val program = ComposedCompiledProgram(programs)
    val env = integration.getEnv()
    new SecLangEngine(program, config: SecLangEngineConfig, files, env).evaluate(ctx, phases)
  }
}
