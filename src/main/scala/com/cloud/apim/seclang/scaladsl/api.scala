package com.cloud.apim.seclang.scaladsl

import com.cloud.apim.seclang.impl.compiler._
import com.cloud.apim.seclang.impl.engine._
import com.cloud.apim.seclang.impl.factory.SecLangEngineFactory
import com.cloud.apim.seclang.impl.parser.AntlrParser
import com.cloud.apim.seclang.model._

object SecLang {
  def parse(input: String): Either[String, Configuration] = AntlrParser.parse(input)
  def compile(configuration: Configuration): CompiledProgram = Compiler.compile(configuration)
  def engine(
    program: CompiledProgram,
    config: SecLangEngineConfig = SecLangEngineConfig.default,
    files: Map[String, String] = Map.empty,
    integration: SecLangIntegration = DefaultSecLangIntegration.default,
  ): SecLangEngine = {
    new SecLangEngine(program, config, files, integration)
  }
  def factory(presets: Map[String, SecLangPreset], config: SecLangEngineConfig = SecLangEngineConfig.default, integration: SecLangIntegration = DefaultSecLangIntegration.default): SecLangEngineFactory = {
    new SecLangEngineFactory(
      presets = presets,
      config = config,
      integration = integration,
    )
  }
}