package com.mojang.rubydung.character;

public class Vec3 {

    public float x;
    public float y;
    public float z;

    /**
     * Vector object containing three float values
     *
     * @param x X value
     * @param y Y value
     * @param z Z value
     */
    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Create an interpolated vector from the current vector position to the given one
     *
     * @param vector       The end vector
     * @param partialTicks Interpolation progress
     * @return Interpolated vector between the two positions
     */
    public Vec3 interpolateTo(Vec3 vector, float partialTicks) {
        float interpolatedX = this.x + (vector.x - this.x) * partialTicks;
        float interpolatedY = this.y + (vector.y - this.y) * partialTicks;
        float interpolatedZ = this.z + (vector.z - this.z) * partialTicks;

        return new Vec3(interpolatedX, interpolatedY, interpolatedZ);
    }

    /**
     * Set x, y and z of the vector
     *
     * @param x X value
     * @param y Y value
     * @param z Z value
     */
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
