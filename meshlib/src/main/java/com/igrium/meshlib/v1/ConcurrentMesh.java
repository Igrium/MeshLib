package com.igrium.meshlib.v1;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMesh extends AbstractConcurrentMesh {
    private final Set<Face> faces = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public boolean addFace(Face face) {
        return faces.add(face);
    }

    @Override
    public boolean addFaces(Collection<? extends Face> faces) {
        return this.faces.addAll(faces);
    }

    @Override
    public boolean removeFace(Object face) {
        return faces.remove(face);
    }

    @Override
    public boolean removeAllFaces(Collection<?> faces) {
        return this.faces.removeAll(faces);
    }

    @Override
    public Collection<? extends Face> getFaces() {
        return Collections.unmodifiableSet(faces);
    }
}
