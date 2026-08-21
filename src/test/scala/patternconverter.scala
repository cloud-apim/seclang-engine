package com.cloud.apim.seclang.test

import com.cloud.apim.seclang.impl.utils.ModSecurityPatternConverter
import munit.FunSuite

import java.util.regex.Pattern

// these patterns used to be written as triple quoted strings containing unicode escapes. scala 2
// resolves those escapes at lex time (even inside triple quotes, and even inside comments) while
// scala 3 does not, so the very same source produced a different string depending on the compiler.
// they are plain string literals now, which every scala version reads the same way. this pins it
// down. mind the comments in this file: on scala 2 a stray backslash-u is a lexer error.
class PatternConverterTest extends FunSuite {

  test("enclosed alphanumerics conversion is compiler independent") {
    val converted = ModSecurityPatternConverter.convert(
      """\x{e2}(?:\x91[\xa0-\xbf]|\x92[\x80-\xbf]|\x93[\x80-\xa9\xab-\xbf])"""
    )
    assertEquals(converted, "[\\u2460-\\u247f\\u2480-\\u24bf\\u24c0-\\u24e9\\u24eb-\\u24ff]")
    // U+2460 CIRCLED DIGIT ONE, inside the converted range
    assert(Pattern.compile(converted).matcher(Character.toString(0x2460)).find())
    // U+2500 BOX DRAWINGS LIGHT HORIZONTAL, just outside it
    assert(!Pattern.compile(converted).matcher(Character.toString(0x2500)).find())
  }

  test("CJK full stop conversion is compiler independent") {
    val converted = ModSecurityPatternConverter.convert("""\x{e3}\x80\x82""")
    assertEquals(converted, "\\u3002")
    // U+3002 IDEOGRAPHIC FULL STOP
    assert(Pattern.compile(converted).matcher(Character.toString(0x3002)).find())
  }
}
