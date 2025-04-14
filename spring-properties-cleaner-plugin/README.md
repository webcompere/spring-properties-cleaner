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