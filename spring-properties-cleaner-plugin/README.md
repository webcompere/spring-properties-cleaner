# spring-properties-cleaner-plugin

Add the scanner to the POM file like this:

```xml
<plugin>
  <groupId>uk.org.webcompere</groupId>
  <artifactId>spring-properties-cleaner-plugin</artifactId>
  <version>1.0.0</version>
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
  </configuration>
```

> See the [main README](../README.md) for how these options work

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