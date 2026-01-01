# Cloud APIM SecLang Engine

A Scala-based engine implementation of a subset of the [OWASP/ModSecurity SecLang language](https://github.com/owasp-modsecurity/ModSecurity/wiki/Reference-Manual-%28v3.x%29) that can run on the JVM.
Built to efficiently execute the [OWASP Core Rule Set (CRS)](https://coreruleset.org/) with user based customizations in highly concurrent and multi-tenant environments.

## TODO

- [ ] Implement missing variables
- [ ] Implement missing operators
- [ ] Implement missing statements
- [ ] Implement all actions besides blocking/passing ones
- [ ] Implement transformation functions
- [ ] Implement phase processing logic
- [ ] Implement rule chaining and skip logic
- [ ] Implement fake file system access (for `xFromFile` operators like `ipMatchFromFile`, `pmFromFile`, etc.)
- [ ] Implement macro expansion
- [ ] Implement transaction storage
- [ ] Integration mechanism (logger, http fetch, fs read/write, env, etc)
- [ ] Implement configuration options 
  - [ ] Support json
  - [ ] Support local fs
  - [ ] Support remote fs (HTTP/HTTPS)
  - [ ] Support lists of locations
  - [ ] Support splitted definitions with cache (cached CRS + cached Global + cached user specific config)
- [ ] parsing of ! in variables
- [ ] variable count
- [ ] variable key as regex
- [ ] implicit rx operator ? (not in crs actually)
- [ ] negating operators
- [ ] run actions

## Unimplemented for CRS

```shell
unimplemented statement DefaultAction
unimplemented statement DefaultAction
unimplemented operator: detectXSS
unimplemented operator: detectSQLi
```