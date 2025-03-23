package uk.org.webcompere.spc.cli;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SpcArgs {

    public enum Action { scan, fix };
    public enum SortMode { none, sorted, clustered };

    @Parameter(
            names = "--help",
            description = "Show usage"
    )
    private boolean help;

    @Parameter(
            names = "--read",
            description = "A properties file or directory with properties files",
            required = true
    )
    private String read;

    @Parameter(
            names = "--prefix",
            description = "When in directory mode, the prefix to read the properties files with - e.g. application (default)"
    )
    private String prefix = "application";

    @Parameter (
            names = "--action",
            description = "Action to perform, scan or fix"
    )
    private Action action = Action.scan;

    @Parameter (
            names = "--apply",
            description = "Execute fix as a dry run (false - default) to console, or write to filesystem"
    )
    private boolean apply = false;

    @Parameter (
            names = "--sort",
            description = "How to sort the keys: 'sorted' (lexical order), 'clustered' (neighbours share a path, but the" +
                    " original file order is preserved as much as possible, or 'none'"
    )
    private SortMode sort = SortMode.none;
}
