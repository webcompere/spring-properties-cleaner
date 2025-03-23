package uk.org.webcompere.spc.processor;

import com.ibm.icu.text.Collator;
import com.ibm.icu.text.RuleBasedCollator;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.LineError;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Fixer {
    /**
     * Fix the files and write them out
     * @param propertiesFiles the files
     * @param config the configuration of the tool
     * @param writer the target writer
     * @return true unless it went wrong
     */
    public static boolean fix(List<PropertiesFile> propertiesFiles,
                              SpcArgs config,
                              Writer writer) {
        try {
            for (int i = 0; i < propertiesFiles.size(); i++) {
                fix(propertiesFiles.get(i), config);
            }

            writer.writeAll(propertiesFiles);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Apply fixes to the file
     * @param file file to fix
     * @param config configuration
     */
    static void fix(PropertiesFile file, SpcArgs config) {
        file.getDuplicates()
                .keySet()
                .forEach(file::collapseIntoLast);

        List<LineError> errors = file.extractErrors();
        file.addTrailingComments(errors.stream()
                .map(error -> "# " + error.getLine() + ": " + error.getContent())
                .collect(Collectors.toList()));

        switch (config.getSort()) {
            case sorted:
                file.sortSettings(createSort());
                break;

            case clustered:
                file.rewriteSettings(Fixer::clusterSettings);
                break;
        }
    }

    private static List<Setting> clusterSettings(List<Setting> settings) {
        int maxPath = settings.stream().mapToInt(setting -> setting.getFullPathParts().length)
                .max()
                .orElse(0);

        if (maxPath <= 1) {
            return settings;
        }

        List<Setting> currentList = new ArrayList<>(settings);

        // clustering is an evil bubble-sort - like sweeper
        for (int depth = 1; depth < maxPath; depth++) {
            List<Setting> nextList = new ArrayList<>();
            while (!currentList.isEmpty()) {
                var top = currentList.get(0);
                nextList.add(top);
                currentList.remove(0);
                nextList.addAll(removeAtDepth(depth, currentList, top));
            }
            currentList = nextList;
        }

        return currentList;
    }

    /**
     * Remove, in order, all items with a path matching this depth from the template item and return them
     * @param depth the depth to check
     * @param removeList the list to remove from
     * @param template the setting with the path list
     * @return the matching items
     */
    private static List<Setting> removeAtDepth(int depth, List<Setting> removeList, Setting template) {
        String key = getKey(template, depth);
        int index = 0;
        List<Setting> removed = new ArrayList<>();
        while (index < removeList.size()) {
            var nextSetting = removeList.get(index);
            if (key.equals(getKey(nextSetting, depth))) {
                removed.add(nextSetting);
                removeList.remove(index);
            } else {
                index++;
            }
        }

        return removed;
    }

    private static String getKey(Setting setting, int depth) {
        return Arrays.stream(setting.getFullPathParts()).limit(depth).collect(Collectors.joining("."));
    }

    private static Comparator<Object> createSort() {
        RuleBasedCollator collator = (RuleBasedCollator) Collator.getInstance(Locale.US);
        collator.setNumericCollation(true);
        collator.setStrength(Collator.PRIMARY);
        return collator;
    }
}
