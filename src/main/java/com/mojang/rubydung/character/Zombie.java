package com.mojang.rubydung.character;

import com.mojang.rubydung.Entity;
import com.mojang.rubydung.Textures;
import com.mojang.rubydung.level.Level;

import static org.lwjgl.opengl.GL11.*;

public class Zombie extends Entity {

    public Cube head;
    public Cube body;

    public Cube rightArm;
    public Cube leftArm;

    public Cube rightLeg;
    public Cube leftLeg;

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
        this.x = x;
        this.y = y;
        this.z = z;

        // Create head cube
        this.head = new Cube(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);

        // Create body cube
        this.body = new Cube(16, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4);

        // Right arm cube
        this.rightArm = new Cube(40, 16)
                .addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4);
        this.rightArm.setPosition(-5.0F, 2.0F, 0.0F);

        // Left arm cube
        this.leftArm = new Cube(40, 16)
                .addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4);
        this.leftArm.setPosition(5.0F, 2.0F, 0.0F);

        // Right Legs cube
        this.rightLeg = new Cube(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        this.rightLeg.setPosition(-2.0F, 12.0F, 0.0F);

        // Left leg cube
        this.leftLeg = new Cube(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        this.leftLeg.setPosition(2.0F, 12.0F, 0.0F);
    }


    @Override
    public void tick() {
        super.tick();

        // Increase movement direction
        this.rotation += this.rotationMotionFactor;

        // Modify direction motion factor
        this.rotationMotionFactor *= 0.99D;
        this.rotationMotionFactor += (Math.random() - Math.random()) * Math.random() * Math.random() * 0.009999999776482582;

        // Calculate movement input using rotation
        float vertical = (float) Math.sin(this.rotation);
        float forward = (float) Math.cos(this.rotation);

        // Randomly jump
        if (this.onGround && Math.random() < 0.01) {
            this.motionY = 0.12f;
        }

        // Apply motion the zombie using the vertical and forward direction
        moveRelative(vertical, forward, this.onGround ? 0.02f : 0.005f);

        // Apply gravity
        this.motionY -= (float) 0.005;

        // Move the entity using motion
        move(this.motionX, this.motionY, this.motionZ);

        // Decrease motion speed
        this.motionX *= 0.91f;
        this.motionY *= 0.98f;
        this.motionZ *= 0.91f;

        // Reset position in void
        if (this.y < -100.0F) {
            resetPosition();
        }

        // Decrease motion speed on ground
        if (this.onGround) {
            this.motionX *= 0.8f;
            this.motionZ *= 0.8f;
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

        // Set rotation of cubes
        this.head.yRotation = (float) Math.sin(time * 0.83);
        this.head.xRotation = (float) Math.sin(time) * 0.8F;
        this.rightArm.xRotation = (float) Math.sin(time * 0.6662 + Math.PI) * 2.0F;
        this.rightArm.zRotation = (float) (Math.sin(time * 0.2312) + 1.0);
        this.leftArm.xRotation = (float) Math.sin(time * 0.6662) * 2.0f;
        this.leftArm.zRotation = (float) (Math.sin(time * 0.2812) - 1.0);
        this.rightLeg.xRotation = (float) Math.sin(time * 0.6662) * 1.4f;
        this.leftLeg.xRotation = (float) Math.sin(time * 0.6662 + Math.PI) * 1.4F;

        // Render cubes
        this.head.render();
        this.body.render();
        this.rightArm.render();
        this.leftArm.render();
        this.rightLeg.render();
        this.leftLeg.render();

        // Stop rendering
        glDisable(GL_TEXTURE_2D);
        glPopMatrix();
    }
}
