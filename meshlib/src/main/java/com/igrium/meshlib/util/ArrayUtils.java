package com.igrium.meshlib.util;

import java.util.Objects;

public class ArrayUtils {
    /**
     * Check if two arrays contain the same elements, regardless of their order.
     * This implementation prioritizes speed over complexity.
     * 
     * @param array1 First array to check.
     * @param array2 Second array to check.
     * @return If they contain the same elements.
     */
    public static boolean arrayEqualsUnordered(Object[] array1, Object[] array2) {
        if (array1.length != array2.length)
            return false;

        boolean[] consumed = new boolean[array1.length];

        for (int i = 0; i < array1.length; i++) {
            Object val = array1[i];
            boolean found = false;
            for (int j = 0; j < array2.length; j++) {
                if (consumed[j])
                    continue;

                if (Objects.equals(val, array2[j])) {
                    found = true;
                    consumed[j] = true;
                    break;
                }
            }
            if (!found)
                return false;
        }

        return true;
    }
}
