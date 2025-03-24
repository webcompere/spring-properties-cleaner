# spring-properties-cleaner
Utility to clean up spring properties files.

> Note: this is WIP

## Execute

After building the application we can run it to get help:

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar --help
```

And we can run it to scan a properties file:

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar --read path/to/resources/myproperties.properties
```

or a directory of them:

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar --read path/to/resources
```

A scan will exit with code 1 if the file contains duplicates with different values.

We can fix things:

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar --action fix --read path/to/resources
```

Fixing will:

- Coalesce duplicates into a single entry - the last value provided
  - adding together all commented lines directly before each duplicate
- Remove spaces between properties and comments
- Take all non-comment and non-property lines and add them as comments at the end of the file
- Apply the chosen sort to the property lines

This will output new files to the console unless we add `--apply`

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar --action fix --apply --read path/to/resources
```

For sorting we can choose the sort mode of `sorted`, `clustered` or `none`. In `sorted`, the properties
are sorted lexically (respecting the value of numbers). In `clustered`, the original order of the file is preserved
as much as possible while also bringing up values with matching paths to be together. `none` - the default - does
nothing.

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar --action fix --sort clustered --read path/to/resources
```

If we have multiple environments then we may have properties files:

- `application.properties`
- `application-dev.properties`
- `application-prod.properties`

For this we can use `--common` to find values that are the same and bring them into the root properties file. There are
multiple modes:

- `none` - don't do it
- `full` - find identical values present in all files and bring them into the root properties file
- `consistent` - find values which are in more than one place and always the same wherever they appear
- `multiple` - find values that are in more than one place and bring them into the root properties, even if they're different in some places

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar --action fix --common full --read path/to/resources
```

## Build

```bash
# build it
./gradlew build

# produce shadow jar
./gradlew shadowJar
```

## TODO

- Identify properties that wouldn't work in YAML as part of scan - e.g. `a.b.c` and `a.b.c.d` cannot be done as YAML
- Output everything in YAML! (which will use default sort of clustered)
  - in YAML mode allow for a minimum length of property to be tree-ified if solo

## Contributing

This project is still incubating. Please feel free to raise issues with suggestions or questions. Not ready 
to receive PRs at the moment.

## License

See [MIT License](./LICENSE). Also uses [alphanumeric-comparator](https://github.com/sawano/alphanumeric-comparator?) with its own Apache 2 license.