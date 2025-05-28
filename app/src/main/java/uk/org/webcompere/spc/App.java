package uk.org.webcompere.spc;

import static java.lang.System.exit;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.processor.Processor;

public class App {

    /**
     * Entry point using JCommander to read command line args
     * @param args the system arguments
     */
    public static void main(String[] args) {
        SpcArgs arguments = new SpcArgs();
        JCommander cliParser = JCommander.newBuilder()
                .programName("Spring properties cleaner")
                .addObject(arguments)
                .build();
        try {
            cliParser.parse(args);

            if (arguments.isHelp()) {
                cliParser.usage();
            } else if (!new Processor().execute(arguments)) {
                exit(1);
            }
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            cliParser.usage();
            exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            exit(1);
        }
    }
}
