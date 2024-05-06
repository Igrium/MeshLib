package com.igrium.testapp;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.igrium.meshlib.ConcurrentMeshBuilder;
import com.igrium.meshlib.FaceBuilder;
import com.igrium.meshlib.Vertex;
import com.igrium.meshlib.math.Vector2;
import com.igrium.meshlib.math.Vector3;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjWriter;

public class TestApp {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting build process");
        long startTime = System.currentTimeMillis();
        ConcurrentMeshBuilder mesh = ConcurrentMeshBuilder.create(true);
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                for (int z = 0; z < 10; z++) {
                    futures.add(makeFuture(mesh, x, y, z));
                }
            }
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        
        System.out.println("Built mesh in " + (System.currentTimeMillis() - startTime) + " ms");
        long compileStartTime = System.currentTimeMillis();
        
        Obj obj = mesh.toObj();
        System.out.println("Compiled mesh in " + (System.currentTimeMillis() - compileStartTime) + " ms");
        System.out.println("Total processing time: " + (System.currentTimeMillis() - startTime) + " ms");

        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("file.obj"))) {
            ObjWriter.write(obj, writer);
        }

        System.out.print("wrote to file.obj");
    }

    // public static void main(String[] args) throws Exception {
    //     long startTime = System.currentTimeMillis();

    //     AbstractConcurrentMesh mesh = new OverlapCheckingMesh();

    //     List<CompletableFuture<Void>> futures = new ArrayList<>();

    //     for (int x = 0; x < 100; x++) {
    //         for (int y = 0; y < 100; y++) {
    //             for (int z = 0; z < 10; z++) {
    //                 futures.add(makeFuture(mesh, x, y, z));
    //             }
    //         }
    //     }

    //     CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        
    //     Obj obj = mesh.toObj();

    //     System.out.println("Compiled mesh in " + (System.currentTimeMillis() - startTime) + " ms");

    //     try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("file.obj"))) {
    //         ObjWriter.write(obj, writer);
    //     }

    //     System.out.print("Wrote to file.obj");
    // }

    private static CompletableFuture<Void> makeFuture(ConcurrentMeshBuilder mesh, float x, float y, float z) {
        return CompletableFuture.runAsync(() -> {
            makeCube(mesh, x, y, z);
        });
    }

    // I didn't finish this. I don't care.
    public static void makeCube(ConcurrentMeshBuilder mesh, float x, float y, float z) {
        float maxX = x + 1;
        float maxY = y + 1;
        float maxZ = z + 1;

        Vector2[] sharedTexCoord = new Vector2[] {
            new Vector2(0, 0),
            new Vector2(0, 1),
            new Vector2(1, 1),
            new Vector2(1, 0)
        };

        Vertex[] face1Verts = new Vertex[] {
            new Vertex(x, y, z),
            new Vertex(maxX, y, z),
            new Vertex(maxX, y, maxZ),
            new Vertex(new Vector3(x, y, maxZ), new Vector3(1, 0, 0))
        };

        new FaceBuilder(face1Verts).texCoords(sharedTexCoord).build(mesh);

        Vertex[] face2Verts = new Vertex[] {
            new Vertex(x, y, z),
            new Vertex(x, maxY, z),
            new Vertex(maxX, maxY, z),
            new Vertex(maxX, y, z)
        };

        new FaceBuilder(face2Verts).texCoords(sharedTexCoord).build(mesh);

        Vertex[] face3Verts = new Vertex[] {
            new Vertex(x, y, z),
            new Vertex(x, y, maxZ),
            new Vertex(x, maxY, maxZ),
            new Vertex(x, maxY, z)
        };

        new FaceBuilder(face3Verts).texCoords(sharedTexCoord).build(mesh);

        Vertex[] face4verts = new Vertex[] {
            new Vertex(maxX, y, z),
            new Vertex(maxX, maxY, z),
            new Vertex(maxX, maxY, maxZ),
            new Vertex(maxX, y, maxZ)
        };

        new FaceBuilder(face4verts).texCoords(sharedTexCoord).build(mesh);

    //     float maxX = x + 1;
    //     float maxY = y + 1;
    //     float maxZ = z + 1;

    //     Vertex[] face1Verts = new Vertex[] {
    //         mesh.putVert(x, y, z),
    //         mesh.putVert(maxX, y, z),
    //         mesh.putVert(maxX, y, maxZ),
    //         mesh.putVert(x, y, maxZ),
    //     };

    //     Vector2[] face1TexCoord = new Vector2[] {
    //         new Vector2(0, 0),
    //         new Vector2(1, 0),
    //         new Vector2(1, 1),
    //         new Vector2(0, 1)
    //     };

    //     Face face1 = Face.create(face1Verts, face1TexCoord, null, null, null);
    //     mesh.addFace(face1);

    //     Vertex[] face2Verts = new Vertex[] {
    //         mesh.putVert(x, y, z),
    //         mesh.putVert(x, maxY, z),
    //         mesh.putVert(maxX, maxY, z),
    //         mesh.putVert(maxX, y, z)
    //     };

    //     Vector3[] face2Normals = new Vector3[] {
    //         new Vector3(0, 1, 0),
    //         new Vector3(0, 1, 0),
    //         new Vector3(0, 0, 1),
    //         new Vector3(0, 1, 0)
    //     };

    //     Face face2 = Face.create(face2Verts, null, null, null, null);
    //     mesh.addFace(face2);

    //     Vertex[] face3Verts = new Vertex[] {
    //         mesh.putVert(x, y, z),
    //         mesh.putVert(x, y, maxZ),
    //         mesh.putVert(x, maxY, maxZ),
    //         mesh.putVert(x, maxY, z)
    //     };

    //     Face face3 = Face.create(face3Verts, null, face2Normals, null, null);
    //     mesh.addFace(face3);
        
    //     Vertex[] face4Verts = new Vertex[] {
    //         mesh.putVert(maxX, y, z),
    //         mesh.putVert(maxX, maxY, z),
    //         mesh.putVert(maxX, maxY, maxZ),
    //         mesh.putVert(maxX, y, maxZ)
    //     };

    //     Face face4 = Face.create(face4Verts, null, null, null, null);
    //     mesh.addFace(face4);
    // }
    }
}
