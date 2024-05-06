package com.igrium.meshlib;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.igrium.meshlib.math.Vector2;
import com.igrium.meshlib.math.Vector3;

import de.javagl.obj.Obj;
import de.javagl.obj.Objs;

public class ConcurrentMeshBuilder {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public class ReferenceMap<T> {
        // Not all values must be in this set.
        private final Map<T, IndexedReference<T>> set = new ConcurrentHashMap<>();
        private final List<IndexedReference<T>> list = new LinkedList<>();

        private ReferenceMap() {};

        public IndexedReference<T> getOrAdd(T value) {
            lock.readLock().lock();
            try {
                return set.computeIfAbsent(value, v -> {
                    synchronized(list) {
                        int index = list.size();
                        IndexedReference<T> ref = new IndexedReference<>(value, index);
                        list.add(ref);
                        return ref;
                    }
                });
            } finally {
                lock.readLock().unlock();
            }
        }

        public IndexedReference<T> add(T value) {
            lock.readLock().lock();
            try {
                IndexedReference<T> ref;
                synchronized(list) {
                    int index = list.size();
                    ref = new IndexedReference<>(value, index);
                    list.add(ref);
                }

                set.putIfAbsent(value, ref);
                return ref;
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    private final ReferenceMap<Vertex> vertices = new ReferenceMap<>();
    private final ReferenceMap<Vector2> texCoords = new ReferenceMap<>();
    private final ReferenceMap<Vector3> normals = new ReferenceMap<>();

    public ReferenceMap<Vertex> getVertices() {
        return vertices;
    }

    public ReferenceMap<Vector2> getTexCoords() {
        return texCoords;
    }

    public ReferenceMap<Vector3> getNormals() {
        return normals;
    }

    private final Collection<Face> faces = new ConcurrentLinkedQueue<>();

    public Collection<Face> getFaces() {
        return Collections.unmodifiableCollection(faces);
    }

    public void putFace(Face face) {
        lock.readLock().lock();
        try {
            faces.add(face);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Obj toObj() {
        Obj obj = Objs.create();
        lock.writeLock().lock();
        try {

            for (IndexedReference<Vertex> ref : vertices.list) {
                obj.addVertex(ref.value());
            }

            for (IndexedReference<Vector2> ref : texCoords.list) {
                obj.addTexCoord(ref.value());
            }

            for (IndexedReference<Vector3> ref : normals.list) {
                obj.addNormal(ref.value());
            }

            for (Face face : faces) {
                obj.setActiveGroupNames(face.getGroups());
                obj.setActiveMaterialGroupName(face.getMaterial());

                var vertices = face.getVertices();
                int[] vertexIndices = new int[vertices.length];

                for (int i = 0; i < vertexIndices.length; i++) {
                    vertexIndices[i] = vertices[i].index();
                }

                var texCoords = face.getTexCoords();
                int[] texCoordIndices = null;

                if (texCoords != null) {
                    texCoordIndices = new int[texCoords.length];
                    for (int i = 0; i < texCoords.length; i++) {
                        texCoordIndices[i] = texCoords[i].index();
                    }
                }

                var normals = face.getNormals();
                int[] normalIndices = null;

                if (normals != null) {
                    normalIndices = new int[normals.length];
                    for (int i = 0; i < normals.length; i++) {
                        normalIndices[i] = normals[i].index();
                    }
                }

                obj.addFace(vertexIndices, texCoordIndices, normalIndices);
            }

            return obj;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
