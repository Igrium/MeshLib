package com.igrium.meshlib;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.igrium.meshlib.ConcurrentMeshBuilder.ReferenceMap;
import com.igrium.meshlib.math.Vector2;
import com.igrium.meshlib.math.Vector3;

public class FaceBuilder {

    private final Vertex[] vertices;
    private Vector2[] texCoords;
    private Vector3[] normals;
    private String material;

    private final List<String> groups = new LinkedList<>();

    public FaceBuilder(Vertex[] vertices) {
        this.vertices = vertices;
    }

    public FaceBuilder texCoords(Vector2[] texCoords) {
        assertLengthEquals(vertices.length, texCoords);
        this.texCoords = texCoords;
        return this;
    }
    
    public FaceBuilder normals(Vector3[] normals) {
        assertLengthEquals(vertices.length, normals);
        this.normals = normals;
        return this;
    }

    public FaceBuilder material(String material) {
        this.material = material;
        return this;
    }

    public FaceBuilder group(String group) {
        groups.add(group);
        return this;
    }

    public FaceBuilder groups(String... groups) {
        for (String str : groups) {
            this.groups.add(str);
        }
        return this;
    }

    public FaceBuilder groups(Collection<? extends String> groups) {
        this.groups.addAll(groups);
        return this;
    }

    @SuppressWarnings("unchecked")
    public Face build(ConcurrentMeshBuilder mesh, boolean reuseVerts) {
        IndexedReference<Vertex>[] vertRefs = new IndexedReference[vertices.length];
        ReferenceMap<Vertex> vertMap = mesh.getVertices();
    
        for (int i = 0; i < vertices.length; i++) {
            Vertex vertex = vertices[i];
            vertRefs[i] = reuseVerts ? vertMap.getOrAdd(vertex) : vertMap.add(vertex);
        }

        IndexedReference<Vector2>[] texCoordRefs = null;
        if (texCoords != null) {
            texCoordRefs = new IndexedReference[texCoords.length];
            ReferenceMap<Vector2> texCoordMap = mesh.getTexCoords();
    
            for (int i = 0; i < texCoords.length; i++) {
                texCoordRefs[i] = texCoordMap.getOrAdd(texCoords[i]);
            }
        }

        IndexedReference<Vector3>[] normalRefs = null;
        if (normals != null) {
            normalRefs = new IndexedReference[normals.length];
            ReferenceMap<Vector3> normalMap = mesh.getNormals();

            for (int i = 0; i < normals.length; i++) {
                normalRefs[i] = normalMap.getOrAdd(normals[i]);
            }
        }

        Face face = Face.create(vertRefs, texCoordRefs, normalRefs, material, groups);
        mesh.putFace(face);
        return face;
    }

    public Face build(ConcurrentMeshBuilder mesh) {
        return build(mesh, true);
    }

    private static void assertLengthEquals(int expected, Object[] array) {
        if (array != null && array.length != expected) {
            throw new IllegalStateException(
                    "Improper array length. Expected %d, but got %d.".formatted(expected, array.length));
        }
    }
    
}