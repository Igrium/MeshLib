package com.igrium.meshlib.util;

import java.util.AbstractList;
import java.util.RandomAccess;

public class UnmodifiableArrayWrapper<T> extends AbstractList<T> implements RandomAccess {

    private final T[] array;

    public UnmodifiableArrayWrapper(T[] array) {
        this.array = array;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public T get(int index) {
        return array[index];
    }
    
}
