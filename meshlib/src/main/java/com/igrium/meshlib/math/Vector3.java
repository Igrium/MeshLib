package com.igrium.meshlib.math;

import de.javagl.obj.FloatTuple;

public record Vector3(float x, float y, float z) implements FloatTuple {
    public static final Vector3 ZERO = new Vector3(0, 0, 0);

    public Vector3 add(float x, float y, float z) {
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3 sub(float x, float y, float z) {
        return new Vector3(this.x - x, this.y - y, this.z - z);
    }

    public Vector3 sub(Vector3 other) {
        return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vector3 mul(float val) {
        return new Vector3(x * val, y * val, z * val);
    }

    public Vector3 mul(float x, float y, float z) {
        return new Vector3(this.x * x, this.y * y, this.z * z);
    }

    public Vector3 mul(Vector3 other) {
        return new Vector3(this.x * other.x, this.y * other.y, this.z * other.z);
    }

    public Vector3 div(float val) throws ArithmeticException {
        return new Vector3(x / val, y / val, z / val);
    }

    public Vector3 div(float x, float y, float z) throws ArithmeticException {
        return new Vector3(this.x / x, this.y / y, this.z / z);
    }

    public Vector3 div(Vector3 other) throws ArithmeticException {
        return new Vector3(this.x / other.x, this.y / other.y, this.z / other.z);
    }

    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public Vector3 normalize() {
        double d = Math.sqrt(x * x + y * y + z * z);
        if (d < 1.0e-4) {
            return ZERO;
        }

        return new Vector3((float) (x / d), (float) (y / d), (float) (z / d));
    }

    public float dot(Vector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3 cross(Vector3 other) {
        return new Vector3(this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x);
    }

    public float distanceToSquared(Vector3 other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        float dz = this.z - other.z;

        return dx * dx + dy * dy + dz * dz;
    }

    public float distanceTo(Vector3 other) {
        return (float) Math.sqrt(distanceToSquared(other));
    }

    @Override
    public final String toString() {
        return "(%f, %f, %f)".formatted(x, y, z);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getZ() {
        return z;
    }

    @Override
    public float getW() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public float get(int index) {
        if (index == 0)
            return x;
        else if (index == 1)
            return y;
        else if (index == 2)
            return z;
        else
            throw new IndexOutOfBoundsException(index);
    }

    @Override
    public int getDimensions() {
        return 3;
    }
}
