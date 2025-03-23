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

This will output new files to the consoles unless we add `--apply`

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar --action fix --apply --read path/to/resources
```


## Build

```bash
# build it
./gradlew build

# produce shadow jar
./gradlew shadowJar
```

## TODO

- Cluster/sort
- Find common properties and shuffle them between files
- Output everything in YAML!