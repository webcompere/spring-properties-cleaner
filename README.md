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

We can fix things like duplicates:

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar --action fix --read path/to/resources
```

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

- Replace error lines with comments in the footer
- Find common properties and shuffle them between files
- Output everything in YAML!