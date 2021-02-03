package com.mojang.minecraft.character;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.Textures;
import com.mojang.minecraft.level.Level;

import static org.lwjgl.opengl.GL11.*;

public class Zombie extends Entity {

    private final ZombieModel model = new ZombieModel();

    public double rotation = Math.random() * Math.PI * 2;
    public double rotationMotionFactor = (Math.random() + 1.0) * 0.01F;

    public float timeOffset = (float) (Math.random() * 1239813.0F);
    public float speed = 1.0F;

    /**
     * Human model test
     *
     * @param level Level of the zombie
     */
    public Zombie(Level level, double x, double y, double z) {
        super(level);

        // Set the position of the entity
        setPosition(x, y, z);
    }


    @Override
    public void onTick() {
        super.onTick();

        // Kill in void
        if (this.y < -100.0F) {
            remove();
        }

        // Increase movement direction
        this.rotation += this.rotationMotionFactor;

        // Modify direction motion factor
        this.rotationMotionFactor *= 0.99D;
        this.rotationMotionFactor += (Math.random() - Math.random()) * Math.random() * Math.random() * 0.009999999776482582;

        // Calculate movement input using rotation
        float vertical = (float) Math.sin(this.rotation);
        float forward = (float) Math.cos(this.rotation);

        // Randomly jump
        if (this.onGround && Math.random() < 0.08F) {
            this.motionY = 0.5F;
        }

        // Apply motion the zombie using the vertical and forward direction
        moveRelative(vertical, forward, this.onGround ? 0.1F : 0.02F);

        // Apply gravity
        this.motionY -= 0.08F;

        // Move the entity using motion
        move(this.motionX, this.motionY, this.motionZ);

        // Decrease motion speed
        this.motionX *= 0.91F;
        this.motionY *= 0.98F;
        this.motionZ *= 0.91F;

        // Decrease motion speed on ground
        if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
        }
    }

    /**
     * Render the zombie
     *
     * @param partialTicks Overflow for interpolation
     */
    public void render(float partialTicks) {
        // Start rendering
        glPushMatrix();
        glEnable(GL_TEXTURE_2D);

        // Bind texture
        glBindTexture(GL_TEXTURE_2D, Textures.loadTexture("/char.png", GL_NEAREST));

        // Zombie animation time
        double time = System.nanoTime() / 1000000000D * 10.0 * this.speed + this.timeOffset;

        // Interpolate entity position
        double interpolatedX = this.prevX + (this.x - this.prevX) * partialTicks;
        double interpolatedY = this.prevY + (this.y - this.prevY) * partialTicks;
        double interpolatedZ = this.prevZ + (this.z - this.prevZ) * partialTicks;

        // Translate using interpolated position
        glTranslated(interpolatedX, interpolatedY, interpolatedZ);

        // Flip the entity because it's upside down
        glScalef(1.0F, -1.0F, 1.0F);

        // Actual size of the entity
        float size = 7.0F / 120.0F;
        glScalef(size, size, size);

        // Body offset animation
        double offsetY = Math.abs(Math.sin(time * 2.0D / 3.0D)) * 5.0 + 23.0D;
        glTranslated(0.0F, -offsetY, 0.0F);

        // Rotate the entity
        glRotated(Math.toDegrees(this.rotation) + 180, 0.0F, 1.0F, 0.0F);

        // Render the model
        this.model.render(time);

        // Stop rendering
        glDisable(GL_TEXTURE_2D);
        glPopMatrix();
    }
}
