package uk.org.webcompere.spc.processor;

import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class Processor {

    /**
     * Process the request from the CLI
     * @param request the request
     * @return <code>false</code> if to "fail" the job
     */
    public boolean execute(SpcArgs request) throws IOException {
        Path path = Path.of(request.getRead());
        File file = path.toFile();
        if (!file.exists()) {
            System.err.println(request.getRead() + " does not exist");
            return false;
        }

        if (file.isFile()) {
            return process(file, request);
        }
        return true;
    }

    boolean process(File file, SpcArgs request) throws IOException {
        PropertiesFile propertiesFile = new PropertiesFile(file.getName());
        Parser parser = new Parser(propertiesFile);
        try (var lines = Files.lines(file.toPath())) {
            lines.forEach(parser::parse);
        }

        var duplicates = propertiesFile.getDuplicates();
        if (!duplicates.isEmpty()) {
            duplicates.entrySet().forEach(entry -> {
                System.err.println(propertiesFile.getName() + ": " + entry.getKey() + " has duplicate values " +
                        entry.getValue().stream().map(val -> "L" + val.getLine() + ":'" + val.getValue() + "'")
                                .collect(Collectors.joining(",")));
            });
            return false;
        }

        return true;
    }
}
