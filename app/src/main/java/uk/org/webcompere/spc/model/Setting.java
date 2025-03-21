package uk.org.webcompere.spc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Setting {
    private List<String> precedingComments;
    private String fullPath;
    private String value;
}
