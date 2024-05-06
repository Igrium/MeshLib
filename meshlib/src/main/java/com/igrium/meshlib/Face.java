package com.igrium.meshlib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.igrium.meshlib.math.Vector2;
import com.igrium.meshlib.math.Vector3;

public class Face {


    private final IndexedReference<Vertex>[] vertices;

    private final IndexedReference<Vector2>[] texCoords;
    
    private final IndexedReference<Vector3>[] normals;

    private String material;
    
    private final List<String> groups;

    private Face(IndexedReference<Vertex>[] vertices,
            IndexedReference<Vector2>[] texCoords, IndexedReference<Vector3>[] normals, String material,
            Collection<? extends String> groups) {
        this.vertices = vertices;
        this.texCoords = texCoords;
        this.normals = normals;
        this.material = material;
        this.groups = new ArrayList<>(groups);
    }

    public static Face create(IndexedReference<Vertex>[] vertices,
            IndexedReference<Vector2>[] texCoords, IndexedReference<Vector3>[] normals, String material,
            Collection<? extends String> groups) {
        if (vertices == null) {
            throw new NullPointerException("Vertices may not be null");
        }
        if (!areLengthsEqual(vertices, texCoords, normals)) {
            throw new IllegalArgumentException("vertices, texCoords, and normals must be of the same length.");
        }

        return new Face(vertices, texCoords, normals, material, groups);
    }

    public IndexedReference<Vertex>[] getVertices() {
        return vertices;
    }

    public IndexedReference<Vector2>[] getTexCoords() {
        return texCoords;
    }

    public IndexedReference<Vector3>[] getNormals() {
        return normals;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public List<String> getGroups() {
        return groups;
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

}
