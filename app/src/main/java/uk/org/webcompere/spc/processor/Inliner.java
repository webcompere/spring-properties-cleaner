package uk.org.webcompere.spc.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Can scan a collection of key value pairs looking for when each use of
 * a particular key is always prefixed with something matching the regex. If so
 * it proposes all consumers should lose that prefix and the key should gain it
 */
public class Inliner {
    private Pattern inliningPattern;
    private Pattern hasPlaceholderPattern;
    private String propertyName;

    public Inliner(String propertyName, String prefixRegex) {
        this.propertyName = propertyName;
        this.inliningPattern = Pattern.compile("^(" + prefixRegex + ")(\\$\\{" + Pattern.quote(propertyName) + "}.*)");
        this.hasPlaceholderPattern = Pattern.compile("^.*(\\$\\{" + Pattern.quote(propertyName) + "}.*)");
    }

    public Map<String, String> scanForInlining(Map<String, String> items) {
        String prefixFound = null;

        Map<String, String> fixups = new HashMap<>();
        for (Map.Entry<String, String> entry : items.entrySet()) {
            Matcher usesOurPlaceholder = hasPlaceholderPattern.matcher(entry.getValue());
            if (!usesOurPlaceholder.matches()) {
                continue;
            }

            // the placeholder is used, does it match the inlining
            Matcher inliningMatcher = inliningPattern.matcher(entry.getValue());
            if (!inliningMatcher.matches()) {
                // fixup isn't possible - we have a non compliant value
                return Map.of();
            }

            // what's the prefix?
            String prefix = inliningMatcher.group(1);
            if (prefixFound == null) {
                prefixFound = prefix;
                fixups.put(propertyName, prefix + items.get(propertyName));
            } else if (!prefixFound.equals(prefix)) {
                // inconsistent prefixing
                return Map.of();
            }

            // store the correct fixup for this key
            fixups.put(entry.getKey(), inliningMatcher.group(2));
        }
        return fixups;
    }
}
