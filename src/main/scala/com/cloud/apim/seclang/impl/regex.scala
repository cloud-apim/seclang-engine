package scala.util.matching

import java.util.regex.Pattern

object RegexUtil {
  def build(pattern: Pattern): Regex = {
    new Regex(pattern)
  }
}
