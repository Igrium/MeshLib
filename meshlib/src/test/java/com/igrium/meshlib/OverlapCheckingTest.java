package com.igrium.meshlib;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

import org.junit.jupiter.api.RepeatedTest;

public class OverlapCheckingTest {

    @RepeatedTest(5)
    public void testArrayEquals() {
        Integer[][] arrays = genShuffledArrays(20);

        assertTrue(OverlapCheckingMesh.arrayEqualsUnordered(arrays[0], arrays[1]));
    }

    @RepeatedTest(5)
    public void testArrayLengthUnequals() {
        Integer[][] arrays = genShuffledArrays(20);
        Integer[] array2 = new Integer[24];
        System.arraycopy(arrays[1], 0, array2, 0, arrays[1].length);
        
        assertFalse(OverlapCheckingMesh.arrayEqualsUnordered(arrays[0], array2));
    }

    @RepeatedTest(5)
    public void testFirstArrayDuplicate() {
        Integer[][] arrays = genShuffledArrays(10);
        arrays[0][5] = arrays[0][9];

        assertFalse(OverlapCheckingMesh.arrayEqualsUnordered(arrays[0], arrays[1]));
    }

    @RepeatedTest(5)
    public void testSecondArrayDuplicate() {
        Integer[][] arrays = genShuffledArrays(10);
        arrays[1][5] = arrays[1][9];

        assertFalse(OverlapCheckingMesh.arrayEqualsUnordered(arrays[0], arrays[1]));
    }

    @RepeatedTest(5)
    public void testBothArraysDuplicate() {
        Integer[] array = IntStream.range(0, 10).boxed().toArray(Integer[]::new);
        Collections.shuffle(Arrays.asList(array));
        array[9] = 0;
        
        Integer[] array2 = array.clone();
        Collections.shuffle(Arrays.asList(array2));

        assertTrue(OverlapCheckingMesh.arrayEqualsUnordered(array, array2));
    }

    private Integer[][] genShuffledArrays(int length) {
        Integer[] array1 = IntStream.range(0, length).boxed().toArray(Integer[]::new);
        Collections.shuffle(Arrays.asList(array1));

        Integer[] array2 = array1.clone();
        Collections.shuffle(Arrays.asList(array2));

        return new Integer[][] { array1, array2 };
    }
}
