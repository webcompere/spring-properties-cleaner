package uk.org.webcompere.spc.model;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PropertiesFileFactory {

    /**
     * Create a test properties file for tests
     * @param name name of the file
     * @param properties properties to put in the file - key value pairs read in the order of the map
     * @return {@link PropertiesFile} for testing with
     */
    public static PropertiesFile createFile(String name, Map<String, String> properties) {
        PropertiesFile propertiesFile = new PropertiesFile(new File(name));
        AtomicInteger lineNumber = new AtomicInteger(1);
        properties.forEach(
                (key, value) -> propertiesFile.add(new Setting(lineNumber.getAndIncrement(), List.of(), key, value)));
        return propertiesFile;
    }
}
