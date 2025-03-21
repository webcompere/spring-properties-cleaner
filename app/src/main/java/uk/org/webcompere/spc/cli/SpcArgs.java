package uk.org.webcompere.spc.cli;

import com.beust.jcommander.Parameter;

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

    public String getReadDirectory() {
        return readDirectory;
    }

    public void setReadDirectory(String readDirectory) {
        this.readDirectory = readDirectory;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "SpcArgs{" +
                "readDirectory='" + readDirectory + '\'' +
                ", action=" + action +
                '}';
    }
}
