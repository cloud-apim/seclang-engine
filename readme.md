# Cloud APIM SecLang Engine

A Scala-based engine implementation of a subset of the [OWASP/ModSecurity SecLang language](https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29) that can run on the JVM.
Built to efficiently execute the [OWASP Core Rule Set (CRS)](https://coreruleset.org/) with user based customizations in highly concurrent and multi-tenant environments.

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

## TODO

- [-] Implement missing variables
- [-] Implement missing operators
- [-] Implement missing statements
- [-] Implement all actions besides blocking/passing ones
- [-] Implement transformation functions
- [x] Implement phase processing logic
- [x] Implement rule chaining and skip logic
- [x] Implement fake file system access (for `xFromFile` operators like `ipMatchFromFile`, `pmFromFile`, etc.)
- [-] Implement macro expansion
- [-] Implement transaction storage
- [ ] Integration mechanism (logger, http fetch, fs read/write, env, etc)
- [ ] Implement configuration options 
  - [ ] Support json
  - [ ] Support local fs
  - [ ] Support remote fs (HTTP/HTTPS)
  - [ ] Support lists of locations
  - [ ] Support splitted definitions with cache (cached CRS + cached Global + cached user specific config)
- [ ] parsing of ! in variables
- [x] variable count
- [ ] variable key as regex
- [ ] implicit rx operator ? (not in crs, starting with ^)
- [x] negating operators
- [x] run actions

## Unimplemented for CRS

```shell
unimplemented statement DefaultAction
unimplemented statement DefaultAction
unimplemented operator: detectXSS
unimplemented operator: detectSQLi
```