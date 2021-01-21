package com.mojang.rubydung.level.tile;

import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.Tessellator;

public class Tile {

    // Tile array
    public static final Tile[] tiles = new Tile[256];

    public static Tile rock = new Tile(1, 1);
    public static Tile grass = new GrassTile(2);
    public static Tile dirt = new Tile(3, 2);
    public static Tile stoneBrick = new Tile(4, 16);
    public static Tile wood = new Tile(5, 4);

    public final int id;
    protected int textureId;

    /**
     * Create tile using id
     *
     * @param id Id of the tile
     */
    public Tile(int id) {
        // Store tile in array
        Tile.tiles[id] = this;
        this.id = id;
    }

    /**
     * Create tile using id and texture slot id
     *
     * @param id        Id of the tile
     * @param textureId Texture slot id
     */
    public Tile(int id, int textureId) {
        this(id);

        this.textureId = textureId;
    }

    /**
     * Render a tile at the given position
     *
     * @param tessellator Tessellator for rendering
     * @param level       Level to check for surrounding tiles
     * @param layer       The layer which decides if it's a shadow or not
     * @param x           Tile position x
     * @param y           Tile position y
     * @param z           Tile position z
     */
    public void render(Tessellator tessellator, Level level, int layer, int x, int y, int z) {
        float shadeX = 0.6f;
        float shadeY = 1.0f;
        float shadeZ = 0.8f;

        // Render bottom face
        if (shouldRenderFace(level, x, y - 1, z, layer)) {
            tessellator.color(shadeY, shadeY, shadeY);
            renderFace(tessellator, x, y, z, 0);
        }

        // Render top face
        if (shouldRenderFace(level, x, y + 1, z, layer)) {
            tessellator.color(shadeY, shadeY, shadeY);
            renderFace(tessellator, x, y, z, 1);
        }

        // Render side faces Z
        if (shouldRenderFace(level, x, y, z - 1, layer)) {
            tessellator.color(shadeZ, shadeZ, shadeZ);
            renderFace(tessellator, x, y, z, 2);
        }
        if (shouldRenderFace(level, x, y, z + 1, layer)) {
            tessellator.color(shadeZ, shadeZ, shadeZ);
            renderFace(tessellator, x, y, z, 3);
        }

        // Render side faces X
        if (shouldRenderFace(level, x - 1, y, z, layer)) {
            tessellator.color(shadeX, shadeX, shadeX);
            renderFace(tessellator, x, y, z, 4);
        }
        if (shouldRenderFace(level, x + 1, y, z, layer)) {
            tessellator.color(shadeX, shadeX, shadeX);
            renderFace(tessellator, x, y, z, 5);
        }
    }

    private boolean shouldRenderFace(Level level, int x, int y, int z, int layer) {
        // Don't render face when both conditions are the same (isShadowLayer != isFullBrightness)
        return !level.isSolidTile(x, y, z) && (level.isLit(x, y, z) ^ layer == 1);
    }

    /**
     * Get the texture slot of the given face
     *
     * @param face Face id
     * @return The texture slot id
     */
    protected int getTexture(int face) {
        return this.textureId;
    }

