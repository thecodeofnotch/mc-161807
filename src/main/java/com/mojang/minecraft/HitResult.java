package com.mojang.minecraft;

public class HitResult {

    public int type;

    public int x;
    public int y;
    public int z;

    public int face;

    /**
     * Target tile over mouse
     *
     * @param type Type of result
     * @param x    Tile position x
     * @param y    Tile position y
     * @param z    Tile position z
     * @param face Face id of the tile
     */
    public HitResult(int type, int x, int y, int z, int face) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
    }
}
