package com.igrium.testapp;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.igrium.meshlib.ConcurrentMesh;
import com.igrium.meshlib.Face;
import com.igrium.meshlib.Vertex;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjWriter;

public class TestApp {
    public static void main(String[] args) throws Exception {
        ConcurrentMesh mesh = new ConcurrentMesh();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                for (int z = 0; z < 100; z++) {
                    futures.add(makeFuture(mesh, x, y, z));
                }
            }
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        
        Obj obj = mesh.toObj();

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("file.obj"))) {
            ObjWriter.write(obj, writer);
        }
    }

    private static CompletableFuture<Void> makeFuture(ConcurrentMesh mesh, float x, float y, float z) {
        return CompletableFuture.runAsync(() -> {
            makeCube(mesh, x, y, z);
        });
    }

    // I didn't finish this. I don't care.
    public static void makeCube(ConcurrentMesh mesh, float x, float y, float z) {
        float maxX = x + 1;
        float maxY = y + 1;
        float maxZ = z + 1;

        Vertex[] face1Verts = new Vertex[] {
            mesh.putVert(x, y, z),
            mesh.putVert(maxX, y, z),
            mesh.putVert(maxX, y, maxZ),
            mesh.putVert(x, y, maxZ),
        };

        Face face1 = Face.create(face1Verts, null, null, null, null);
        mesh.putFace(face1);

        Vertex[] face2Verts = new Vertex[] {
            mesh.putVert(x, y, z),
            mesh.putVert(x, maxY, z),
            mesh.putVert(maxX, maxY, z),
            mesh.putVert(maxX, y, z)
        };

        Face face2 = Face.create(face2Verts, null, null, null, null);
        mesh.putFace(face2);

        Vertex[] face3Verts = new Vertex[] {
            mesh.putVert(x, y, z),
            mesh.putVert(x, y, maxZ),
            mesh.putVert(x, maxY, maxZ),
            mesh.putVert(x, maxY, z)
        };

        Face face3 = Face.create(face3Verts, null, null, null, null);
        mesh.putFace(face3);
        
        Vertex[] face4Verts = new Vertex[] {
            mesh.putVert(maxX, y, z),
            mesh.putVert(maxX, maxY, z),
            mesh.putVert(maxX, maxY, maxZ),
            mesh.putVert(maxX, y, maxZ)
        };

        Face face4 = Face.create(face4Verts, null, null, null, null);
        mesh.putFace(face4);
    }

}
