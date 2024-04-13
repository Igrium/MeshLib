package com.igrium.meshlib.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * A set that maintains insertion order and can be queried based on index.
 * Elements cannot be removed from the set; only added.
 */
public class IndexedSet<T> extends AbstractSet<T> {
    
    private final LinkedHashMap<T, Integer> map = new LinkedHashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Iterator<T> iterator() {
        return new SetIterator();
    }

    @Override
    public boolean add(T e) {
        return map.putIfAbsent(e, map.size()) == null;
    }

    /**
     * Return the index of an element in the set.
     * @param e Element to get.
     * @return Index of the element. <code>-1</code> if the element is not in the set.
     */
    public int indexOf(Object e) {
        Integer index = map.get(e);
        return index != null ? index : -1;
    }

    /**
     * Add an element to the set if it does not exist already.
     * 
     * @param val Element to add.
     * @return The index of the element. If the element was already in the set, the
     *         existing index. If not, the new index.
     */
    public int addIfAbsent(T val) {
        int size = map.size();
        Integer index = map.putIfAbsent(val, size);
        return index != null ? index : size;
    }
    
    // Wrapper iterator disables remove function.
    private class SetIterator implements Iterator<T> {
        private final Iterator<T> baseIterator = map.keySet().iterator();

        @Override
        public boolean hasNext() {
            return baseIterator.hasNext();
        }

        @Override
        public T next() {
            return baseIterator.next();
        }
    }
}
