package uk.org.webcompere.spc.processor;

import se.sawano.java.text.AlphanumericComparator;
import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Sorting {

    /**
     * Apply the chosen sort to the file
     * @param file file to sort
     * @param sort the sort type
     */
    public static void applySort(PropertiesFile file, SpcArgs.SortMode sort) {
        switch (sort) {
            case sorted:
                file.sortSettings(createSort());
                break;

            case clustered:
                file.rewriteSettings(Sorting::clusterSettings);
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

    public static Comparator<CharSequence> createSort() {
        return new AlphanumericComparator(Locale.ENGLISH);
    }
}
