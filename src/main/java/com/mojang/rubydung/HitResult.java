package com.mojang.rubydung;

public class HitResult {

    public int x;
    public int y;
    public int z;

    public int type;
    public int face;

    /**
     * Target tile over mouse
     *
     * @param x    Tile position x
     * @param y    Tile position y
     * @param z    Tile position z
     * @param type Type of result
     * @param face Face id of the tile
     */
    public HitResult(int x, int y, int z, int type, int face) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.face = face;
    }
}
