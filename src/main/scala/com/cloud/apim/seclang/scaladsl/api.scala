package com.cloud.apim.seclang.scaladsl

import com.cloud.apim.seclang.impl.parser.AntlrParser
import com.cloud.apim.seclang.impl.compiler._
import com.cloud.apim.seclang.impl.engine._
import com.cloud.apim.seclang.model.{Configuration, SecRulesEngineConfig}

object SecLang {
  def parse(input: String): Either[String, Configuration] = AntlrParser.parse(input)
  def compile(configuration: Configuration): CompiledProgram = Compiler.compile(configuration)
  def engine(
    program: CompiledProgram,
    files: Map[String, String] = Map.empty,
    env: Map[String, String] = Map.empty,
    config: SecRulesEngineConfig = SecRulesEngineConfig.default
  ): SecRulesEngine = {
    new SecRulesEngine(program, files, env, config)
  }
}