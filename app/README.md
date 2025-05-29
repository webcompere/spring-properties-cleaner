# Spring Properties Cleaner CLI

## Execute

After building the application we can run it to get help:

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar --help
```
### CI/CD

If using `maven` as a build tool, then use the [Maven Plugin](../spring-properties-cleaner-plugin/README.md)
to scan that no errors have crept in. Or use this CLI as part of a build pipeline, where it will 
exit with non-zero if there are any errors.

### Scanning

We can run the tool to scan a properties file:

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar \
   --read path/to/resources/myproperties.properties
```

or a directory of them:

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar \ 
   --read path/to/resources
```

A scan will exit with code 1 if the file contains errors. The scan will also warn of telescoping values which would prevent a conversion to YML.

> Note: providing the settings for fixing into scan mode will cause
> the scan to decide how fixing might look, and return an error
> if the fixing preferences would require the files to be changed

### Fixing/Rewriting

We can fix things:

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar \ 
   --action fix \
   --read path/to/resources
```

Fixing will:

- Coalesce duplicates into a single entry - the last value provided
    - adding together all commented lines directly before each duplicate
- Take all non-comment and non-property lines and add them as comments at the end of the file
- Apply the chosen sort to the property lines
- Modify sort order and whitespace
- Modify key values with `inlinePrefix` mode

This will output new files to the console unless we add `--apply`

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar \ 
    --action fix \
    --apply \
    --read path/to/resources
```

### Sorting

For sorting we can choose the sort mode of `sorted`, `clustered` or `none`. In `sorted`, the properties
are sorted lexically (respecting the value of numbers). In `clustered`, the original order of the file is preserved
as much as possible while also bringing up values with matching paths to be together. `none` - the default - does
nothing.

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar \ 
    --action fix \
    --sort clustered \
    --read path/to/resources
```

### Whitespace

For whitespace we can use a whitespace mode to remove or preserve blank lines in the file. The
default is `preserve`, meaning whitespace is kept as is. We can use `remove` to remove it
and `section` to remove whitespace from the original file, and insert extra whitespace when
one key doesn't share the same first-level prefix as another. This makes sense when also
using a sort:

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar \ 
    --action fix \
    --sort clustered \
    --whitespace section \
    --read path/to/resources
```

Here, we get a line break between properties when we move from `spring.` to `redis.`:

```properties
spring.jpa=true
spring.cache=true

redis.port=9000
```

### Inline Prefix

This is intended to help with moving to common properties. Imagine
we have a file like this:

```properties
# application-dev.properties
server.address=my.server.com
server.path1=http://${server.address}/path/one
server.path2=http://${server.address}/path/two
```

If in some of our profiles, the paths start `https` and some
they start `http`, then the common properties file cannot
take the `server.path`s out into `application.properties`.

If we could modify the file to become:

```properties
# application-dev.properties
server.address=http://my.server.com
server.path1=${server.address}/path/one
server.path2=${server.address}/path/two
```

Then the http prefix will be part of `server.address` and the
paths will be standard regardless of http scheme.

This can be achieved using the `inlinePrefix` option, providing
a regular expression as the prefix matcher.

```bash
java -jar app/build/libs/spring-properties-cleaner-1.0.jar \ 
    --action fix \
    --sort clustered \
    --inlinePrefix https?:// \
    --read path/to/resources
```

### Extracting Common Properties

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
java -jar app/build/libs/spring-properties-cleaner-1.0.jar \
    --action fix \
    --common full \
    --read path/to/resources
```

### YML Output

We can also provide `--yml` to get the _fixed_ files to be written in YML format. This will use the
provided sort mode, defaulting to `clustered` if a sort is not provided.

> NOTE: YML Cannot be generated if properties are telescoping. E.g. `server` and `server.port` cannot both
> be in the same YAML. This will be warned on a scan.

## Build

```bash
# build it
./gradlew build

# produce shadow jar
./gradlew shadowJar
```
