# spring-properties-cleaner-plugin

Add the scanner to the POM file like this:

```xml
<plugin>
  <groupId>uk.org.webcompere</groupId>
  <artifactId>spring-properties-cleaner-plugin</artifactId>
  <version>1.0.3</version>
  <executions>
    <execution>
	  <goals>
	    <goal>scan</goal>
	  </goals>
    </execution>
  </executions>
</plugin>
```

It will automatically fail your build if there are any duplicate properties in the properties files.

For configuration, add properties:

```xml
  <configuration>
    <!-- Default: none, Options: clustered, sorted -->
    <sort>clustered</sort>

    <!-- Default: none, Options: consistent, multiple, full -->
    <common>consistent</common>
    
    <!-- Default: preserve, Options: remove, section -->
    <whitespace>preserve</whitespace>
    
    <!-- If we want to inline into a property a prefix that matches the regex that always appears before it -->
    <inlinePrefix>https?://</inlinePrefix>
  </configuration>
```

| Configuration | Option       | How it works                                                                                                                                         |
|---------------|--------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| `sort`        | `none`       | No sorting applied                                                                                                                                   |
| `sort`        | `sorted`     | Alpha-numeric sorting is applied; it handles numbers within the name and sorts them numerically, rather than lexically                               |
| `sort`        | `clustered`  | While preserving the sort of the keys as much as possible, any keys which share a prefix are pulled up to join their family members                  |
| `common` | `none`       | No `application.properties` file is created                                                                                                          |
| `common` | `full`       | Any key/value which is present in all files with the same value should be promoted to `application.properties`                                       |
| `common` | `consistent` | When the key/value always has the same value where it appears, then it should be promoted to `application.properties`, even if it's not in all files |
| `common` | `multiple`   | Where a key/value appears in multiple files, then the most commonly used value should be in `application.properties`                                 |
| `whitespace` | `preserve`   | Do not touch any vertical whitespace                                                                                                                 |
| `whitespace` | `remove`     | Remove vertical whitespace between keys |
| `whitespace` | `section`    | Insert a single line of whitespace between keys where the first level prefix is different |
| `inlinePrefix` | \<regex>     | Provide a regular expression that indicates what the expected eligible prefix before a key might be. If this prefix is the same for all times the key appears, then it's inlined into the key |

## Goals

To fix the files, according to the current configuration:

```bash
mvn spring-properties-cleaner:fix
```

To just scan the files:

```bash
mvn spring-properties-cleaner:scan
```

It's recommended to put the scan goal into the `pom.xml` as above, running `fix` on demand. 

The scan goal will fail if the `fix` goal would apply any changes.

## Local Testing

Run the `publishToMavenLocal` task to get version `1.0.0` deployed into local maven repository.