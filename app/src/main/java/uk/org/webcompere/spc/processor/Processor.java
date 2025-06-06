package uk.org.webcompere.spc.processor;

import static uk.org.webcompere.spc.processor.Scanner.scanForIssues;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.parser.Parser;
import uk.org.webcompere.spc.processor.writing.ConsoleWriter;
import uk.org.webcompere.spc.processor.writing.FileWriter;
import uk.org.webcompere.spc.processor.writing.StoringWriter;
import uk.org.webcompere.spc.processor.writing.Writer;

public class Processor {

    private Consumer<String> errorLog = System.err::println;
    private Consumer<String> infoLog = System.out::println;

    public Processor() {}

    public Processor(Consumer<String> errorLog, Consumer<String> infoLog) {
        this.errorLog = errorLog;
        this.infoLog = infoLog;
    }

    /**
     * Process the request from the CLI
     * @param request the request
     * @return <code>false</code> if to "fail" the job
     * @throws IOException if there's any problem reading or writing files
     */
    public boolean execute(SpcArgs request) throws IOException {
        Path path = Path.of(request.getRead());
        File file = path.toFile();
        if (!file.exists()) {
            errorLog.accept(request.getRead() + " does not exist");
            return false;
        }

        if (file.isFile()) {
            return process(file, request);
        }
        return processDirectory(file, request);
    }

    boolean process(File file, SpcArgs request) throws IOException {
        return processFiles(request, List.of(file));
    }

    boolean processDirectory(File directory, SpcArgs request) throws IOException {
        String prefix = request.getPrefix();

        List<File> toProcess = Arrays.stream(directory.listFiles())
                .filter(file -> file.getName().toLowerCase().endsWith(".properties"))
                .filter(file -> file.getName().toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());

        return processFiles(request, toProcess);
    }

    private boolean processFiles(SpcArgs request, List<File> toProcess) throws IOException {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        try {
            List<PropertiesFile> loaded = new ArrayList<>();
            List<ScanResult> results = new ArrayList<>();
            for (File file : toProcess) {
                var propertiesFile = load(file);
                loaded.add(propertiesFile);
                var result = scanForIssues(propertiesFile);
                results.add(result);
                errors.addAll(result.getErrors());
                warnings.addAll(result.getWarnings());
            }

            if (request.getAction() == SpcArgs.Action.fix) {
                return performFix(request, results, loaded);
            }

            checkImpactIfFixed(request, loaded, errors);

            return errors.isEmpty();
        } finally {
            errors.forEach(errorLog);
            warnings.forEach(infoLog);
        }
    }

    private void checkImpactIfFixed(SpcArgs request, List<PropertiesFile> loaded, List<String> errors) {
        // cannot do this for yaml
        if (request.isYml()) {
            return;
        }

        StoringWriter storingWriter = new StoringWriter();
        Fixer.fix(loaded, request, storingWriter);

        List<File> filesThatChanged = storingWriter.whichAreDifferent();
        filesThatChanged.forEach(
                file -> errors.add("File '" + file.getName() + "' does not meet standard - have you run fix?"));
    }

    private boolean performFix(SpcArgs request, List<ScanResult> results, List<PropertiesFile> loaded) {
        if (request.isYml() && results.stream().anyMatch(ScanResult::isTelescopingProperties)) {
            errorLog.accept("Cannot convert to YML owing to telescoping properties");
            return false;
        }
        return Fixer.fix(loaded, request, writer(request));
    }

    private PropertiesFile load(File file) throws IOException {
        PropertiesFile propertiesFile = new PropertiesFile(file);
        Parser parser = new Parser(propertiesFile);
        try (var lines = Files.lines(file.toPath())) {
            lines.forEach(parser::parse);
        }
        return propertiesFile;
    }

    /**
     * Create the appropriate writer from the config
     * @param request the request from the command line
     * @return either a real writer or a console writer
     */
    public Writer writer(SpcArgs request) {
        if (request.isApply()) {
            return new FileWriter();
        }
        return new ConsoleWriter(infoLog);
    }
}
