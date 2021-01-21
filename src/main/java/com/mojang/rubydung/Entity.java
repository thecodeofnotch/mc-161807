package com.mojang.rubydung;

import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.phys.AABB;

import java.util.List;

public abstract class Entity {

    private final Level level;

    public double x, y, z;
    public double prevX, prevY, prevZ;
    public double motionX, motionY, motionZ;
    public float xRotation, yRotation;

    public AABB boundingBox;

    protected boolean onGround;
    protected float heightOffset;

    /**
     * Entity with physics
     *
     * @param level Level of the entity
     */
    public Entity(Level level) {
        this.level = level;

        resetPosition();
    }

    /**
     * Set the entity to a specific location
     *
     * @param x Position x
     * @param y Position y
     * @param z Position z
     */
    public void setPosition(double x, double y, double z) {
        // Set the position of the entity
        this.x = x;
        this.y = y;
        this.z = z;

        // Entity size
        float width = 0.3F;
        float height = 0.9F;

        // Set the position of the bounding box
        this.boundingBox = new AABB(x - width, y - height,
                z - width, x + width,
                y + height, z + width);
    }

    /**
     * Reset the position of the entity to a random location on the level
     */
    protected void resetPosition() {
        float x = (float) Math.random() * this.level.width;
        float y = (float) (this.level.depth + 3);
        float z = (float) Math.random() * this.level.height;

        setPosition(x, y, z);
    }

    /**
     * Turn the head using motion yaw and pitch
     *
     * @param x Rotate the head using yaw
     * @param y Rotate the head using pitch
     */
    public void turn(float x, float y) {
        this.yRotation += x * 0.15F;
        this.xRotation -= y * 0.15F;

        // Pitch limit
        this.xRotation = Math.max(-90.0F, this.xRotation);
        this.xRotation = Math.min(90.0F, this.xRotation);
    }

    /**
     * Update the entity
     */
    public void tick() {
        // Store previous position
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
    }

    /**
     * Move entity relative in level with collision check
     *
     * @param x Relative x
     * @param y Relative y
     * @param z Relative z
     */
    public void move(double x, double y, double z) {
        double prevX = x;
        double prevY = y;
        double prevZ = z;

        // Get surrounded tiles
        List<AABB> aABBs = this.level.getCubes(this.boundingBox.expand(x, y, z));

        // Check for Y collision
        for (AABB abb : aABBs) {
            y = abb.clipYCollide(this.boundingBox, y);
        }
        this.boundingBox.move(0.0F, y, 0.0F);

        // Check for X collision
        for (AABB aABB : aABBs) {
            x = aABB.clipXCollide(this.boundingBox, x);
        }
        this.boundingBox.move(x, 0.0F, 0.0F);

        // Check for Z collision
        for (AABB aABB : aABBs) {
            z = aABB.clipZCollide(this.boundingBox, z);
        }
        this.boundingBox.move(0.0F, 0.0F, z);

        // Update on ground state
        this.onGround = prevY != y && prevY < 0.0F;

        // Stop motion on collision
        if (prevX != x) this.motionX = 0.0D;
        if (prevY != y) this.motionY = 0.0D;
        if (prevZ != z) this.motionZ = 0.0D;

        // Move the actual entity position
        this.x = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
        this.y = this.boundingBox.minY + this.heightOffset;
        this.z = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
    }


    /**
     * Add motion to the entity in the facing direction with given speed
     *
     * @param x     Motion to add on X axis
     * @param z     Motion to add on Z axis
     * @param speed Strength of the added motion
     */
    protected void moveRelative(float x, float z, float speed) {
        float distance = x * x + z * z;

        // Stop moving if too slow
        if (distance < 0.01F)
            return;

        // Apply speed to relative movement
        distance = speed / (float) Math.sqrt(distance);
        x *= distance;
        z *= distance;

        // Calculate sin and cos of entity rotation
        double sin = Math.sin(Math.toRadians(this.yRotation));
        double cos = Math.cos(Math.toRadians(this.yRotation));

        // Move the entity in facing direction
        this.motionX += x * cos - z * sin;
        this.motionZ += z * cos + x * sin;
    }

    /**
     * Is entity in sun
     *
     * @return Entity is in sunlight
     */
    public boolean isLit() {
        return this.level.isLit((int) this.x, (int) this.y, (int) this.z);
    }
}
