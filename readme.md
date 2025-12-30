# Cloud APIM Scala SecLang Engine

a partial implementation of the OWASP/ModSecurity [SecLang](https://coraza.io/docs/seclang/syntax/) engine in Scala made to run CRS

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
- [ ] Implement configuration options 
  - [ ] Support json
  - [ ] Support local fs
  - [ ] Support remote fs (HTTP/HTTPS)
  - [ ] Support lists of locations
  - [ ] Support splitted definitions with cache (cached CRS + cached Global + cached user specific config)

## Unimplemented

```shell
unimplemented statement EngineConfigDirective
unimplemented variable: REQBODY_PROCESSOR
unimplemented variable: UNIQUE_ID
unimplemented variable: REQUEST_LINE
unimplemented variable: REMOTE_ADDR
unknown operator: Negated(Within(%{tx.allowed_methods}))
unimplemented operator: pmFromFile
unimplemented variable: REQUEST_PROTOCOL
unimplemented variable: REQUEST_FILENAME
unimplemented variable: REQUEST_HEADERS_NAMES
unimplemented variable: REQUEST_URI_RAW
unimplemented variable: REQUEST_BASENAME
```