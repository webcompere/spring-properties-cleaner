package uk.org.webcompere.spc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LineError {
    private int line;
    private String content;
}
