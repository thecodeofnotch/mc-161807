package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;

import java.util.Random;

public class GrassTile extends Tile {

    /**
     * Create a grass tile with the id
     *
     * @param id The id of the grass tile
     */
    protected GrassTile(int id) {
        super(id);

        this.textureId = 3;
    }

    @Override
    protected int getTexture(int face) {
        // Texture mapping of the grass tile
        return face == 1 ? 0 : face == 0 ? 2 : 3;
    }

    @Override
    public void onTick(Level level, int x, int y, int z, Random random) {
        if (level.isLit(x, y, z)) {
            // Make surrounding dirt tiles to grass
            for (int i = 0; i < 4; ++i) {
                int targetX = x + random.nextInt(3) - 1;
                int targetY = y + random.nextInt(5) - 3;
                int targetZ = z + random.nextInt(3) - 1;

                // If target is dirt and has sunlight
                if (level.getTile(targetX, targetY, targetZ) == Tile.dirt.id && level.isLit(targetX, targetY, targetZ)) {

                    // Set to grass
                    level.setTile(targetX, targetY, targetZ, Tile.grass.id);
                }
            }
        } else {
            // Set tile to dirt if there is no sunlight
            level.setTile(x, y, z, Tile.dirt.id);
        }
    }
}
