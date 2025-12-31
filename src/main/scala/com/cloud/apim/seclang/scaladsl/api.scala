package com.cloud.apim.seclang.scaladsl

import com.cloud.apim.seclang.impl.parser.AntlrParser
import com.cloud.apim.seclang.impl.compiler._
import com.cloud.apim.seclang.impl.engine._
import com.cloud.apim.seclang.model.Configuration

object SecLang {
  def parse(input: String): Either[String, Configuration] = AntlrParser.parse(input)
  def compile(configuration: Configuration): CompiledProgram = Compiler.compile(configuration)
  def engine(program: CompiledProgram): SecRulesEngine = new SecRulesEngine(program)
}