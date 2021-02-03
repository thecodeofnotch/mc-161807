package com.mojang.minecraft.character;

import static org.lwjgl.opengl.GL11.*;

public class Cube {

    private Polygon[] polygons;

    private int textureOffsetX;
    private int textureOffsetY;

    public float x;
    public float y;
    public float z;

    public float xRotation;
    public float yRotation;
    public float zRotation;

    /**
     * Create cube object
     *
     * @param textureOffsetX x offset position on the texture
     * @param textureOffsetY y offset position on the texture
     */
    public Cube(int textureOffsetX, int textureOffsetY) {
        this.textureOffsetX = textureOffsetX;
        this.textureOffsetY = textureOffsetY;
    }

    /**
     * Set the texture offset position of the cube
     *
     * @param textureOffsetX Offset position x
     * @param textureOffsetY Offset position y
     */
    public void setTextureOffset(int textureOffsetX, int textureOffsetY) {
        this.textureOffsetX = textureOffsetX;
        this.textureOffsetY = textureOffsetY;
    }

    /**
     * Create box using offset position and width, height and depth
     *
     * @param offsetX X offset of the render position
     * @param offsetY Y offset of the render position
     * @param offsetZ Z offset of the render position
     * @param width   Cube width
     * @param height  Cube height
     * @param depth   Cube depth
     */
    public Cube addBox(float offsetX, float offsetY, float offsetZ, int width, int height, int depth) {
        this.polygons = new Polygon[6];

        float x = offsetX + width;
        float y = offsetY + height;
        float z = offsetZ + depth;

        // Create bottom vertex points of cube
        Vertex vertexBottom1 = new Vertex(offsetX, offsetY, offsetZ, 0.0F, 0.0F);
        Vertex vertexBottom2 = new Vertex(x, offsetY, offsetZ, 0.0F, 8.0F);
        Vertex vertexBottom3 = new Vertex(offsetX, offsetY, z, 0.0F, 0.0F);
        Vertex vertexBottom4 = new Vertex(x, offsetY, z, 0.0F, 8.0F);

        // Create top vertex points of cube
        Vertex vertexTop1 = new Vertex(x, y, z, 8.0F, 8.0F);
        Vertex vertexTop2 = new Vertex(offsetX, y, z, 8.0F, 0.0F);
        Vertex vertexTop3 = new Vertex(x, y, offsetZ, 8.0F, 8.0F);
        Vertex vertexTop4 = new Vertex(offsetX, y, offsetZ, 8.0F, 0.0F);

        // Create polygons for each cube side
        this.polygons[0] = new Polygon(
                new Vertex[]{
                        vertexBottom4, vertexBottom2, vertexTop3, vertexTop1
                },
                this.textureOffsetX + depth + width,
                this.textureOffsetY + depth,
                this.textureOffsetX + depth + width + depth,
                this.textureOffsetY + depth + height
        );

        this.polygons[1] = new Polygon(
                new Vertex[]{
                        vertexBottom1, vertexBottom3, vertexTop2, vertexTop4
                },
                this.textureOffsetX,
                this.textureOffsetY + depth,
                this.textureOffsetX + depth,
                this.textureOffsetY + depth + height
        );

        this.polygons[2] = new Polygon(
                new Vertex[]{
                        vertexBottom4, vertexBottom3, vertexBottom1, vertexBottom2
                },
                this.textureOffsetX + depth,
                this.textureOffsetY,
                this.textureOffsetX + depth + width,
                this.textureOffsetY + depth
        );

        this.polygons[3] = new Polygon(
                new Vertex[]{
                        vertexTop3, vertexTop4, vertexTop2, vertexTop1
                },
                this.textureOffsetX + depth + width,
                this.textureOffsetY,
                this.textureOffsetX + depth + width + width,
                this.textureOffsetY + depth
        );

        this.polygons[4] = new Polygon(
                new Vertex[]{
                        vertexBottom2, vertexBottom1, vertexTop4, vertexTop3
                },
                this.textureOffsetX + depth,
                this.textureOffsetY + depth,
                this.textureOffsetX + depth + width,
                this.textureOffsetY + depth + height
        );

        this.polygons[5] = new Polygon(
                new Vertex[]{
                        vertexBottom3, vertexBottom4, vertexTop1, vertexTop2
                },
                this.textureOffsetX + depth + width + depth,
                this.textureOffsetY + depth,
                this.textureOffsetX + depth + width + depth + width,
                this.textureOffsetY + depth + height
        );

        return this;
    }

    /**
     * Set the absolute position of the cube
     *
     * @param x Absolute x position of cube
     * @param y Absolute y position of cube
     * @param z Absolute z position of cube
     */
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Render the cube
     */
    public void render() {
        glPushMatrix();

        // Position of the cube
        glTranslatef(this.x, this.y, this.z);

        // Rotation of the cube
        glRotated(Math.toDegrees(this.zRotation), 0.0F, 0.0F, 1.0F);
        glRotated(Math.toDegrees(this.yRotation), 0.0F, 1.0F, 0.0F);
        glRotated(Math.toDegrees(this.xRotation), 1.0F, 0.0F, 0.0F);

        // Start rendering
        glBegin(GL_QUADS);

        // Render polygons
        for (Polygon polygon : this.polygons) {
            polygon.render();
        }

        // Stop rendering
        glEnd();

        glPopMatrix();
    }
}
