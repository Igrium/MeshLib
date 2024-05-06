package com.igrium.meshlib;

import com.igrium.meshlib.math.Vector3;

import de.javagl.obj.FloatTuple;

/**
 * A vertex within the obj.
 * @param pos The position in 3D space.
 * @param color The vertex color. Used by Blender's OBJ parser.
 */
public record Vertex(Vector3 pos, Vector3 color) implements FloatTuple {
    public Vertex(Vector3 pos) {
        this(pos, new Vector3(1, 1, 1));
    }

    public Vertex(float x, float y, float z) {
        this(new Vector3(x, y, z));
    }

    @Override
    public float getX() {
        return pos.x();
    }

    @Override
    public float getY() {
        return pos.y();
    }

    @Override
    public float getZ() {
        return pos.z();
    }

    @Override
    public float getW() {
        return color.x();
    }

    @Override
    public float get(int index) {
        if (index == 0)
            return pos.x();
        else if (index == 1)
            return pos.y();
        else if (index == 2)
            return pos.z();
        else if (index == 3)
            return color.x();
        else if (index == 4)
            return color.y();
        else if (index == 5)
            return color.z();
        else
            throw new IndexOutOfBoundsException(index);
    }

    @Override
    public int getDimensions() {
        return 6;
    }
}
