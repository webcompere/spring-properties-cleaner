package uk.org.webcompere.spc.model;

import java.util.Optional;
import java.util.function.Function;

/**
 * Will return a section break or nothing depending on the change of keys
 */
public class SectionBreaker implements Function<String, Optional<String>> {
    private String lastPrefix;

    @Override
    public Optional<String> apply(String nextKey) {
        if (lastPrefix == null) {
            lastPrefix = prefixOf(nextKey);
            return Optional.empty();
        }

        String nextPrefix = prefixOf(nextKey);
        if (lastPrefix.equals(nextPrefix)) {
            return Optional.empty();
        }
        lastPrefix = nextPrefix;
        return Optional.of("");
    }

    private String prefixOf(String key) {
        return key.split("\\.")[0];
    }
}
