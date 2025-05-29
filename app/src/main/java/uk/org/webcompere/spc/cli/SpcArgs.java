package uk.org.webcompere.spc.cli;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SpcArgs {

    public enum Action {
        scan,
        fix
    }

    public enum SortMode {
        none,
        sorted,
        clustered
    }

    public enum CommonPropertiesMode {
        none,
        full,
        consistent,
        multiple
    }

    public enum WhiteSpaceMode {
        preserve,
        remove,
        section
    }

    @Parameter(names = "--help", description = "Show usage", help = true)
    private boolean help;

    @Parameter(names = "--read", description = "A properties file or directory with properties files", required = true)
    private String read;

    @Parameter(
            names = "--prefix",
            description =
                    "When in directory mode, the prefix to read the properties files with - e.g. application (default)")
    private String prefix = "application";

    @Parameter(names = "--action", description = "Action to perform, scan or fix")
    private Action action = Action.scan;

    @Parameter(
            names = "--sort",
            description =
                    "How to sort the keys: 'sorted' (lexical order), 'clustered' (neighbours share a path, but the"
                            + " original file order is preserved as much as possible, or 'none'")
    private SortMode sort = SortMode.none;

    @Parameter(
            names = "--whitespace",
            description = "How to handle whitespace between keys when outputting to .properties file: 'preserve' "
                    + " (default) means leave it as it is in the original file,"
                    + " 'remove' means to take whitespace out of the file, and 'section' means to remove whitespace but "
                    + "insert it between keys with a different . separated prefix.")
    private WhiteSpaceMode whiteSpaceMode = WhiteSpaceMode.preserve;

    @Parameter(
            names = "--inlinePrefix",
            description = "Scans all the properties in the file to see if, when used in other properties, "
                    + "they are prefixed by something matching the regular expression provided. If they are, "
                    + "and it's the same in every case, then the prefix will be inlined into the value.")
    private String inlinePrefix;

    @Parameter(
            names = "--common",
            description = "Find common properties. In scan mode, this will warn, in fix mode, this will create them. "
                    + "There are degrees of commonality. `full` - means must have same value everywhere to be common, "
                    + "`consistent` - means must be consistent everywhere it's found, but doesn't have to be everywhere, "
                    + "`multiple` - means must have been found consistently in multiple locations, but can be inconsistent, "
                    + "`none` (default) disables the mode. Common properties will never overwrite any existing value in "
                    + "the shared configuration file.")
    private CommonPropertiesMode commonProperties = CommonPropertiesMode.none;

    @Parameter(
            names = "--apply",
            description =
                    "Apply to filesystem (when specified), otherwise it's a dry run - output to console (default)")
    private boolean apply = false;

    @Parameter(
            names = "--yml",
            description = "Convert the file(s) to YML when action is 'fix'. This will use a default sort mode of "
                    + "'clustered', though this can be overridden with '--sort sorted' if required")
    private boolean yml = false;
}
