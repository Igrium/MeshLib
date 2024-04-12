package com.igrium.meshlib;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.igrium.meshlib.math.Vector3;

/**
 * A framework to build a mesh across multiple threads, re-using vertices when
 * possible. This class contains a registry of "tracked" vertices. Faces do
 * <em>not</em> need to use tracked vertices exclusively, however it will
 * improve file size to do so.
 */
public class ConcurrentMeshBuilder {

    private final Map<Vector3, Vertex> vertices = new ConcurrentHashMap<>();

    public Vertex getVertex(Vector3 pos) {
        return vertices.get(pos);
    }

    public Vertex getOrCreateVertex(Vector3 pos) {
        return vertices.computeIfAbsent(pos, vec -> new Vertex(vec));
    }

    public Vertex getOrCreate(Vector3 pos, Vector3 color) {
        return vertices.computeIfAbsent(pos, vec -> new Vertex(vec, color));
    }

    public final Vertex getOrCreateVertex(float x, float y, float z) {
        return getOrCreateVertex(new Vector3(x, y, z));
    }

    public void putVertex(Vertex vertex) {
        vertices.put(vertex.pos(), vertex);
    }

    public Map<Vector3, Vertex> getVertices() {
        return Collections.unmodifiableMap(vertices);
    }

    private final Set<Face> faces = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void putFace(Face face) {
        this.faces.add(face);
    }

    public boolean removeFace(Face face) {
        return faces.remove(face);
    }

    public Set<Face> getFaces() {
        return faces;
    }
    
}
