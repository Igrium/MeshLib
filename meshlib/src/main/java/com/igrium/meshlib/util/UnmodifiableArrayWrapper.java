package com.igrium.meshlib.util;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.RandomAccess;
import java.util.function.IntFunction;

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
    
    @Override
    public T[] toArray() {
        return array.clone();
    }

    @SuppressWarnings("unchecked")
    public <U extends Object> U[] toArray(U[] a) {
        return (U[]) Arrays.copyOf(array, array.length, a.getClass());
    };

    @Override
    public <U> U[] toArray(IntFunction<U[]> generator) {
        U[] target = generator.apply(array.length);
        System.arraycopy(array, 0, target, 0, array.length);
        return target;
    }
}