    /**
     * Render the single face of a tile
     *
     * @param tessellator Tessellator for rendering
     * @param x           Tile position x
     * @param y           Tile position y
     * @param z           Tile position z
     * @param face        Face id (0:Top, 1:Bottom, ...)
     */
    public void renderFace(Tessellator tessellator, int x, int y, int z, int face) {
        // Get texture slot id of this face
        int textureId = getTexture(face);

        // UV mapping points
        float minU = textureId % 16 / 16.0F;
        float maxU = minU + 16 / 256F;
        float minV = (float) (textureId / 16) / 16.0F;
        float maxV = minV + 16 / 256F;

        // Vertex points
        float minX = x + 0.0f;
        float maxX = x + 1.0f;
        float minY = y + 0.0f;
        float maxY = y + 1.0f;
        float minZ = z + 0.0f;
        float maxZ = z + 1.0f;

        // Render bottom face
        if (face == 0) {
            tessellator.vertexUV(minX, minY, maxZ, minU, maxV);
            tessellator.vertexUV(minX, minY, minZ, minU, minV);
            tessellator.vertexUV(maxX, minY, minZ, maxU, minV);
            tessellator.vertexUV(maxX, minY, maxZ, maxU, maxV);
        }

        // Render top face
        if (face == 1) {
            tessellator.vertexUV(maxX, maxY, maxZ, maxU, maxV);
            tessellator.vertexUV(maxX, maxY, minZ, maxU, minV);
            tessellator.vertexUV(minX, maxY, minZ, minU, minV);
            tessellator.vertexUV(minX, maxY, maxZ, minU, maxV);
        }

        // Render side faces Z
        if (face == 2) {
            tessellator.vertexUV(minX, maxY, minZ, maxU, minV);
            tessellator.vertexUV(maxX, maxY, minZ, minU, minV);
            tessellator.vertexUV(maxX, minY, minZ, minU, maxV);
            tessellator.vertexUV(minX, minY, minZ, maxU, maxV);
        }
        if (face == 3) {
            tessellator.vertexUV(minX, maxY, maxZ, minU, minV);
            tessellator.vertexUV(minX, minY, maxZ, minU, maxV);
            tessellator.vertexUV(maxX, minY, maxZ, maxU, maxV);
            tessellator.vertexUV(maxX, maxY, maxZ, maxU, minV);
        }

        // Render side faces X
        if (face == 4) {
            tessellator.vertexUV(minX, maxY, maxZ, maxU, minV);
            tessellator.vertexUV(minX, maxY, minZ, minU, minV);
            tessellator.vertexUV(minX, minY, minZ, minU, maxV);
            tessellator.vertexUV(minX, minY, maxZ, maxU, maxV);
        }
        if (face == 5) {
            tessellator.vertexUV(maxX, minY, maxZ, minU, maxV);
            tessellator.vertexUV(maxX, minY, minZ, maxU, maxV);
            tessellator.vertexUV(maxX, maxY, minZ, maxU, minV);
            tessellator.vertexUV(maxX, maxY, maxZ, minU, minV);
        }
    }

    /**
     * Render the single face of a tile without a texture
     *
     * @param tessellator Tessellator for rendering
     * @param x           Tile position x
     * @param y           Tile position y
     * @param z           Tile position z
     * @param face        Face id (0:Top, 1:Bottom, ...)
     */
    public void renderFaceNoTexture(Tessellator tessellator, int x, int y, int z, int face) {
        float minX = x + 0.0f;
        float maxX = x + 1.0f;
        float minY = y + 0.0f;
        float maxY = y + 1.0f;
        float minZ = z + 0.0f;
        float maxZ = z + 1.0f;

        // Render face
        if (face == 0) {
            tessellator.vertex(minX, minY, maxZ);
            tessellator.vertex(minX, minY, minZ);
            tessellator.vertex(maxX, minY, minZ);
            tessellator.vertex(maxX, minY, maxZ);
        }
        if (face == 1) {
            tessellator.vertex(maxX, maxY, maxZ);
            tessellator.vertex(maxX, maxY, minZ);
            tessellator.vertex(minX, maxY, minZ);
            tessellator.vertex(minX, maxY, maxZ);
        }
        if (face == 2) {
            tessellator.vertex(minX, maxY, minZ);
            tessellator.vertex(maxX, maxY, minZ);
            tessellator.vertex(maxX, minY, minZ);
            tessellator.vertex(minX, minY, minZ);
        }
        if (face == 3) {
            tessellator.vertex(minX, maxY, maxZ);
            tessellator.vertex(minX, minY, maxZ);
            tessellator.vertex(maxX, minY, maxZ);
            tessellator.vertex(maxX, maxY, maxZ);
        }
        if (face == 4) {
            tessellator.vertex(minX, maxY, maxZ);
            tessellator.vertex(minX, maxY, minZ);
            tessellator.vertex(minX, minY, minZ);
            tessellator.vertex(minX, minY, maxZ);
        }
        if (face == 5) {
            tessellator.vertex(maxX, minY, maxZ);
            tessellator.vertex(maxX, minY, minZ);
            tessellator.vertex(maxX, maxY, minZ);
            tessellator.vertex(maxX, maxY, maxZ);
        }
    }
}
