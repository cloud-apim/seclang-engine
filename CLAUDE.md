# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SecLang Engine is a Scala library implementing a subset of the OWASP/ModSecurity SecLang language for the JVM. It parses, compiles, and evaluates SecLang rules, primarily designed to run the OWASP Core Rule Set (CRS) in multi-tenant environments.

Project is built on JDK 17 using SBT (Simple Build Tool) for scala, and is cross-compiled for Scala 2.12 and 2.13.

## Build Commands

```bash
# Compile
sbt compile

# Run all tests
sbt test

# Run a specific test suite
sbt "testOnly *SecLangBasicTest"
sbt "testOnly *SecLangCRSTest"

# run a specific CRS test
# change in SecLangCRSTest 
#  private val testOnly: List[(String, Int)] = List(("ruleId", testNumber))
#  //private val testOnly: List[(String, Int)] = List.empty

# Generate documentation
sbt doc

# Cross build : run the task for every scala version listed in crossScalaVersions (2.12 and 2.13)
sbt +compile
sbt +test
sbt +publishLocal

# Build for one specific scala version
sbt ++2.13.18 compile
```

## Regenerating ANTLR Parser

When modifying the grammar files in `src/main/resources/g4/*.g4`:

```bash
./gen.sh
```

This generates Java parser code in `src/main/scala/com/cloud/apim/seclang/antlr/`. The generated code is not really useful to read. DO NOT READ IT !

## Architecture

### Core Pipeline

1. **Parse** (`impl/parser.scala`) - ANTLR-based parser converts SecLang text into AST (`Configuration`).
2. **Compile** (`impl/compiler.scala`) - Transforms AST into `CompiledProgram` with phase-organized `RuleChain`s
3. **Evaluate** (`impl/engine.scala`) - Executes compiled program against `RequestContext`

### Key Types

- **`SecLang`** (`scaladsl/api.scala`) - Main entry point with `parse()`, `compile()`, `engine()`, and `factory()` methods
- **`Configuration`** (`model/model.scala`) - Parsed AST containing statements (SecRule, SecAction, SecMarker, etc.)
- **`CompiledProgram`** - Phase-indexed rules ready for execution
- **`SecLangEngine`** - Evaluates requests against compiled rules
- **`SecLangEngineFactory`** - Caches compiled programs, supports preset-based composition

### Rule Evaluation Flow

1. Rules execute in phases (1-5): request headers, request body, response headers, response body, logging
2. Variables are extracted from `RequestContext` (`impl/variables.scala`)
3. Transformations applied (`impl/transformations.scala`)
4. Operators match against transformed values (`impl/operators.scala`)
5. Actions execute on match (`impl/actions.scala`)

### Model Structure

- `SecRule` - Main rule: variables + operator + actions
- `RuleChain` - Linked rules via `chain` action
- `RequestContext` - HTTP request/response data for evaluation
- `EngineResult` - Disposition (Continue/Block) + match events

### Integration Points

- `SecLangIntegration` trait - Override for custom logging, caching, and audit
- `SecLangPreset` - Bundled rules with data files (e.g., CRS preset)

## Tests

- `test-data/coreruleset/` - Local copy of OWASP CRS for testing
- `src/test/scala/crs.scala` - CRS test suite validation. Mimic the official CRS test suite (using a local copy of the test cases).
- `test-data/crs-failing-tests` - status files for the CRS test suite. Failing tests are listed here with test case, the tested rule and the failure reason

## Conventions

- Uses Scala 2.12 idiomatic style with immutable data structures
- Code must cross-compile on Scala 2.12 and 2.13: no `mapValues` (returns a `MapView` on 2.13), no
  `Predef.conforms()` from java (use `javadsl.JavaCompat` instead), and beware of `scala.Seq` meaning
  `scala.collection.Seq` on 2.12 but `scala.collection.immutable.Seq` on 2.13
- Tests use MUnit
- Parse errors return `Either[String, Configuration]` rather than throwing exceptions
- All model types extend `AstNode` with JSON serialization via play-json
