package uk.org.webcompere.spc.processor.sorting;

import lombok.Getter;

import java.util.Comparator;

/**
 * Implements a basic alpha-numeric sorting algorithm where any number within the string is tokenized into a Double for comparison and
 * numbers are treated as "later" than strings.
 */
public class AlphaNumericSort {

    public static Comparator<CharSequence> createSort() {
        return AlphaNumericSort::alphaSort;
    }

    @Getter
    private static class Chunker {
        private CharSequence source;
        private int index = 0;

        private Double number;
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
            boolean hadDecimal = false;
            StringBuilder newNum = new StringBuilder();
            newNum.append(source.charAt(index++));
            while (index < source.length()) {
                char next = source.charAt(index);
                if (isNumeric(next)) {
                    newNum.append(next);
                    index++;
                } else if ('.' == next && !hadDecimal && index < source.length() - 1 && isNumeric(source.charAt(index + 1))) {
                    hadDecimal = true;
                    newNum.append(next);
                    index++;
                } else {
                    break;
                }
            }
            number = Double.parseDouble(newNum.toString());
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

    private static int alphaSort(CharSequence s1, CharSequence s2) {
        Chunker chunker1 = new Chunker(s1);
        Chunker chunker2 = new Chunker(s2);

        while (!chunker1.done() && !chunker2.done()) {
            if (chunker1.isNumber() != chunker2.isNumber()) {
                return chunker1.isNumber() ? 1 : -1;
            }

            if (chunker1.isNumber()) {
                if (!chunker1.getNumber().equals(chunker2.getNumber())) {
                    int comparison = Double.compare(chunker1.getNumber(), chunker2.getNumber());
                    if (comparison != 0) {
                        return comparison;
                    }
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
