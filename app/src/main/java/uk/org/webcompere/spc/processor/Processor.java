package uk.org.webcompere.spc.processor;

import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static uk.org.webcompere.spc.processor.Scanner.scanForIssues;

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
            return process(file);
        }
        return processDirectory(file, request.getPrefix());
    }

    boolean process(File file) throws IOException {
        var propertiesFile = load(file);

        var scanResult = scanForIssues(propertiesFile);;

        scanResult.getErrors().forEach(System.err::println);
        scanResult.getWarnings().forEach(System.out::println);

        return scanResult.getErrors().isEmpty();
    }

    boolean processDirectory(File directory, String prefix) throws IOException {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        List<File> toProcess = Arrays.stream(directory.listFiles())
                .filter(file -> file.getName().toLowerCase().endsWith(".properties"))
                .filter(file -> file.getName().toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());

        List<PropertiesFile> loaded = new ArrayList<>();
        for (File file: toProcess) {
            var propertiesFile = load(file);
            loaded.add(propertiesFile);
            var result = scanForIssues(propertiesFile);
            errors.addAll(result.getErrors());
            warnings.addAll(result.getWarnings());
        }

        errors.forEach(System.err::println);
        warnings.forEach(System.out::println);

        return errors.isEmpty();
    }

    private PropertiesFile load(File file) throws IOException {
        PropertiesFile propertiesFile = new PropertiesFile(file);
        Parser parser = new Parser(propertiesFile);
        try (var lines = Files.lines(file.toPath())) {
            lines.forEach(parser::parse);
        }
        return propertiesFile;
    }
}
