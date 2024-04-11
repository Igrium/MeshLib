package com.igrium.meshlib.math;

public record Vector2(float x, float y) {
    public static final Vector2 ZERO = new Vector2(0, 0);

    public Vector2 add(float x, float y) {
        return new Vector2(this.x + x, this.y + y);
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    public Vector2 sub(float x, float y) {
        return new Vector2(this.x - x, this.y - y);
    }

    public Vector2 sub(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    public Vector2 mul(float val) {
        return new Vector2(x * val, y * val);
    }

    public Vector2 mul(float x, float y) {
        return new Vector2(this.x * x, this.y * y);
    }

    public Vector2 mul(Vector2 other) {
        return new Vector2(this.x * other.x, this.y * other.y);
    }

    public Vector2 div(float val) throws ArithmeticException {
        return new Vector2(this.x / val, this.y / val);
    }

    public Vector2 div(float x, float y) throws ArithmeticException {
        return new Vector2(this.x / x, this.y / y);
    }

    public Vector2 div(Vector2 other) throws ArithmeticException {
        return new Vector2(this.x / other.x, this.y / other.y);
    }

    public float lengthSquared() {
        return x * x + y * y;
    }

    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public Vector2 normalize() {
        double d = Math.sqrt(x * x + y * y);
        if (d < 1.0e-4) {
            return ZERO;
        }

        return new Vector2((float) (x / d), (float) (y / d));
    }

    public float dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    public float distanceToSquared(Vector2 other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;

        return dx * dx + dy * dy;
    }

    public float distanceTo(Vector2 other) {
        return (float) Math.sqrt(distanceToSquared(other));
    }

    @Override
    public final String toString() {
        return "(%f, %f)".formatted(x, y);
    }
}
