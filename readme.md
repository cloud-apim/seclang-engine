# Cloud APIM - SecLang Engine WAF

SecLang Engine WAF is a ModSecurity-compatible Web Application Firewall (WAF) library for the JVM, written in Scala.

It implements a subset of the [ModSecurity language](https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29), optimized to run the [OWASP Core Rule Set (CRS)](https://coreruleset.org/) at scale, with strong support for multi-tenancy, high concurrency, and user-level customization.

## Status

this project is a work in progress, right now we are passing **more than 98%** of the [CRS test suite](https://github.com/coreruleset/coreruleset/tree/main/tests/regression/tests)

```json
{
  "global_stats" : {
    "failure_percentage" : 1.3385251886103675,
    "passing_percentage" : 98.66147481138964,
    "total_tests" : 4109,
    "success_tests" : 4054,
    "failure_tests" : 55
  },
  "time_stats" : {
    "calls" : 4109,
    "total_time_ms" : 25426,
    "min_time_ns" : 415000,
    "min_time_ms" : 0,
    "max_time_ms" : 3342,
    "avg_time_ms" : 6
  }
}
```

## Simple usage

```scala
import com.cloud.apim.seclang.model.Disposition._
import com.cloud.apim.seclang.model._
import com.cloud.apim.seclang.scaladsl.SecLang

class SecLangTest extends munit.FunSuite {
  
  test("simple seclang rules") {
    val rules = """
                  |SecRule REQUEST_HEADERS:User-Agent "@pm firefox" \
                  |    "id:00001,\
                  |    phase:1,\
                  |    block,\
                  |    t:none,t:lowercase,\
                  |    msg:'someone used firefox to access',\
                  |    logdata:'someone used firefox to access',\
                  |    tag:'test',\
                  |    ver:'0.0.0-dev',\
                  |    status:403,\
                  |    severity:'CRITICAL'"
                  |
                  |SecRule REQUEST_URI "@contains /health" \
                  |    "id:00002,\
                  |    phase:1,\
                  |    pass,\
                  |    t:none,t:lowercase,\
                  |    msg:'someone called /health',\
                  |    logdata:'someone called /health',\
                  |    tag:'test',\
                  |    ver:'0.0.0-dev'"
                  |
                  |SecRuleEngine On
                  |""".stripMargin

    val loaded = SecLang.parse(rules).fold(err => sys.error(err), identity)
    val program = SecLang.compile(loaded)
    val engine = SecLang.engine(program)

    val failing_ctx_2 = RequestContext(
      method = "GET",
      uri = "/",
      headers = Map("User-Agent" -> List("Firefox/128.0")),
      query = Map("q" -> List("test")),
      body = None
    )
    val passing_ctx_1 = RequestContext(
      method = "GET",
      uri = "/health",
      headers = Map("User-Agent" -> List("curl/8.0")),
      query = Map("q" -> List("test")),
      body = None
    )
    val passing_ctx_2 = RequestContext(
      method = "GET",
      uri = "/admin",
      headers = Map("User-Agent" -> List("chrome/8.0")),
      query = Map("q" -> List("test")),
      body = None
    )

    val failing_res_2 = engine.evaluate(failing_ctx_2, phases = List(1, 2))
    val passing_res_1 = engine.evaluate(passing_ctx_1, phases = List(1, 2))
    val passing_res_2 = engine.evaluate(passing_ctx_2, phases = List(1, 2))

    assertEquals(failing_res_2.disposition, Block(403, Some("someone used firefox to access"), Some(1)))
    assertEquals(passing_res_1.disposition, Continue)
    assertEquals(passing_res_2.disposition, Continue)
  }
}
```

## Factory with presets usage

```scala

import com.cloud.apim.seclang.model.Disposition._
import com.cloud.apim.seclang.model._
import com.cloud.apim.seclang.scaladsl.SecLang

class SecLangFactoryTest extends munit.FunSuite {

  test("simple factory test") {
    val presets = Map(
      "no_firefox" -> SecLangPreset.withNoFiles("no_firefox", """
           |SecRule REQUEST_HEADERS:User-Agent "@pm firefox" \
           |   "id:00001,\
           |   phase:1,\
           |   block,\
           |   t:none,t:lowercase,\
           |   msg:'someone used firefox to access',\
           |   logdata:'someone used firefox to access',\
           |   tag:'test',\
           |   ver:'0.0.0-dev',\
           |   status:403,\
           |   severity:'CRITICAL'"
           |""".stripMargin),
      "health_check" -> SecLangPreset.withNoFiles("health_check", """
           |SecRule REQUEST_URI "@contains /health" \
           |   "id:00002,\
           |   phase:1,\
           |   pass,\
           |   t:none,t:lowercase,\
           |   msg:'someone called /health',\
           |   logdata:'someone called /health',\
           |   tag:'test',\
           |   ver:'0.0.0-dev'"
           |""".stripMargin)
    )
    val factory = SecLang.factory(presets)
    val rulesConfig = List(
      "@import_preset no_firefox",
      "@import_preset health_check",
      "SecRuleEngine On"
    )
    val failing_ctx_2 = RequestContext(
      method = "GET",
      uri = "/",
      headers = Map("User-Agent" -> List("Firefox/128.0")),
      query = Map("q" -> List("test")),
      body = None
    )
    val passing_ctx_1 = RequestContext(
      method = "GET",
      uri = "/health",
      headers = Map("User-Agent" -> List("curl/8.0")),
      query = Map("q" -> List("test")),
      body = None
    )
    val passing_ctx_2 = RequestContext(
      method = "GET",
      uri = "/admin",
      headers = Map("User-Agent" -> List("chrome/8.0")),
      query = Map("q" -> List("test")),
      body = None
    )

    val failing_res_2 = factory.evaluate(rulesConfig, failing_ctx_2, phases = List(1, 2))
    val passing_res_1 = factory.evaluate(rulesConfig, passing_ctx_1, phases = List(1, 2))
    val passing_res_2 = factory.evaluate(rulesConfig, passing_ctx_2, phases = List(1, 2))

    assertEquals(failing_res_2.disposition, Block(403, Some("someone used firefox to access"), Some(1)))
    assertEquals(passing_res_1.disposition, Continue)
    assertEquals(passing_res_2.disposition, Continue)
  }
}
```

## Installation

Add the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.cloud-apim" %% "seclang-engine" % "1.0.0"
```

The library is cross-compiled for Scala 2.12 and 2.13.

### Using Snapshots

To use snapshot versions, add the Sonatype snapshots repository:

```scala
resolvers += "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.cloud-apim" %% "seclang-engine" % "1.0.0-SNAPSHOT"
```

### Maven

For Maven projects, add to your `pom.xml`:

```xml
<dependency>
  <groupId>com.cloud-apim</groupId>
  <artifactId>seclang-engine_2.12</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle

For Gradle projects:

```gradle
implementation 'com.cloud-apim:seclang-engine_2.12:1.0.0'
```

## Build

To build the project from source:

```bash
# Clone the repository
git clone https://github.com/cloud-apim/seclang-engine.git
cd seclang-engine

# Compile
sbt compile

# Package
sbt package
```

## Testing

The project uses the official [OWASP Core Rule Set (CRS) test suite](https://github.com/coreruleset/coreruleset/tree/main/tests/regression/tests) to validate the engine implementation.

### Setup

Before running the tests, you need to download the CRS test data:

```bash
./setuptest.sh
```

This script clones the CRS repository into `test-data/coreruleset/`.

### Running tests

```bash
# Run all tests
sbt test

# Run a specific test suite
sbt "testOnly *SecLangBasicTest"
sbt "testOnly *SecLangCRSTest"

# Run a specific test
sbt "testOnly *SecLangBasicTest -- *simple*"
```

### Test status

The file `crs-tests-status.json` contains the current status of the CRS test suite, including passing/failing tests and their failure reasons.

## Missing stuff

### Missing transformations

* `replaceNulls`
* `parityEven7bit`
* `parityOdd7bit`
* `parityZero7bit`
* `sqlHexDecode`

### Missing variables

* `AUTH_TYPE`
* `FULL_REQUEST`
* `FULL_REQUEST_LENGTH`
* `HIGHEST_SEVERITY`
* `INBOUND_DATA_ERROR`
* `MODSEC_BUILD`
* `MSC_PCRE_LIMITS_EXCEEDED`
* `MULTIPART_CRLF_LF_LINES`
* `MULTIPART_FILENAME`
* `MULTIPART_NAME`
* `MULTIPART_STRICT_ERROR`
* `MULTIPART_UNMATCHED_BOUNDARY`
* `OUTBOUND_DATA_ERROR`
* `REQBODY_ERROR`
* `REQBODY_ERROR_MSG`
* `RULE`
* `SDBM_DELETE_ERROR`
* `SESSION`
* `SESSIONID`
* `URLENCODED_ERROR`
* `WEBAPPID`

### Missing operators

* `verifyCC`
* `verifyCPF`
* `verifySSN`
* `rbl`
* `rxGlobal`
* `fuzzyHash`
