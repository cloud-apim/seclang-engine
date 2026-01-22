# Cloud APIM - SecLang Engine

<p align="center">
  <b>An open-source, enterprise-grade, high-performance Web Application Firewall (WAF) library for the JVM, made to protect your webapps.</b>
</p>

<p align="center">
  <a href="https://github.com/cloud-apim/seclang-engine/actions/workflows/ci.yaml"><img src="https://github.com/cloud-apim/seclang-engine/actions/workflows/ci.yaml/badge.svg" alt="CI"></a>
  <a href="https://central.sonatype.com/artifact/com.cloud-apim/seclang-engine_2.12"><img src="https://img.shields.io/maven-central/v/com.cloud-apim/seclang-engine_2.12?color=blue" alt="Maven Central"></a>
  <a href="https://github.com/cloud-apim/seclang-engine/blob/main/LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License"></a>
  <img src="https://img.shields.io/badge/Scala-2.12-red.svg" alt="Scala 2.12">
  <a href="https://coreruleset.org/"><img src="https://img.shields.io/badge/CRS%20v4-100%25-brightgreen" alt="CRS v4 Compatibility"></a>
</p>

SecLang Engine is written in Scala and implements the [ModSecurity SecLang](https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29) 
rule language, providing full compatibility with the [OWASP Core Rule Set (CRS) v4](https://coreruleset.org/).

## Key Features

- **CRS Compatibility** - Runs the OWASP Core Rule Set v4 out of the box, protecting your applications against SQL Injection, Cross-Site Scripting (XSS), Remote Code Execution, and other threats from the OWASP Top Ten.
- **Multi-Tenant by Design** - Built from the ground up for multi-tenant environments, allowing different rule configurations per tenant with isolated execution contexts and shared rule presets.
- **JVM Native** - Runs natively on the JVM with Scala 2.12 support. Easily integrates with existing Java/Scala applications, API gateways, and reverse proxies.
- **High Performance** - Optimized for high-throughput scenarios with compiled rule programs, regex caching, and efficient variable resolution. Handles thousands of requests per second with minimal latency overhead.
- **Library-First** - Designed as an embeddable library, not a standalone server. Integrate WAF capabilities directly into your application, gateway, or proxy with a simple API.
- **Extensible** - Customize logging, caching, and integration points through the `SecLangIntegration` trait. Compose rule presets and configurations dynamically at runtime.
- **Battle-Tested** - Passes 100% of the official CRS test suite, ensuring reliable protection against real-world attack patterns.

## Status

This project is a work in progress. 

We are currently [passing **100%**](./crs-tests-status.json) of the [CRS test suite](https://github.com/coreruleset/coreruleset/tree/main/tests/regression/tests)

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

## Simple usage (Java)

```java
import com.cloud.apim.seclang.javadsl.*;
import com.cloud.apim.seclang.model.CompiledProgram;

import java.util.Arrays;
import java.util.HashMap;

public class SecLangExample {

    public static void main(String[] args) {
        String rules = """
            SecRule REQUEST_HEADERS:User-Agent "@pm firefox" \\
                "id:00001,\\
                phase:1,\\
                block,\\
                t:none,t:lowercase,\\
                msg:'someone used firefox to access',\\
                logdata:'someone used firefox to access',\\
                tag:'test',\\
                ver:'0.0.0-dev',\\
                status:403,\\
                severity:'CRITICAL'"

            SecRule REQUEST_URI "@contains /health" \\
                "id:00002,\\
                phase:1,\\
                pass,\\
                t:none,t:lowercase,\\
                msg:'someone called /health',\\
                logdata:'someone called /health',\\
                tag:'test',\\
                ver:'0.0.0-dev'"

            SecRuleEngine On
            """;

        // Parse and compile rules
        SecLang.ParseResult parseResult = SecLang.parse(rules);
        if (parseResult.isError()) {
            System.err.println("Parse error: " + parseResult.getError());
            return;
        }

        CompiledProgram program = SecLang.compile(parseResult.getConfiguration());

        // Create engine (with default config, no files, default integration)
        JSecLangEngine engine = SecLang.engine(program);

        // Or with custom configuration:
        // JSecLangEngine engine = SecLang.engine(
        //     program,
        //     JSecLangEngineConfig.defaultConfig(),
        //     new HashMap<>(),
        //     JSecLangIntegration.defaultIntegration()
        // );

        // Build request contexts
        JRequestContext failingCtx = JRequestContext.builder()
            .method("GET")
            .uri("/")
            .header("User-Agent", "Firefox/128.0")
            .queryParam("q", "test")
            .build();

        JRequestContext passingCtx1 = JRequestContext.builder()
            .method("GET")
            .uri("/health")
            .header("User-Agent", "curl/8.0")
            .queryParam("q", "test")
            .build();

        JRequestContext passingCtx2 = JRequestContext.builder()
            .method("GET")
            .uri("/admin")
            .header("User-Agent", "chrome/8.0")
            .queryParam("q", "test")
            .build();

        // Evaluate requests against rules
        JEngineResult failingRes = engine.evaluate(failingCtx, Arrays.asList(1, 2));
        JEngineResult passingRes1 = engine.evaluate(passingCtx1, Arrays.asList(1, 2));
        JEngineResult passingRes2 = engine.evaluate(passingCtx2, Arrays.asList(1, 2));

        // Check results
        if (failingRes.isBlocked()) {
            System.out.println("Request blocked!");
            System.out.println("  Status: " + failingRes.getDisposition().getStatus());
            System.out.println("  Message: " + failingRes.getDisposition().getMessage().orElse("N/A"));
            System.out.println("  Rule ID: " + failingRes.getDisposition().getRuleId().orElse(0));
        }

        assert failingRes.isBlocked();
        assert failingRes.getDisposition().getStatus() == 403;
        assert passingRes1.isContinue();
        assert passingRes2.isContinue();
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
libraryDependencies += "com.cloud-apim" %% "seclang-engine" % "1.4.0"
```

The library is compiled for Scala 2.12

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
  <version>1.4.0</version>
</dependency>
```

### Gradle

For Gradle projects:

```gradle
implementation 'com.cloud-apim:seclang-engine_2.12:1.4.0'
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
