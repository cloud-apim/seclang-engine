# Scala SecLang Engine

## 📌 Project Overview

This repository implements a **standalone Scala library** for parsing, representing, and evaluating **SecLang** (the OWASP ModSecurity rule syntax, https://coraza.io/docs/seclang/syntax/). The goal is to enable:

* **Parsing SecLang rule files** into an internal AST or IR.
* **Evaluating SecLang logic** in a SecLang engine for testing and tooling.
* **Chaining rules, disabling rules, overrides, control flow** semantics roughly matching ModSecurity semantics.

Agents should treat this project as a reusable Scala library with no JVM app server dependency. Outputs must work as input to any SecLang engine.

## 📦 Project Goals

* Build a Scala library capable of parsing SecLang grammar.
* Design a **rule AST with semantic annotations** so rules, actions, and operators are represented cleanly in Scala.
* Implement a **SecLang evaluation engine** that applies a given rule set to test inputs.
* Provide utility functions to chain rules, resolve overrides, and handle rule disables/activations.
* Include **unit and integration tests** to verify parsing and evaluation behavior.

## 🚀 Build & Development

### Prerequisites

* Java 11+ or later
* Scala 2.12 (with 2.13 compatibility)
* sbt

### Build

```bash
# compile
sbt compile

# run tests
sbt test

# generate docs
sbt doc
```

### IDE Setup

* Open in your favorite Scala-aware IDE (IntelliJ recommended).
* Ensure sbt auto-import is enabled.

## 🧠 Code Style & Conventions

This project adheres to the following conventions:

* Scala 2.12 idiomatic functional style where reasonable.
* Prefer **immutable data structures** for AST and rule models.
* Use **explicit error types** rather than exceptions for parse errors.
* Tests written with MUnit.
* Follow **standard Scala formatting** (scalafmt recommended).

## 🧪 Tests & Test Data

Your tests must:

* Validate SecLang grammar coverage (positive and negative).
* Check engine behavior on chained rules and overrides.
* Include examples of realistic rulesets in `test/resources`.

Recommended commands:

```bash
# run all tests
sbt test

# run single suite
sbt "testOnly *ParserSpec"
```

Fix any test failures before merging changes.

## 🪄 Rule Parsing Requirements

* Support full SecLang grammar including operators, transformations, actions, variables, rule metadata, etc.
* Produce a Scala AST representing rule metadata, conditions, operators, and actions.
* Must handle chaining and control flow semantics correctly (e.g., chained rules, skip, disrupt actions).
* Engine should allow enabling/disabling specific rules programmatically.

## 🧩 SecLang Engine Behavior

Agents must implement an engine which:

* Loads parsed rules into an internal structure.
* Applies rules to input contexts (e.g., simulated HTTP transaction).
* Outputs matches, actions taken, and disrupt actions (block, audit, etc.).
* Respects rule priorities, overrides, and disable/enable flags.

## 📜 Required File Structure

```
.
├── src/
│   ├── main/scala/…
│   │   ├── parser/        # SecLang parser implementation
│   │   ├── model/         # AST/IR representations
│   │   ├── engine/        # Evaluation engine
│   │   └── util/          # Helpers & utilities
│   └── test/scala/…       # ScalaTest/MUnit suites
├── test/resources/        # Sample SecLang files
├── AGENTS.md              # This agent guidance
├── build.sbt              # sbt build definition
└── README.md              # Human-oriented overview
```

## 🎯 Agent Instructions

* **When generating code**, obey the parser & engine contracts above.
* **Do not assume external dependencies** like a web server unless required.
* **Design extensible APIs** to allow future adaptations to similar rule engines.
* **Include compile-time checks** where possible.
* **Keep documentation in sync** with implementation (update README and docs).

## 📬 Pull Request Guidelines

* Include test coverage for the implemented feature.
* Describe semantic behavior when rules interact (e.g., chaining, disables).
* Validate that engine evaluation matches expected SecLang behavior.

## ⚠️ Security & Safety

* Treat untrusted SecLang files defensively in parsers (avoid crashable patterns).
* Cases of ambiguous grammar should be flagged and tested.

## About SecLang

SecLang is an HTTP event engine that  

- observes requests/responses by phase
- applies rules in order
- modifies shared state (variables)
- can disable, chain, skip, or transform other rules

phases are the following: 1 (request headers), 2 (request body), 3 (response headers), 4 (response body), 5 (logging): used for audit logging and custom actions

examples of basic rule:

```
SecRule REQUEST_URI "@streq /test" "id:1,phase:1,deny,status:403"
```

```
SecRule ARGS "@rx select\s+.*from" \
  "id:1001,phase:2,deny,status:403,msg:'SQL injection'"
```

More complex example with chaining:

```
SecRule ARGS "@rx eval\(" "id:2001,phase:2,deny,status:403,msg:'Possible code injection'"
  "chain"
  SecRule REQUEST_HEADERS:User-Agent "@rx curl" "t:none,nolog,skip:1"
```

More complex example with skip and disrupt actions:

```
SecRule REQUEST_URI "@streq /admin" "id:3001,phase:1,skip:2"
SecRule REQUEST_HEADERS:Authorization "!@rx ^Bearer " "id:3002,phase:1,deny,status:401,msg:'Missing or invalid auth'"
SecRule REQUEST_HEADERS:Authorization "!@rx ^Bearer\s+secret123" "id:3003,phase:1,deny,status:403,msg:'Invalid token'"
```

full syntax reference: https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29