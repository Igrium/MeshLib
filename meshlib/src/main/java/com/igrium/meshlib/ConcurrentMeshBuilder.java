package com.igrium.meshlib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.igrium.meshlib.math.Vector2;
import com.igrium.meshlib.math.Vector3;
import com.igrium.meshlib.util.ArrayUtils;

import de.javagl.obj.Obj;
import de.javagl.obj.Objs;

/**
 * <p>
 * Designed to build a large, complex mesh across multiple threads. The mesh
 * creation process takes place in two stages:
 * </p>
 * <ol>
 * <li>The build stage works across many threads and works to assemble large
 * amounts of vertices and faces.</li>
 * <li>The compile stage works on one thread, and compiles the mesh data into a
 * <code>Obj</code>.</li>
 * </ol>
 * <p>
 * All methods in this class are thread-safe. However, if this mesh is running
 * the compile stage on any thread (<code>toObj()</code>), every other access
 * will be blocked until the compile is complete.
 * </p>
 * <p>
 * Due to the way that index tracking is implemented, manipulation of indexed
 * elements after they've been added to the mesh is unsupported. It is also not
 * recommended to try and load an existing <code>Obj</code> into a
 * <code>ConcurrentMeshBuilder</code>.
 * </p>
 * <p>
 * Concurrent meshes also support "overlap checking". If enabled, every attempt
 * to add a face will first check that there are no existing faces using the
 * same vertex set. This way, weirdly-implemented mesh suppliers don't create
 * invalid mesh data.
 * </p>
 */
public abstract class ConcurrentMeshBuilder {

    /**
     * Create a concurrent mesh builder.
     * 
     * @param overlapChecking If <code>true</code>, the builder will ensure that no
     *                        two faces share the same vertex set, reducing the risk
     *                        of weirdly-implemented mesh suppliers creating invalid
     *                        mesh data.
     * @return The new mesh builder.
     */
    public static ConcurrentMeshBuilder create(boolean overlapChecking) {
        return overlapChecking ? new OverlapCheckingMeshBuilder() : new SimpleConcurrentMeshBuilder();
    }

    /**
     * Create a concurrent mesh builder without overlap checking.
     * @return The new mesh builder.
     */
    public static ConcurrentMeshBuilder create() {
        return new SimpleConcurrentMeshBuilder();
    }
    
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
                return ArrayUtils.arrayEqualsUnordered(this.array, other.array);
            } else {
                return false;
            }
        }
    }

    final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Get the read-write lock that this concurrent mesh uses. The compile stage
     * (<code>toObj()</code>) locks the write lock. Everything else should lock the
     * read lock.
     * 
     * @return The read-write lock.
     */
    protected ReadWriteLock getLock() {
        return lock;
    }

    boolean prioritizeNewFaces = true;

    /**
     * If overlap checking is enabled, whether new faces should override old faces.
     */
    public void setPrioritizeNewFaces(boolean prioritizeNewFaces) {
        this.prioritizeNewFaces = prioritizeNewFaces;
    }

    
    /**
     * If overlap checking is enabled, whether new faces should override old faces.
     */
    public boolean prioritizeNewFaces() {
        return prioritizeNewFaces;
    }

    /**
     * A map of references pointing to indexed values within the mesh (vertices,
     * texcoords, or normals). Like the parent mesh, all methods here are
     * thread-safe.
     */
    public class ReferenceMap<T> {
        // Not all values must be in this set.
        private final Map<T, IndexedReference<T>> set = new ConcurrentHashMap<>();
        private final List<IndexedReference<T>> list = new LinkedList<>();

        private ReferenceMap() {};

        /**
         * Find the indexed reference pointing to a specific value and create it if it
         * does not exist.
         * 
         * @param value The value.
         * @return The indexed reference.
         */
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

        /**
         * Add an indexed reference pointing to a value,
         * regardless if one already eists.
         * 
         * @param value The value.
         * @return The indexed reference.
         */
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

    /**
     * Get the mesh's vertices.
     * @return A mutable reference map of vertices.
     */
    public ReferenceMap<Vertex> getVertices() {
        return vertices;
    }

    /**
     * Get the mesh's texture coordinates.
     * @return A mutable reference map of texture coordinates.
     */
    public ReferenceMap<Vector2> getTexCoords() {
        return texCoords;
    }

    /**
     * Get the mesh's normals.
     * @return A mutable reference map of normals.
     */
    public ReferenceMap<Vector3> getNormals() {
        return normals;
    }

    /**
     * Get all of the faces in this mesh.
     * @return An unmodifiable collection of all the mesh's faces.
     */
    public abstract Collection<Face> getFaces();

    /**
     * Add a face to this mesh. If overlap checking is enabled, the face will be
     * matched against existing faces first.
     * 
     * @param face Face to add.
     * @return The face that ended up in the mesh. If overlap checking is enabled
     *         and the supplied face conflicts with an existing face, will return
     *         either the supplied face or the existing face based on
     *         <code>prioritizeNewFaces()</code>
     */
    public abstract Face putFace(Face face);

    /**
     * Check if this mesh has overlap checking enabled.
     * @return If overlap checking is enabled.
     */
    public abstract boolean isOverlapChecking();

    /**
     * Compile this mesh builder into an <code>Obj</code>. Depending on the
     * complexity of the mesh, this method could take quite some time. Additionally,
     * all other methods in the mesh builder will block until it returns.
     * 
     * @param sort If true, faces will be sorted by their material and subsequently
     *             their group.
     * 
     * @return The compiled <code>Obj</code>
     */
    public Obj toObj(boolean sort) {
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

            Collection<Face> faces = getFaces();

            if (sort) {
                List<Face> sorted = new ArrayList<>(faces);
                sorted.sort(Face::compareTo);
                faces = sorted;
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
    
    
    /**
     * Compile this mesh builder into an <code>Obj</code>. Depending on the
     * complexity of the mesh, this method could take quite some time. Additionally,
     * all other methods in the mesh builder will block until it returns.
     * 
     * @return The compiled <code>Obj</code>
     */
    public Obj toObj() {
        return toObj(false);
    }

    private static class SimpleConcurrentMeshBuilder extends ConcurrentMeshBuilder {
        private final Queue<Face> faces = new ConcurrentLinkedQueue<>();

        @Override
        public Collection<Face> getFaces() {
            return Collections.unmodifiableCollection(faces);
        }

        @Override
        public Face putFace(Face face) {
            lock.readLock().lock();
            try {
                faces.add(face);
            } finally {
                lock.readLock().unlock();
            }
            return face;
        }

        @Override
        public boolean isOverlapChecking() {
            return false;
        }
    }

    private static class OverlapCheckingMeshBuilder extends ConcurrentMeshBuilder {
        private final Map<UnorderedArrayHandle<IndexedReference<Vertex>>, Face> faces = new ConcurrentHashMap<>();

        @Override
        public Collection<Face> getFaces() {
            return Collections.unmodifiableCollection(faces.values());
        }

        public Face putFace(Face face, boolean override) {
            if (override) {
                faces.put(new UnorderedArrayHandle<>(face.getVertices()), face);
                return face;
            } else {
                Face prev = faces.putIfAbsent(new UnorderedArrayHandle<>(face.getVertices()), face);
                return prev != null ? prev : face;
            }
        }

        @Override
        public Face putFace(Face face) {
            return putFace(face, prioritizeNewFaces);
        }

        @Override
        public boolean isOverlapChecking() {
            return true;
        }
    }
}
