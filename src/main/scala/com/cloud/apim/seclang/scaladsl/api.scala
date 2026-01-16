package com.cloud.apim.seclang.scaladsl

import com.cloud.apim.seclang.impl.compiler._
import com.cloud.apim.seclang.impl.engine._
import com.cloud.apim.seclang.impl.factory.SecLangEngineFactory
import com.cloud.apim.seclang.impl.parser.AntlrParser
import com.cloud.apim.seclang.model._
import play.api.libs.json.{JsPath, Json, JsonValidationError}

import scala.collection.concurrent.TrieMap

object SecLang {

  def parse(input: String): Either[SecLangError, Configuration] = AntlrParser.parse(input)

  def parseJson(input: String): Either[Seq[(JsPath, Seq[JsonValidationError])], Configuration] = Configuration.format.reads(Json.parse(input)).asEither

  def compile(configuration: Configuration): CompiledProgram = compileSafe(configuration).fold(err => throw err.throwable, identity)

  def compileSafe(configuration: Configuration): Either[SecLangError, CompiledProgram] = Compiler.compile(configuration)

  def engine(
    program: CompiledProgram,
    config: SecLangEngineConfig = SecLangEngineConfig.default,
    files: Map[String, String] = Map.empty,
    txMap: Option[TrieMap[String, String]] = None,
    integration: SecLangIntegration = DefaultSecLangIntegration.default,
  ): SecLangEngine = {
    new SecLangEngine(program, config, files, txMap, integration)
  }

  def factory(presets: Map[String, SecLangPreset], config: SecLangEngineConfig = SecLangEngineConfig.default, integration: SecLangIntegration = DefaultSecLangIntegration.default): SecLangEngineFactory = {
    new SecLangEngineFactory(
      presets = presets,
      config = config,
      integration = integration,
    )
  }
}

object SecLangPresets {
  def corerulesetLocalFs(path: String) = SecLangPreset.fromSource(
    name = "crs",
    rulesSource = ConfigurationSourceList(List(FileScanConfigurationSource(path, """.*\.conf"""))),
    filesSource = FilesSourceList(List(FsScanFilesSource(path, """.*\.data"""))),
  )
  val coreruleset = SecLangPreset.fromSource(
    name = "crs",
    rulesSource = ConfigurationSourceList(
      List(
        HttpConfigurationSource("https://raw.githubusercontent.com/coreruleset/coreruleset/refs/heads/main/crs-setup.conf.example")
      ) ++ List(
        "REQUEST-900-EXCLUSION-RULES-BEFORE-CRS.conf.example",
        "REQUEST-901-INITIALIZATION.conf",
        "REQUEST-905-COMMON-EXCEPTIONS.conf",
        "REQUEST-911-METHOD-ENFORCEMENT.conf",
        "REQUEST-913-SCANNER-DETECTION.conf",
        "REQUEST-920-PROTOCOL-ENFORCEMENT.conf",
        "REQUEST-921-PROTOCOL-ATTACK.conf",
        "REQUEST-922-MULTIPART-ATTACK.conf",
        "REQUEST-930-APPLICATION-ATTACK-LFI.conf",
        "REQUEST-931-APPLICATION-ATTACK-RFI.conf",
        "REQUEST-932-APPLICATION-ATTACK-RCE.conf",
        "REQUEST-933-APPLICATION-ATTACK-PHP.conf",
        "REQUEST-934-APPLICATION-ATTACK-GENERIC.conf",
        "REQUEST-941-APPLICATION-ATTACK-XSS.conf",
        "REQUEST-942-APPLICATION-ATTACK-SQLI.conf",
        "REQUEST-943-APPLICATION-ATTACK-SESSION-FIXATION.conf",
        "REQUEST-944-APPLICATION-ATTACK-JAVA.conf",
        "REQUEST-949-BLOCKING-EVALUATION.conf",
        "RESPONSE-950-DATA-LEAKAGES.conf",
        "RESPONSE-951-DATA-LEAKAGES-SQL.conf",
        "RESPONSE-952-DATA-LEAKAGES-JAVA.conf",
        "RESPONSE-953-DATA-LEAKAGES-PHP.conf",
        "RESPONSE-954-DATA-LEAKAGES-IIS.conf",
        "RESPONSE-955-WEB-SHELLS.conf",
        "RESPONSE-956-DATA-LEAKAGES-RUBY.conf",
        "RESPONSE-959-BLOCKING-EVALUATION.conf",
        "RESPONSE-980-CORRELATION.conf",
        "RESPONSE-999-EXCLUSION-RULES-AFTER-CRS.conf.example"
      ).map(s => HttpConfigurationSource(s"https://raw.githubusercontent.com/coreruleset/coreruleset/refs/heads/main/rules/${s}"))
    ),
    filesSource = FilesSourceList(
      List(
        "asp-dotnet-errors.data",
        "iis-errors.data",
        "java-classes.data",
        "lfi-os-files.data",
        "php-errors.data",
        "php-function-names-933150.data",
        "php-variables.data",
        "restricted-files.data",
        "restricted-upload.data",
        "ruby-errors.data",
        "scanners-user-agents.data",
        "sql-errors.data",
        "ssrf.data",
        "unix-shell-builtins.data",
        "unix-shell.data",
        "web-shells-asp.data",
        "web-shells-php.data",
        "windows-powershell-commands.data",
      ).map(n => HttpFilesSource(n, s"https://raw.githubusercontent.com/coreruleset/coreruleset/refs/heads/main/rules/${n}"))
    ),
  )
}