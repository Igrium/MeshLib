package com.igrium.meshlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.igrium.meshlib.math.Vector2;
import com.igrium.meshlib.math.Vector3;

/**
 * A face within a concurrent mesh. This implementation is mutable. Although
 * mutability methods on this face are not thread-safe, they can be added to the
 * mesh builder in parallel.
 */
public class Face implements Comparable<Face> {

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
        this.groups = groups != null ? new ArrayList<>(groups) : null;
    }

    /**
     * Create a face instance. All non-null arrays must be of the same length.
     * 
     * @param vertices  The vertices to use.
     * @param texCoords The texture coordinates to use. May be <code>null</code>.
     * @param normals   The normals to use. May be <code>null</code>.
     * @param material  The material to use. May be <code>null</code>.
     * @param groups    A collection of all OBJ groups that this face is in. If
     *                  <code>null</code>, no groups are assigned.
     * @return The face instance.
     * @throws NullPointerException     If <code>vertices == null</code>
     * @throws IllegalArgumentException If one of the reference arrays if of the
     *                                  wrong length.
     */
    public static Face create(IndexedReference<Vertex>[] vertices, IndexedReference<Vector2>[] texCoords,
            IndexedReference<Vector3>[] normals, String material, Collection<? extends String> groups)
            throws NullPointerException, IllegalArgumentException {
        if (vertices == null) {
            throw new NullPointerException("Vertices may not be null");
        }
        if (!areLengthsEqual(vertices, texCoords, normals)) {
            throw new IllegalArgumentException("vertices, texCoords, and normals must be of the same length.");
        }

        return new Face(vertices, texCoords, normals, material, groups);
    }

    /**
     * Get the vertices in the face.
     * @return Vertex array.
     */
    public IndexedReference<Vertex>[] getVertices() {
        return vertices;
    }

    /**
     * Get the texture coordinates in the face.
     * @return TexCoord array. May be <code>null</code>.
     */
    public IndexedReference<Vector2>[] getTexCoords() {
        return texCoords;
    }

    /**
     * Get the normals in the face.
     * @return Normal array. May be <code>null</code>.
     */
    public IndexedReference<Vector3>[] getNormals() {
        return normals;
    }

    /**
     * Get the material this face uses.
     * @return Material name. May be <code>null</code>.
     */
    public String getMaterial() {
        return material;
    }

    /**
     * Set the material this face uses.
     * @param material Material name. May be <code>null</code>.
     */
    public void setMaterial(String material) {
        this.material = material;
    }

    /**
     * Get the groups this face is in.
     * @return A mutable list of all this face's groups.
     */
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

    @Override
    public int compareTo(Face other) {
        int val = 0;
        if (this.material != null && other.material != null) {
            val = this.material.compareTo(other.material) * 0xFFFF;
        }

        String[] thisGroups = this.groups.toArray(String[]::new);
        Arrays.sort(thisGroups);

        String[] otherGroups = other.groups.toArray(String[]::new);
        Arrays.sort(otherGroups);

        return val + Arrays.compare(thisGroups, otherGroups);
    }
}
