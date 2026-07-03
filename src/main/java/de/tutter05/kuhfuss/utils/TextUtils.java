package de.tutter05.kuhfuss.utils;

public class TextUtils {

    /**
     * Returns whether the specified string is numeric
     * @param string string to check
     * @return true if string is a number else false
     */
    private static boolean isNumber(final String string) {
        return string.matches("^[+-]?\\d+$");
    }

    /**
     * Requires the specified string to be a number
     * @param string string to check
     * @return checked string
     * @throws NumberFormatException if the string is not a number
     */
    public static String requireNumericString(final String string) {
        if(!isNumber(string)) {
            throw new NumberFormatException("string is not a number");
        }
        return string;
    }

}
