package com.mojang.rubydung.level.tile;

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
}
