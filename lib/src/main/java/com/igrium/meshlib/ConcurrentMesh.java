package com.igrium.meshlib;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.igrium.meshlib.math.Vector2;
import com.igrium.meshlib.math.Vector3;
import com.igrium.meshlib.util.IndexedSet;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjFaces;
import de.javagl.obj.Objs;

/**
 * A framework to build a mesh across multiple threads, re-using vertices when
 * possible. This class contains a registry of "tracked" vertices. Faces do
 * <em>not</em> need to use tracked vertices exclusively, however it will
 * improve file size to do so.
 */
public class ConcurrentMesh {

    private final Map<Vector3, Vertex> vertices = new ConcurrentHashMap<>();

    public Vertex getVert(Vector3 pos) {
        return vertices.get(pos);
    }

    public Vertex putVert(Vector3 pos) {
        return vertices.computeIfAbsent(pos, vec -> new Vertex(vec));
    }

    public Vertex putVert(Vector3 pos, Vector3 color) {
        return vertices.computeIfAbsent(pos, vec -> new Vertex(vec, color));
    }

    public final Vertex putVert(float x, float y, float z) {
        return putVert(new Vector3(x, y, z));
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
    
    /**
     * Create an OBJ from this mesh builder.
     * @return The generated OBJ.
     */
    public Obj toObj() {
        Obj obj = Objs.create();
        final IndexedSet<Vertex> vertices = new IndexedSet<>();
        final IndexedSet<Vector2> texCoords = new IndexedSet<>();
        final IndexedSet<Vector3> normals = new IndexedSet<>();

        // Add faces
        getFaces().stream().sorted(ConcurrentMesh::compareFaces).forEach(face -> {
            int[] vertexIndices = new int[face.numSides()];
            int[] texCoordIndices = null;
            int[] normalIndices = null;

            int i = 0;
            for (var vertex : face.getVertices()) {
                int index = vertices.addIfAbsent(vertex);
                vertexIndices[i] = index;
                i++;
            }

            i = 0;
            if (face.hasTexCoords()) {
                texCoordIndices = new int[face.numSides()];
                for (var texCoord : face.getTexCoords()) {
                    int index = texCoords.addIfAbsent(texCoord);
                    texCoordIndices[i] = index;
                    i++;
                }
            }

            i = 0;
            if (face.hasNormals()) {
                normalIndices = new int[face.numSides()];
                for (var normal : face.getNormals()) {
                    int index = normals.addIfAbsent(normal);
                    normalIndices[i] = index;
                    i++;
                }
            }

            String material = face.getMaterial();
            obj.setActiveMaterialGroupName(material != null ? material : "");

            Collection<String> groups = face.getGroups();
            obj.setActiveGroupNames(groups != null ? groups : Collections.emptyList());

            obj.addFace(ObjFaces.create(vertexIndices, texCoordIndices, normalIndices));
        });

        // Add vertices, texCoords, and normals
        for (Vertex vertex : vertices) {
            obj.addVertex(vertex);
        }

        for (Vector2 texCoord : texCoords) {
            obj.addTexCoord(texCoord);
        }

        for (Vector3 normal : normals) {
            obj.addNormal(normal);
        }

        return obj;
    }


    private static int compareFaces(Face face1, Face face2) {
        int face1MatHash = face1.getMaterial() != null ? face1.getMaterial().hashCode() : Integer.MIN_VALUE;
        int face2MatHash = face2.getMaterial() != null ? face2.getMaterial().hashCode() : Integer.MIN_VALUE;
        if (Objects.equals(face1.getMaterial(), face2.getMaterial())) {
            return face1.hashGroups() - face2.hashGroups();
        } else {
            return face1MatHash - face2MatHash;
        }
    }
}
