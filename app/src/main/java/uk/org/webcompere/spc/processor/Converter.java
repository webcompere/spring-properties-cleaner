package uk.org.webcompere.spc.processor;

import uk.org.webcompere.spc.cli.SpcArgs;
import uk.org.webcompere.spc.model.PropertiesFile;
import uk.org.webcompere.spc.model.Setting;
import uk.org.webcompere.spc.parser.Lines;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static uk.org.webcompere.spc.parser.Lines.streamPairs;

public class Converter {
    /**
     * Convert a file into the lines to serialize
     * @param file the file
     * @param sortMode the sort mode the file has already been sorted with
     * @param isYml to yml?
     * @return a list of lines to write for the file
     */
    public static List<String> toLines(PropertiesFile file, SpcArgs.SortMode sortMode, boolean isYml) {
        if (!isYml) {
            return file.toLines();
        }

        return toYmlStream(file, sortMode).collect(toList());
    }

    /**
     * What is the output filename
     * @param source the source file
     * @param isYml whether we're converting to yml
     * @return the output {@link File}
     */
    public static File targetFile(File source, boolean isYml) {
        if (!isYml) {
            return source;
        }

        // this is an algorithm that wouldn't _like_ "application.properties.properties" but
        // why have you got that as a filename?
        return new File(source.getParent(), source.getName()
                .toLowerCase().replaceAll(".properties", ".yml"));
    }

    private static Stream<String> toYmlStream(PropertiesFile file, SpcArgs.SortMode sortMode) {
        if (sortMode == SpcArgs.SortMode.none) {
            Sorting.applySort(file, SpcArgs.SortMode.clustered);
        }

        Setting blank = new Setting(0, List.of(), "", "");
        return toYmlStream(Stream.concat(Stream.of(blank), file.getSettings().stream()));
    }

    private static Stream<String> toYmlStream(Stream<Setting> settingsWithHeader) {
        return streamPairs(settingsWithHeader.collect(toList()))
                .flatMap(Converter::nextBlock);
    }

    private static Stream<String> nextBlock(Lines.Pair<Setting> previousAndNext) {
        String[] previousLine = previousAndNext.getFirst().getFullPathParts();
        String[] thisLine = previousAndNext.getSecond().getFullPathParts();
        Setting toRender = previousAndNext.getSecond();

        int thisLineLast = thisLine.length - 1;

        int commonRoot = getCommonRoot(previousLine, thisLine);

        String thisLineIndent = "  ".repeat(thisLineLast);

        return Stream.of(
                // the lines of YML path leading up to this setting
                IntStream.range(commonRoot, thisLineLast)
                    .mapToObj(i -> "  ".repeat(i) + thisLine[i] + ":"),

                // the comments at this indent before the value
                toRender.getPrecedingComments().stream()
                        .map(comment -> thisLineIndent + comment),

                // the actual field at this point
                Stream.of(thisLineIndent + thisLine[thisLineLast] + ": " + previousAndNext.getSecond().getValue()))

                // converted into a single stream of lines
                .flatMap(Function.identity());
    }

    private static int getCommonRoot(String[] previousLine, String[] thisLine) {
        int commonRoot = 0;
        while (commonRoot < previousLine.length &&
                commonRoot < thisLine.length &&
                previousLine[commonRoot].equals(thisLine[commonRoot])
        ) {
            commonRoot++;
        }
        return commonRoot;
    }
}
