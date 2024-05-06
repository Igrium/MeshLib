package com.igrium.meshlib;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.igrium.meshlib.ConcurrentMeshBuilder.ReferenceMap;
import com.igrium.meshlib.math.Vector2;
import com.igrium.meshlib.math.Vector3;

/**
 * A helper class to build faces in a concurrent mesh builder. The mesh builder
 * will not be modified until <code>build</code> is called.
 * <p>
 * This face builder is not thread-safe, but multiple can be used on the same mesh builder concurrently.
 */
public class FaceBuilder {

    private final Vertex[] vertices;
    private Vector2[] texCoords;
    private Vector3[] normals;
    private String material;

    private final List<String> groups = new LinkedList<>();

    /**
     * Create a face builder.
     * @param vertices Vertices in the face.
     */
    public FaceBuilder(Vertex[] vertices) {
        this.vertices = vertices;
    }

    /**
     * Set the texture coordinates of this face.
     * 
     * @param texCoords Texture coordinates to use.
     *                  <code>null</code> to remove texture coordinates.
     * @return <code>this</code>
     * @throws IllegalArgumentException If the supplied array is of an
     *                                  incorrect length.
     */
    public FaceBuilder texCoords(Vector2[] texCoords) throws IllegalArgumentException {
        assertLengthEquals(vertices.length, texCoords);
        this.texCoords = texCoords;
        return this;
    }

    /**
     * Set the normals of this face.
     * 
     * @param normals Normals to use. <code>null</code> to remove normals.
     * @return <code>this</code>
     * @throws IllegalArgumentException If the supplied array is of an
     *                                  incorrect length.
     */
    public FaceBuilder normals(Vector3[] normals) throws IllegalArgumentException {
        assertLengthEquals(vertices.length, normals);
        this.normals = normals;
        return this;
    }

    /**
     * Set the material of this face.
     * @param material Material name to use. <code>null</code> to remove the material reference.
     * @return <code>this</code>
     */
    public FaceBuilder material(String material) {
        this.material = material;
        return this;
    }
    
    /**
     * Add a group to this face.
     * @param group Group to add.
     * @return <code>this</code>
     */
    public FaceBuilder group(String group) {
        this.groups.add(group);
        return this;
    }

    /**
     * Add an array of groups to this face.
     * @param groups Groups to add.
     * @return <code>this</code>
     */
    public FaceBuilder groups(String... groups) {
        this.groups.addAll(Arrays.asList(groups));
        return this;
    }

    /**
     * Add a collection of groups to this face.
     * @param groups Groups to add.
     * @return <code>this</code>
     */
    public FaceBuilder groups(Collection<? extends String> groups) {
        this.groups.addAll(groups);
        return this;
    }

    /**
     * Get the groups this face will be in.
     * @return A mutable list of groups.
     */
    public List<String> getGroups() {
        return groups;
    }

    /**
     * Build this face and add it to a mesh builder.
     * @param mesh Mesh builder to use.
     * @param reuseVerts If <code>true</code>, the mesh builder will attempt to re-use existing vertices rather than making new ones.
     * @return The built face.
     */
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

    private static void assertLengthEquals(int expected, Object[] array) throws IllegalArgumentException {
        if (array != null && array.length != expected) {
            throw new IllegalArgumentException(
                    "Improper array length. Expected %d, but got %d.".formatted(expected, array.length));
        }
    }
    
}