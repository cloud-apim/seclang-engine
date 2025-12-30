package com.cloud.apim.seclang.scaladsl

import com.cloud.apim.seclang.antlr.AntlrParser
import com.cloud.apim.seclang.impl.compiler._
import com.cloud.apim.seclang.impl.engine._
import com.cloud.apim.seclang.impl.model._
import com.cloud.apim.seclang.impl.parser._
import com.cloud.apim.seclang.model.Configuration

object SecLang {
  def parseFastParse(input: String): Either[String, List[Directive]] = FastParseParser.parse(input)
  def parseScala(input: String): Either[String, List[Directive]] = ScalaParser.parse(input)
  def parseAntlr(input: String): Either[String, Configuration] = AntlrParser.parse(input)
  def parse(input: String): Either[String, List[Directive]] = parseScala(input)
  def compile(directives: List[Directive]): CompiledProgram = Compiler.compile(directives)
  def engine(program: CompiledProgram): SecRulesEngine = new SecRulesEngine(program)
}