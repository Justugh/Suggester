package net.justugh.sb.util;

public class StringUtil {

    /**
     * Joins a String array starting from the specified
     * index using spaces.
     *
     * @param index The index to start from.
     * @param array The array to join.
     * @return The joined array as a single String.
     */
    public static String join(int index, String[] array) {
        if (index >= array.length)
            throw new IndexOutOfBoundsException("Specified index is greater than array length");

        String[] newArray = new String[array.length - index];
        if (array.length - index >= 0) System.arraycopy(array, index, newArray, 0, array.length - index);
        return String.join(" ", newArray);
    }

}
