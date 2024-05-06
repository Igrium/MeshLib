package com.igrium.meshlib;

/**
 * A reference to an indexed value within a concurrent mesh builder (vertex,
 * texture coordinate, or normal)
 */
public record IndexedReference<T>(T value, int index) {
    
}
