package uk.org.webcompere.spc.processor;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScanResult {
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    private List<String> duplicateKeys = new ArrayList<>();

    public void addError(String error) {
        errors.add(error);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }
}
