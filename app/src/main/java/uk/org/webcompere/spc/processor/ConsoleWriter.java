package uk.org.webcompere.spc.processor;

import java.io.File;
import java.util.List;

/**
 * Write the files to the console
 */
public class ConsoleWriter implements Writer {

    @Override
    public void write(File file, List<String> lines) {
        System.out.println("--- START File: " + file.getAbsoluteFile() + " ---");
        lines.forEach(System.out::println);
        System.out.println("--- ENDS ---");
    }
}
