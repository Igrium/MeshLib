package com.igrium.meshlib;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.igrium.meshlib.math.Vector2;
import com.igrium.meshlib.math.Vector3;
import com.igrium.meshlib.util.UnmodifiableArrayWrapper;

/**
 * A thread-safe, immutable implementation of a face. Due to the sheer number of
 * these in a mesh, this implementation is designed to have the smallest memory
 * footprint possible. The underlying data is stored as a series of fixed
 * arrays; therefore, all of the interfacing methods calculate the relevent data
 * at runtime.
 */
public final class Face {
    private final Vertex[] vertices;

    private final Vector2[] texCoords;

    private final Vector3[] normals;
    
    private final String material;

    private final String[] groups;

    private Face(Vertex[] vertices, Vector2[] texCoords, Vector3[] normals, String material, String[] groups) {
        this.vertices = vertices;
        this.texCoords = texCoords;
        this.normals = normals;
        this.material = material;
        this.groups = groups;
    }

    public static Face create(Vertex[] vertices, Vector2[] texCoords, Vector3[] normals, String material,
            Collection<? extends String> groups) {
        if (vertices == null) {
            throw new NullPointerException("Vertices may not be null!");
        }

        if (!areLengthsEqual(vertices, texCoords, normals)) {
            throw new IllegalArgumentException("vertices, texCoords, and normals must be of the same length.");
        }


        return new Face(vertices.clone(),
                texCoords != null ? texCoords.clone() : null,
                normals != null ? normals.clone() : null,
                material,
                groups != null ? new HashSet<>(groups).toArray(String[]::new) : null);
    }

    public List<Vertex> getVertices() {
        return createWrapper(vertices);
    }

    public int hashVertices() {
        return Arrays.hashCode(vertices);
    }

    public boolean vertsEqual(Vertex... vertices) {
        return Arrays.equals(this.vertices, vertices);
    }

    public boolean hasTexCoords() {
        return texCoords != null;
    }

    public List<Vector2> getTexCoords() {
        return createWrapper(texCoords);
    }

    public int hashTexCoords() {
        return texCoords != null ? Arrays.hashCode(texCoords) : null;
    }

    public boolean texCoordsEqual(Vector2... texCoords) {
        return Arrays.equals(this.texCoords, texCoords);
    }

    public boolean hasNormals() {
        return normals != null;
    }

    public List<Vector3> getNormals() {
        return createWrapper(normals);
    }

    public int hashNormals() {
        return normals != null ? Arrays.hashCode(normals) : 0;
    }

    public boolean normalsEqual(Vector3... normals) {
        return Arrays.equals(this.normals, normals);
    }

    public String getMaterial() {
        return material;
    }

    public boolean hasGroups() {
        return groups != null;
    }

    public Collection<String> getGroups() {
        return createWrapper(groups);
    }
    
    public int hashGroups() {
        if (groups == null)
            return 0;
        int hash = 0;
        for (String group : groups) {
            if (group != null)
                hash += group.hashCode();
        }
        return hash;
    }

    public boolean groupsEqual(String... groups) {
        return arrayEqualsUnordered(this.groups, groups);
    }

    public int numSides() {
        return vertices.length;
    }

    public Face withVertices(Vertex[] vertices) {
        if (vertices == null) {
            throw new NullPointerException("Vertices may not be null!");
        }
        assertArrayLength(numSides(), vertices.length);
        return new Face(vertices.clone(), texCoords, normals, material, groups);
    }

    public Face withTexCoords(Vector2[] texCoords) {
        assertArrayLength(numSides(), texCoords);
        return new Face(vertices, texCoords.clone(), normals, material, groups);
    }

    public Face withNormals(Vector3[] normals) {
        assertArrayLength(numSides(), normals);
        return new Face(vertices, texCoords, normals.clone(), material, groups);
    }

    public Face withMaterial(String material) {
        return new Face(vertices, texCoords, normals, material, groups);
    }

    public Face withGroups(Collection<? extends String> groups) {
        if (groups != null) {
            return new Face(vertices, texCoords, normals, material, new HashSet<>(groups).toArray(String[]::new));
        } else {
            return new Face(vertices, texCoords, normals, material, null);
        }
    }
    
    private static <T> List<T> createWrapper(T[] array) {
        return array != null ? new UnmodifiableArrayWrapper<>(array) : null;
    }

    private static boolean areLengthsEqual(Object[]... arrays) {
        if (arrays.length == 0)
            return true;

        int length = arrays[0].length;
        for (var array : arrays) {
            if (array != null && array.length != length)
                return false;
        }
        return true;
    }

    private static void assertArrayLength(int expected, int actual) {
        if (actual != expected) {
            throw new IllegalArgumentException("Improper array length: " + actual + ". Expected " + expected);
        }
    }

    private static void assertArrayLength(int expected, Object[] actual) {
        if (actual != null)
            assertArrayLength(actual.length, actual);
    }

    private static <T> boolean arrayEqualsUnordered(T[] array1, T[] array2) {
        if (array1 == array2) {
            return true;
        }

        if (array1 == null || array2 == null) {
            return false;
        }

        if (array1.length == 0 || array2.length == 0) {
            return array1.length == array2.length;
        }

        if (array1.length == 1 && array2.length == 1) {
            return array1[0].equals(array2[0]);
        }

        Set<T> set1 = new HashSet<>();
        Set<T> set2 = new HashSet<>();

        for (T val : array1) {
            set1.add(val);
        }
        for (T val : array2) {
            set2.add(val);
        }

        return set1.equals(set2);
    }

    // @Override
    // public boolean equals(Object obj) {
    //     if (obj instanceof Face other) {
    //         return Arrays.equals(this.vertices, other.vertices)
    //                 && Arrays.equals(this.texCoords, other.texCoords)
    //                 && Arrays.equals(this.normals, other.normals)
    //                 && this.material.equals(other.material)
    //                 && Arrays.equals(this.groups, other.groups);
    //     } else {
    //         return false;
    //     }
    // }
    
}
