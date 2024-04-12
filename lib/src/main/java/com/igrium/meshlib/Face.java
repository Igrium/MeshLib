package com.igrium.meshlib;

import java.util.List;

import com.igrium.meshlib.math.Vector2;
import com.igrium.meshlib.math.Vector3;
import com.igrium.meshlib.util.UnmodifiableArrayWrapper;

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
            String[] groups) {
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
                groups != null ? groups : null);
    }

    public List<Vertex> getVertices() {
        return createWrapper(vertices);
    }

    public List<Vector2> getTexCoords() {
        return createWrapper(texCoords);
    }

    public List<Vector3> getNormals() {
        return createWrapper(normals);
    }

    public String getMaterial() {
        return material;
    }

    public List<String> getGroups() {
        return createWrapper(groups);
    }

    public int numSides() {
        return vertices.length;
    }

    public Face withVertices(Vertex[] vertices) {
        assertArrayLength(numSides(), vertices.length);
        return new Face(vertices.clone(), texCoords, normals, material, groups);
    }

    public Face withTexCoords(Vector2[] texCoords) {
        assertArrayLength(numSides(), texCoords.length);
        return new Face(vertices, texCoords.clone(), normals, material, groups);
    }

    public Face withNormals(Vector3[] normals) {
        assertArrayLength(numSides(), normals.length);
        return new Face(vertices, texCoords, normals.clone(), material, groups);
    }

    public Face withMaterial(String material) {
        return new Face(vertices, texCoords, normals, material, groups);
    }

    public Face withGroups(String[] groups) {
        return new Face(vertices, texCoords, normals, material, groups.clone());
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
}
