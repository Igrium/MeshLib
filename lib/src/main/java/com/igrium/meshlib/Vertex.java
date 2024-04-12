package com.igrium.meshlib;

import com.igrium.meshlib.math.Vector3;

public record Vertex(Vector3 pos, Vector3 color) {
    public Vertex(Vector3 pos) {
        this(pos, null);
    }
}
