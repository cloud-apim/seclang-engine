package com.cloud.apim.seclang.antlr

import com.cloud.apim.seclang.impl.model.Directive

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

object AntlrParser {
  def parse(in: String): Either[String, List[Directive]] = {
    
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
    
    Right(List())
  }
}
