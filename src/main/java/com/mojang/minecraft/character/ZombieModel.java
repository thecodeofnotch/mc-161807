package com.mojang.minecraft.character;

public class ZombieModel {

    public Cube head;
    public Cube body;

    public Cube rightArm;
    public Cube leftArm;

    public Cube rightLeg;
    public Cube leftLeg;

    /**
     * Create cubes for the zombie model
     */
    public ZombieModel() {
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

    /**
     * Render the model
     *
     * @param time Animation offset
     */
    public void render(double time) {
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
    }
}
