package com.mojang.minecraft.level;

public interface LevelListener {
    /**
     * Gets called when a tile changed it's light level
     *
     * @param x    Tile position x
     * @param z    Tile position z
     * @param minY Minimum tile position Y (Start range)
     * @param maxY Maximum tile position Y (End range)
     */
    void lightColumnChanged(int x, int z, int minY, int maxY);

    /**
     * Gets called when a tile changed it's type
     *
     * @param x Tile position x
     * @param y Tile position y
     * @param z Tile position z
     */
    void tileChanged(int x, int y, int z);

    /**
     * Gets called when the entire level changed
     */
    void allChanged();
}