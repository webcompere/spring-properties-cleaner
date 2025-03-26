package uk.org.webcompere.spc.processor;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * Write the files to the console
 */
public class ConsoleWriter implements Writer {

    private Consumer<String> console = System.out::println;

    public ConsoleWriter() {
    }

    public ConsoleWriter(Consumer<String> console) {
        this.console = console;
    }

    @Override
    public void write(File file, List<String> lines) {
        console.accept("--- START File: " + file.getAbsoluteFile() + " ---");
        lines.forEach(console);
        console.accept("--- ENDS ---");
    }
}
