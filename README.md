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
java -jar app/build/libs/spring-properties-cleaner-1.0.jar --read path/to/myproperties.properties
```

A scan will exit with code 1 if the file contains duplicates.

## Build

```bash
# build it
./gradlew build

# produce shadow jar
./gradlew shadowJar
```

