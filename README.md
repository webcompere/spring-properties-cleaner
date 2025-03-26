# Spring Properties Cleaner 

[![Build](https://github.com/webcompere/spring-properties-cleaner/actions/workflows/build-actions.yml/badge.svg?branch=main)](https://github.com/webcompere/spring-properties-cleaner/actions/workflows/build-actions.yml)
[![codecov](https://codecov.io/gh/webcompere/spring-properties-cleaner/graph/badge.svg?token=OlKMD7tq48)](https://codecov.io/gh/webcompere/spring-properties-cleaner)

> Note: this is WIP

## Overview

Utility to clean up spring properties files.

This is available as:

- [Maven Plugin](./spring-properties-cleaner-plugin/README.md)
- [Command Line Utility](./app/README.md)


## TODO

- Create mvn plugin for both scanning and fixing
- In YAML mode allow for a minimum length of property to be tree-ified if solo
- In YAML handle telescoping properties like this:
  ```yml
  server: myServer
  server.port: 8080
  ```

## Contributing

This project is still incubating. Please feel free to raise issues with suggestions or questions. Not ready 
to receive PRs at the moment.

## License

See [MIT License](./LICENSE). Also uses [alphanumeric-comparator](https://github.com/sawano/alphanumeric-comparator?) with its own Apache 2 license.