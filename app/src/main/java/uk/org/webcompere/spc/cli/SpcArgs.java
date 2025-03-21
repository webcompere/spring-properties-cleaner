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

    @Parameter(
            names = "--readDirectory",
            description = "Directory with the properties files in",
            required = true
    )
    private String readDirectory;

    @Parameter (
            names = "--action",
            description = "Action to performance, scan or fix",
            required = false
    )
    private Action action = Action.scan;
}
