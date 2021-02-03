package com.mojang.rubydung.particle;

import com.mojang.rubydung.Entity;
import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.Tessellator;

public class Particle extends Entity {

    public int textureId;

    private final float textureUOffset;
    private final float textureVOffset;

    private final float size;
    private final int lifetime;

    private int age = 0;

    /**
     * Particle entity
     *
     * @param level     The level
     * @param x         Particle location x
     * @param y         Particle location y
     * @param z         Particle location z
     * @param motionX   Particle motion x
     * @param motionY   Particle motion y
     * @param motionZ   Particle motion z
     * @param textureId Texture slot id of the particle
     */
    public Particle(Level level, double x, double y, double z, double motionX, double motionY, double motionZ, int textureId) {
        super(level);

        // Set texture
        this.textureId = textureId;

        // Set size of the particle
        setSize(0.2F, 0.2F);
        this.heightOffset = this.boundingBoxHeight / 2.0F;

        // Set position
        setPosition(x, y, z);

        // Set motion and add random values
        this.motionX = motionX + (Math.random() * 2.0D - 1.0D) * 0.4D;
        this.motionY = motionY + (Math.random() * 2.0D - 1.0D) * 0.4D;
        this.motionZ = motionZ + (Math.random() * 2.0D - 1.0D) * 0.4D;

        // Create random speed
        double speed = (Math.random() + Math.random() + 1.0D) * 0.15D;

        // Apply speed
        double distance = Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / distance * speed * 0.7D;
        this.motionY = this.motionY / distance * speed;
        this.motionZ = this.motionZ / distance * speed * 0.7D;

        // Create random texture offset
        this.textureUOffset = (float) Math.random() * 3.0F;
        this.textureVOffset = (float) Math.random() * 3.0F;

        this.size = (float) (Math.random() * 0.5D + 0.5D);
        this.lifetime = (int) (4.0D / (Math.random() * 0.9D + 0.1D));
    }

    @Override
    public void onTick() {
        super.onTick();

        // Kill randomly
        if (this.age++ >= this.lifetime) {
            remove();
        }

        // Apply gravity
        this.motionY -= 0.06D;

        // Move the particle using motion
        this.move(this.motionX, this.motionY, this.motionZ);

        // Decrease motion speed
        this.motionX *= 0.98D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.98D;

        // Decrease motion speed on ground
        if (this.onGround) {
            this.motionX *= 0.7D;
            this.motionZ *= 0.7D;
        }
    }

    /**
     * Render particle
     *
     * @param tessellator  Tessellator for rendering
     * @param partialTicks Ticks for interpolation
     * @param cameraX      Camera rotation X
     * @param cameraY      Camera rotation Y
     * @param cameraZ      Camera rotation Z
     * @param cameraXWithY Additional camera rotation x including the y rotation
     * @param cameraZWithY Additional camera rotation z including the y rotation
     */
    public void render(Tessellator tessellator, float partialTicks, float cameraX, float cameraY, float cameraZ, float cameraXWithY, float cameraZWithY) {
        // UV mapping points
        float minU = (this.textureId % 16 + this.textureUOffset / 4.0F) / 16.0F;
        float maxU = minU + 999.0F / 64000.0F;
        float minV = ((float) (this.textureId / 16) + this.textureVOffset / 4.0F) / 16.0F;
        float maxV = minV + 999.0F / 64000.0F;

        // Interpolate position
        float x = (float) (this.prevX + (this.x - this.prevX) * partialTicks);
        float y = (float) (this.prevY + (this.y - this.prevY) * partialTicks);
        float z = (float) (this.prevZ + (this.z - this.prevZ) * partialTicks);

        // Size of the particle
        float size = this.size * 0.1F;

        // Render vertices
        tessellator.vertexUV(x - cameraX * size - cameraXWithY * size, y - cameraY * size, z - cameraZ * size - cameraZWithY * size, minU, maxV);
        tessellator.vertexUV(x - cameraX * size + cameraXWithY * size, y + cameraY * size, z - cameraZ * size + cameraZWithY * size, minU, minV);
        tessellator.vertexUV(x + cameraX * size + cameraXWithY * size, y + cameraY * size, z + cameraZ * size + cameraZWithY * size, maxU, minV);
        tessellator.vertexUV(x + cameraX * size - cameraXWithY * size, y - cameraY * size, z + cameraZ * size - cameraZWithY * size, maxU, maxV);
    }
}
