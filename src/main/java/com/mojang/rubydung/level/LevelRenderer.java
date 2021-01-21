package com.mojang.rubydung.level;

import com.mojang.rubydung.Entity;
import com.mojang.rubydung.HitResult;
import com.mojang.rubydung.Player;
import com.mojang.rubydung.level.tile.Tile;
import com.mojang.rubydung.phys.AABB;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class LevelRenderer implements LevelListener {

    private static final int CHUNK_SIZE = 16;

    private final Tessellator tessellator;
    private final Level level;
    private final Chunk[] chunks;

    private final int chunkAmountX;
    private final int chunkAmountY;
    private final int chunkAmountZ;

    /**
     * Create renderer for level
     *
     * @param level The rendered level
     */
    public LevelRenderer(Level level) {
        level.addListener(this);

        this.tessellator = new Tessellator();
        this.level = level;

        // Calculate amount of chunks of level
        this.chunkAmountX = level.width / CHUNK_SIZE;
        this.chunkAmountY = level.depth / CHUNK_SIZE;
        this.chunkAmountZ = level.height / CHUNK_SIZE;

        // Create chunk array
        this.chunks = new Chunk[this.chunkAmountX * this.chunkAmountY * this.chunkAmountZ];

        // Fill level with chunks
        for (int x = 0; x < this.chunkAmountX; x++) {
            for (int y = 0; y < this.chunkAmountY; y++) {
                for (int z = 0; z < this.chunkAmountZ; z++) {
                    // Calculate min bounds for chunk
                    int minChunkX = x * CHUNK_SIZE;
                    int minChunkY = y * CHUNK_SIZE;
                    int minChunkZ = z * CHUNK_SIZE;

                    // Calculate max bounds for chunk
                    int maxChunkX = (x + 1) * CHUNK_SIZE;
                    int maxChunkY = (y + 1) * CHUNK_SIZE;
                    int maxChunkZ = (z + 1) * CHUNK_SIZE;

                    // Check for chunk bounds out of level
                    maxChunkX = Math.min(level.width, maxChunkX);
                    maxChunkY = Math.min(level.depth, maxChunkY);
                    maxChunkZ = Math.min(level.height, maxChunkZ);

                    // Create chunk based on bounds
                    Chunk chunk = new Chunk(level, minChunkX, minChunkY, minChunkZ, maxChunkX, maxChunkY, maxChunkZ);
                    this.chunks[(x + y * this.chunkAmountX) * this.chunkAmountZ + z] = chunk;
                }
            }
        }
    }

    /**
     * Get all chunks with dirty flag
     *
     * @return List of dirty chunks
     */
    public List<Chunk> getAllDirtyChunks() {
        ArrayList<Chunk> dirty = new ArrayList<>();
        for (final Chunk chunk : this.chunks) {
            if (chunk.isDirty()) {
                dirty.add(chunk);
            }
        }
        return dirty;
    }

    /**
     * Render all chunks of the level
     *
     * @param layer The render layer
     */
    public void render(int layer) {
        // Get current camera frustum
        Frustum frustum = Frustum.getFrustum();

        // Reset global chunk rebuild stats
        Chunk.rebuiltThisFrame = 0;

        // For all chunks
        for (Chunk chunk : this.chunks) {

            // Render if bounding box of chunk is in frustum
            if (frustum.isVisible(chunk.boundingBox)) {

                // Render chunk
                chunk.render(layer);
            }
        }
    }

    /**
     * Rebuild all dirty chunks in a sorted order
     *
     * @param player The player for the sort priority. Chunks closer to the player will get a higher priority.
     */
    public void updateDirtyChunks(Player player) {
        // Get all dirty chunks
        List<Chunk> dirty = getAllDirtyChunks();
        if (!dirty.isEmpty()) {

            // Sort the dirty chunk list
            dirty.sort(new DirtyChunkSorter(player, Frustum.getFrustum()));

            // Rebuild max 8 chunks per frame
            for (int i = 0; i < 8 && i < dirty.size(); i++) {
                dirty.get(i).rebuild();
            }
        }
    }

    /**
     * Mark all chunks inside of the given area as dirty.
     *
     * @param minX Minimum on X axis
     * @param minY Minimum on Y axis
     * @param minZ Minimum on Z axis
     * @param maxX Maximum on X axis
     * @param maxY Maximum on Y axis
     * @param maxZ Maximum on Z axis
     */
    public void setDirty(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        // To chunk coordinates
        minX /= CHUNK_SIZE;
        minY /= CHUNK_SIZE;
        minZ /= CHUNK_SIZE;
        maxX /= CHUNK_SIZE;
        maxY /= CHUNK_SIZE;
        maxZ /= CHUNK_SIZE;

        // Minimum limit
        minX = Math.max(minX, 0);
        minY = Math.max(minY, 0);
        minZ = Math.max(minZ, 0);

        // Maximum limit
        maxX = Math.min(maxX, this.chunkAmountX - 1);
        maxY = Math.min(maxY, this.chunkAmountY - 1);
        maxZ = Math.min(maxZ, this.chunkAmountZ - 1);

        // Mark all chunks as dirty
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    // Get chunk at this position
                    Chunk chunk = this.chunks[(x + y * this.chunkAmountX) * this.chunkAmountZ + z];

                    // Set dirty
                    chunk.setDirty();
                }
            }
        }
    }

    /**
     * Render pick selection face on tile
     *
     * @param player The player
     */
    public void pick(Entity player) {
        float radius = 3.0F;
        AABB boundingBox = player.boundingBox.grow(radius, radius, radius);

        int minX = (int) boundingBox.minX;
        int maxX = (int) (boundingBox.maxX + 1.0f);
        int minY = (int) boundingBox.minY;
        int maxY = (int) (boundingBox.maxY + 1.0f);
        int minZ = (int) boundingBox.minZ;
        int maxZ = (int) (boundingBox.maxZ + 1.0f);

        glInitNames();
        for (int x = minX; x < maxX; x++) {
            // Name value x
            glPushName(x);
            for (int y = minY; y < maxY; y++) {
                // Name value y
                glPushName(y);
                for (int z = minZ; z < maxZ; z++) {
                    // Name value z
                    glPushName(z);

                    // Check for solid tile
                    if (this.level.isSolidTile(x, y, z)) {

                        // Name value type
                        glPushName(0);

                        // Render all faces
                        for (int face = 0; face < 6; face++) {

                            // Name value face id
                            glPushName(face);

                            // Render selection face
                            this.tessellator.init();
                            Tile.rock.renderFaceNoTexture(this.tessellator, x, y, z, face);
                            this.tessellator.flush();

                            glPopName();
                        }
                        glPopName();
                    }
                    glPopName();
                }
                glPopName();
            }
            glPopName();
        }
    }

    /**
     * Render hit face of the result
     *
     * @param hitResult The hit result to render
     */
    public void renderHit(HitResult hitResult) {
        // Setup blending and color
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_CURRENT_BIT);
        glColor4f(1.0F, 1.0F, 1.0F, ((float)Math.sin(System.currentTimeMillis() / 100.0D) * 0.2F + 0.4F) * 0.5F);

        // Render face
        this.tessellator.init();
        Tile.rock.renderFaceNoTexture(this.tessellator, hitResult.x, hitResult.y, hitResult.z, hitResult.face);
        this.tessellator.flush();

        // Disable blending
        glDisable(GL_BLEND);
    }

    @Override
    public void lightColumnChanged(int x, int z, int minY, int maxY) {
        setDirty(x - 1, minY - 1, z - 1, x + 1, maxY + 1, z + 1);
    }

    @Override
    public void tileChanged(int x, int y, int z) {
        setDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    @Override
    public void allChanged() {
        setDirty(0, 0, 0, this.level.width, this.level.depth, this.level.height);
    }
}
