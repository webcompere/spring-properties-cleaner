package uk.org.webcompere.spc.processor.sorting;

import java.math.BigInteger;
import lombok.Getter;

/**
 * Implements a basic alpha-numeric sorting algorithm where any number within the string is tokenized into a Double for comparison and
 * numbers are treated as "later" than strings.
 */
public class AlphaNumericSort {

    @Getter
    private static class Chunker {
        private final CharSequence source;
        private int index = 0;

        private BigInteger number;
        private String string;

        public Chunker(CharSequence source) {
            this.source = source;
            next();
        }

        public void next() {
            number = null;
            string = null;
            if (index < source.length()) {
                if (isNumeric(source.charAt(index))) {
                    buildNumber();
                } else {
                    buildString();
                }
            }
        }

        private void buildString() {
            StringBuilder newString = new StringBuilder();
            newString.append(source.charAt(index++));
            while (index < source.length() && !isNumeric(source.charAt(index))) {
                newString.append(source.charAt(index++));
            }
            string = newString.toString();
        }

        private void buildNumber() {
            StringBuilder newNum = new StringBuilder();
            newNum.append(source.charAt(index++));
            while (index < source.length()) {
                char next = source.charAt(index);
                if (isNumeric(next)) {
                    newNum.append(next);
                    index++;
                } else {
                    break;
                }
            }
            number = new BigInteger(newNum.toString());
        }

        private boolean isNumeric(char c) {
            return c >= '0' && c <= '9';
        }

        public boolean done() {
            return number == null && string == null;
        }

        public boolean isNumber() {
            return number != null;
        }
    }

    /**
     * Comparator function
     * @param s1 first string to compare
     * @param s2 second string to compare
     * @return the comparison of two strings - this will go in blocks which are of type string or number
     */
    public static int alphaNumericSort(CharSequence s1, CharSequence s2) {
        Chunker chunker1 = new Chunker(s1);
        Chunker chunker2 = new Chunker(s2);

        while (!chunker1.done() && !chunker2.done()) {
            if (chunker1.isNumber() != chunker2.isNumber()) {
                return chunker1.isNumber() ? 1 : -1;
            }

            if (chunker1.isNumber()) {
                if (!chunker1.getNumber().equals(chunker2.getNumber())) {
                    return chunker1.getNumber().compareTo(chunker2.getNumber());
                }
            } else {
                int comparison = CharSequence.compare(chunker1.getString(), chunker2.getString());
                if (comparison != 0) {
                    return comparison;
                }
            }

            chunker1.next();
            chunker2.next();
        }

        if (chunker1.done() != chunker2.done()) {
            return chunker1.done() ? -1 : 1;
        }

        // both empty
        return 0;
    }
}
