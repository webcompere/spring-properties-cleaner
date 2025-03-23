package uk.org.webcompere.spc.model;

import lombok.Data;

import java.util.List;

@Data
public class Setting {
    private static final String DELIMITER = "\\.";
    private int line;
    private List<String> precedingComments;
    private String fullPath;
    private String[] fullPathParts;
    private String value;

    public Setting(int line, List<String> precedingComments, String fullPath, String value) {
        this.line = line;
        this.precedingComments = precedingComments;
        this.fullPath = fullPath;
        this.value = value;
        this.fullPathParts = fullPath.split(DELIMITER);
    }
}
