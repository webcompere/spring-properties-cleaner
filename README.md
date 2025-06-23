# Spring Properties Cleaner 

![Maven](https://img.shields.io/badge/apachemaven-C71A36.svg?logo=apachemaven&logoColor=white)
[![Maven Central Version](https://img.shields.io/maven-central/v/uk.org.webcompere/spring-properties-cleaner-plugin)](https://central.sonatype.com/artifact/uk.org.webcompere/spring-properties-cleaner-plugin)
![JavaFX](https://img.shields.io/badge/java-11-white.svg?logo=javafx&logoColor=white)
[![Build](https://github.com/webcompere/spring-properties-cleaner/actions/workflows/build-actions.yml/badge.svg?branch=main)](https://github.com/webcompere/spring-properties-cleaner/actions/workflows/build-actions.yml)
[![codecov](https://codecov.io/gh/webcompere/spring-properties-cleaner/graph/badge.svg?token=OlKMD7tq48)](https://codecov.io/gh/webcompere/spring-properties-cleaner)

## Overview

Utility to clean up spring properties files.

> Tutorial for the Maven Plugin available [over on Baeldung](https://www.baeldung.com/spring-properties-cleaner)

This is available as:

- [Maven Plugin](./spring-properties-cleaner-plugin/README.md)
- [Command Line Utility](./app/README.md)

## Use Case

As `application-<profile>.properties` files grow, with multiple Spring Profiles, a few anti-patterns may occur:

- Duplicate properties within the same file
- Duplicate properties across the profiles, which could have been in a root `application.properties`
- The order of the properties becomes random
- We may wish to switch to the more readable `.yml` format

This toolset attempts to address these challenges. It can be used in a couple of modes:

- Use the CLI to do some tidying
- Use the maven plugin to ensure things don't get worse
  - or  just use the maven plugin to tidy things up

### Improvement Order and Principles

When using a tool like this to clean things up, it's probably useful to work
in small increments which are easy to code review.

As such, the tool will respect comment lines, assuming that comments are stuck
to the property declaration which follows them.

Similarly, most modes of fixing files will only change what's necessary, leaving
easy to review diffs. This includes sorting the file using `clustering`, where
only properties with common prefixes, that are split across the file, are moved.

It may be desirable to refactor the properties all the way to YML, but to
make it easier to review each step, here's a recommended fix order:

- Eliminate duplicates
- Apply `clustered` sort (optional, but useful)
- Inline regular prefixes to properties to make a common file easier to build (optional)
- Extract a common properties file using `full` mode to get properties that are duplicated the same everywhere
- Extract common properties using `consistent` mode to bring in more properties - checking that you're happy to have all of these in the common properties file
- Extract common properties using `multiple` to bring in any common properties that appear often, though not always - again checking the impact
- Apply a `sorted` sort to put everything in lexical (with numeric awareness) order (optional)
- Make whitespace changes if required

The scanning tool cannot scan `.yml` files, so converting everything to new `.yml` files
is a final option you might take using the CLI. If you wish to keep
running everything in properties files, then the maven plugin can enforce
the settings you've used to fix the file when scanning, and can apply fresh
fixes at your scan level if something has gone wrong in the current version.

## Contributing

Please feel free to raise issues with suggestions or questions. Please give examples of your properties files. Feel free to fork
and raise any PRs that might be useful.

## License

See [MIT License](./LICENSE).
