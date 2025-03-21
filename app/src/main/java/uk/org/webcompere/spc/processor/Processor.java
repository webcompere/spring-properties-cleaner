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
            return process(file, request);
        }
        return processDirectory(file, request);
    }

    boolean process(File file, SpcArgs request) throws IOException {
        var propertiesFile = load(file);

        var scanResult = scanForIssues(propertiesFile);;

        scanResult.getErrors().forEach(System.err::println);
        scanResult.getWarnings().forEach(System.out::println);

        if (request.getAction() == SpcArgs.Action.fix) {
            return Fixer.fix(List.of(propertiesFile), List.of(scanResult), writer(request));
        }

        return scanResult.getErrors().isEmpty();
    }

    boolean processDirectory(File directory, SpcArgs request) throws IOException {
        String prefix = request.getPrefix();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        List<File> toProcess = Arrays.stream(directory.listFiles())
                .filter(file -> file.getName().toLowerCase().endsWith(".properties"))
                .filter(file -> file.getName().toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());

        List<PropertiesFile> loaded = new ArrayList<>();
        List<ScanResult> results = new ArrayList<>();
        for (File file: toProcess) {
            var propertiesFile = load(file);
            loaded.add(propertiesFile);
            var result = scanForIssues(propertiesFile);
            results.add(result);
            errors.addAll(result.getErrors());
            warnings.addAll(result.getWarnings());
        }

        errors.forEach(System.err::println);
        warnings.forEach(System.out::println);

        if (request.getAction() == SpcArgs.Action.fix) {
            return Fixer.fix(loaded, results, writer(request));
        }

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

    public static Writer writer(SpcArgs request) {
        if (request.isApply()) {
            return new FileWriter();
        }
        return new ConsoleWriter();
    }
}
