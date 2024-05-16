package it.unimi.di.ewlab.iss.common.utils;

import androidx.annotation.NonNull;

public final class Position3D {
    private final float x;
    private final float y;
    private final float z;

    public Position3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @NonNull
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
