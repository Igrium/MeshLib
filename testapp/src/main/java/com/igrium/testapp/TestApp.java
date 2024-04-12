package com.igrium.testapp;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.igrium.meshlib.ConcurrentMesh;
import com.igrium.meshlib.Face;
import com.igrium.meshlib.Vertex;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjWriter;

public class TestApp {
    public static void main(String[] args) throws Exception {
        ConcurrentMesh mesh = new ConcurrentMesh();

        Vertex[] face1Verts = new Vertex[] {
                mesh.putVert(0, 0, 0),
                mesh.putVert(0, 0, -1),
                mesh.putVert(1, 0, -1),
                mesh.putVert(1, 0, 0)
        };

        mesh.putFace(Face.create(face1Verts, null, null, null, null));

        Vertex[] face2Verts = new Vertex[] {
                mesh.putVert(0, 0, 0),
                mesh.putVert(1, 0, 0),
                mesh.putVert(1, 0, 1),
                mesh.putVert(0.5f, 0, 1.5f),
                mesh.putVert(0, 0, 1)
        };

        mesh.putFace(Face.create(face2Verts, null, null, "test_mat", null));

        Vertex[] face3Verts = new Vertex[] {
                mesh.putVert(0, 0, 0),
                mesh.putVert(0, 0, -1),
                mesh.putVert(-1, 0, -1),
                mesh.putVert(-1, 0, 0)
        };

        mesh.putFace(Face.create(face3Verts, null, null, null, null));

        Obj obj = mesh.toObj();

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("file.obj"))) {
            ObjWriter.write(obj, writer);
        }
    }
}
