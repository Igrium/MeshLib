package com.igrium.meshlib;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A concurrent mesh that ensures that you can't have two faces that share the
 * same vertex set.
 */
public class OverlapCheckingMesh extends AbstractConcurrentMesh {

    private static class UnorderedArrayHandle<T> {
        final T[] array;

        UnorderedArrayHandle(T[] array) {
            this.array = array;
        }

        @Override
        public int hashCode() {
            // Using strait addition utilizes the commutative property to ensure hash code
            // is order-independent.
            int code = 0;
            for (T val : array) {
                code += val.hashCode();
            }
            return code;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof UnorderedArrayHandle other) {
                return arrayEqualsUnordered(this.array, other.array);
            } else {
                return false;
            }
        }
    }

    private boolean override = false;

    public boolean isOverride() {
        return override;
    }

    public OverlapCheckingMesh setOverride(boolean override) {
        this.override = override;
        return this;
    }

    private final Map<UnorderedArrayHandle<Vertex>, Face> faces = new ConcurrentHashMap<>();

    @Override
    public boolean addFace(Face face) {
        if (override) {
            faces.put(new UnorderedArrayHandle<>(face.getVertexArray()), face);
            return true;
        } else {
            Face prev = faces.putIfAbsent(new UnorderedArrayHandle<>(face.getVertexArray()), face);
            return prev != null;
        }
    }

    @Override
    public boolean removeFace(Object face) {
        return faces.values().remove(face);
    }

    @Override
    public boolean removeAllFaces(Collection<?> faces) {
        return this.faces.values().removeAll(faces);
    }

    @Override
    public Collection<? extends Face> getFaces() {
        return Collections.unmodifiableCollection(faces.values());
    }
    
    static boolean arrayEqualsUnordered(Object[] array1, Object[] array2) {
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
