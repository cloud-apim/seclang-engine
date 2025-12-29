package com.cloud.apim.seclang.scaladsl

import com.cloud.apim.seclang.antlr.{SecLangLexer, SecLangParser, SecLangParserBaseListener, SecLangParserBaseVisitor}
import com.cloud.apim.seclang.impl.compiler._
import com.cloud.apim.seclang.impl.engine._
import com.cloud.apim.seclang.impl.model._
import com.cloud.apim.seclang.impl.parser._

class AstBuilderVisitor extends SecLangParserBaseVisitor[Unit] {
  override def visitStmt(ctx: SecLangParser.StmtContext): Unit = {
    println("Visiting " + ctx.getText)
    ()
  }
}

class AstBuilderListener extends SecLangParserBaseListener {
  override def enterStmt(ctx: SecLangParser.StmtContext): Unit = {
    println(s"enterStmt: ${ctx.getText}")
  }
  override def exitStmt(ctx: SecLangParser.StmtContext): Unit = {
    println(s"exitStmt: ${ctx.getText}")
  }
}

object SecLang {

  def antlr(in: String): Unit = {
    // from https://github.com/coreruleset/seclang_parser/tree/main
    import org.antlr.v4.runtime._
    import org.antlr.v4.runtime.tree._

    val input  = CharStreams.fromString(in)
    val lexer  = new SecLangLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new SecLangParser(tokens)
    val tree = parser.configuration()

    val walker = new ParseTreeWalker()
    walker.walk(new AstBuilderListener(), tree);

    //println(tree.toStringTree(parser))
    val result = new AstBuilderVisitor().visit(tree)
  }


  def parse(input: String): Either[String, List[Directive]] = {
    //FastParseParser.parse(input)

    antlr(input)
    Parser2.parse(input)
  }
  def compile(directives: List[Directive]): CompiledProgram = Compiler.compile(directives)
  def engine(program: CompiledProgram): SecRulesEngine = new SecRulesEngine(program)
}