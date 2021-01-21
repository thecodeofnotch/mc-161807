package com.mojang.rubydung.level;

import com.mojang.rubydung.level.tile.Tile;
import com.mojang.rubydung.phys.AABB;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Level {

    public final int width;
    public final int height;
    public final int depth;

    private final byte[] blocks;
    private final int[] lightDepths;

    private final ArrayList<LevelListener> levelListeners = new ArrayList<>();
    private final Random random = new Random();

    /**
     * Three dimensional level containing all tiles
     *
     * @param width  Level width
     * @param height Level height
     * @param depth  Level depth
     */
    public Level(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;

        this.blocks = new byte[width * height * depth];
        this.lightDepths = new int[width * height];

        // Load level if it exists
        boolean mapLoaded = load();

        // Generate a new level if file doesn't exists
        if (!mapLoaded) {
            generateMap();
        }

        // Calculate light depth of the entire level
        calcLightDepths(0, 0, width, height);
    }

    /**
     * Generate a new level
     */
    private void generateMap() {
        int[] firstHeightMap = new PerlinNoiseFilter(0).read(this.width, this.height);
        int[] secondHeightMap = new PerlinNoiseFilter(0).read(this.width, this.height);
        int[] cliffMap = new PerlinNoiseFilter(1).read(this.width, this.height);
        int[] rockMap = new PerlinNoiseFilter(1).read(this.width, this.height);

        // Generate tiles
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < depth; ++y) {
                for (int z = 0; z < height; ++z) {
                    // Extract values from height map
                    int firstHeightValue = firstHeightMap[x + z * this.width];
                    int secondHeightValue = secondHeightMap[x + z * this.width];

                    // Change the height map
                    if (cliffMap[x + z * this.width] < 128) {
                        secondHeightValue = firstHeightValue;
                    }

                    // Get max level height at this position
                    int maxLevelHeight = Math.max(secondHeightValue, firstHeightValue) / 8 + this.depth / 3;

                    // Get end of rock layer
                    int maxRockHeight = rockMap[x + z * this.width] / 8 + this.depth / 3;

                    // Keep it below the max height of the level
                    if (maxRockHeight > maxLevelHeight - 2) {
                        maxRockHeight = maxLevelHeight - 2;
                    }

                    // Get block array index
                    int index = (y * this.height + z) * this.width + x;

                    int id = 0;

                    // Grass layer
                    if (y == maxLevelHeight) {
                        id = Tile.grass.id;
                    }

                    // Dirt layer
                    if (y < maxLevelHeight) {
                        id = Tile.dirt.id;
                    }

                    // Rock layer
                    if (y <= maxRockHeight) {
                        id = Tile.rock.id;
                    }

                    // Set the tile id
                    this.blocks[index] = (byte) id;
                }
            }
        }
    }

    /**
     * Load blocks from level.dat
     *
     * @return successfully loaded
     */
    public boolean load() {
        try {
            DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream("level.dat")));
            dis.readFully(this.blocks);
            calcLightDepths(0, 0, this.width, this.height);
            dis.close();

            // Notify all tiles changed
            for (LevelListener levelListener : this.levelListeners) {
                levelListener.allChanged();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Store blocks in level.dat
     */
    public void save() {
        try {
            DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream("level.dat")));
            dos.write(this.blocks);
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculate light depth of given area
     *
     * @param minX Minimum on X axis
     * @param minZ Minimum on Z axis
     * @param maxX Maximum on X axis
     * @param maxZ Maximum on Z axis
     */
    private void calcLightDepths(int minX, int minZ, int maxX, int maxZ) {
        // For each x/z position in level
        for (int x = minX; x < minX + maxX; x++) {
            for (int z = minZ; z < minZ + maxZ; z++) {

                // Get previous light depth value
                int prevDepth = this.lightDepths[x + z * this.width];

                // Calculate new light depth
                int depth = this.depth - 1;
                while (depth > 0 && !isLightBlocker(x, depth, z)) {
                    depth--;
                }

                // Set new light depth
                this.lightDepths[x + z * this.width] = depth;

                // On light depth change
                if (prevDepth != depth) {
                    // Get changed range
                    int minTileChangeY = Math.min(prevDepth, depth);
                    int maxTileChangeY = Math.max(prevDepth, depth);

                    // Notify tile column changed
                    for (LevelListener levelListener : this.levelListeners) {
                        levelListener.lightColumnChanged(x, z, minTileChangeY, maxTileChangeY);
                    }
                }
            }
        }
    }

    /**
     * Return true if a tile is available at the given location
     *
     * @param x Level position x
     * @param y Level position y
     * @param z Level position z
     * @return Tile available
     */
    public boolean isTile(int x, int y, int z) {
        // Is location out of the level?
        if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height) {
            return false;
        }

        // Calculate index from x, y and z
        int index = (y * this.height + z) * this.width + x;

        // Return true if there is a tile at this location
        return this.blocks[index] != 0;
    }

    /**
     * Return the id of the tile at the given location
     *
     * @param x Level position x
     * @param y Level position y
     * @param z Level position z
     * @return Tile id at this location
     */
    public int getTile(int x, int y, int z) {
        // Is location out of the level?
        if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height) {
            return 0;
        }

        // Calculate index from x, y and z
        int index = (y * this.height + z) * this.width + x;

        // Return tile id
        return this.blocks[index];
    }

    /**
     * Returns true if tile is solid and not transparent
     *
     * @param x Tile position x
     * @param y Tile position y
     * @param z Tile position z
     * @return Tile is solid
     */
    public boolean isSolidTile(int x, int y, int z) {
        return isTile(x, y, z);
    }

    /**
     * Returns true if the tile is blocking the light
     *
     * @param x Tile position x
     * @param y Tile position y
     * @param z Tile position z
     * @return Tile blocks the light
     */
    public boolean isLightBlocker(final int x, final int y, final int z) {
        return this.isSolidTile(x, y, z);
    }

    /**
     * Get brightness of a tile
     *
     * @param x Tile position x
     * @param y Tile position y
     * @param z Tile position z
     * @return The brightness value from 0 to 1
     */
    public float getBrightness(int x, int y, int z) {
        // Define brightness
        float dark = 0.8F;
        float light = 1.0F;

        // Is light tile
        if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height) {
            return light;
        }

        // Is dark tile
        if (y < this.lightDepths[x + z * this.width]) {
            return dark;
        }

        // Unknown brightness
        return light;
    }


    /**
     * Get bounding box of all tiles surrounded by the given bounding box
     *
     * @param boundingBox Target bounding box located in the level
     * @return List of bounding boxes representing the tiles around the given bounding box
     */
    public ArrayList<AABB> getCubes(AABB boundingBox) {
        ArrayList<AABB> boundingBoxList = new ArrayList<>();

        int minX = (int) (Math.floor(boundingBox.minX) - 1);
        int maxX = (int) (Math.ceil(boundingBox.maxX) + 1);
        int minY = (int) (Math.floor(boundingBox.minY) - 1);
        int maxY = (int) (Math.ceil(boundingBox.maxY) + 1);
        int minZ = (int) (Math.floor(boundingBox.minZ) - 1);
        int maxZ = (int) (Math.ceil(boundingBox.maxZ) + 1);

        // Minimum level position
        minX = Math.max(0, minX);
        minY = Math.max(0, minY);
        minZ = Math.max(0, minZ);

        // Maximum level position
        maxX = Math.min(this.width, maxX);
        maxY = Math.min(this.depth, maxY);
        maxZ = Math.min(this.height, maxZ);

        // Include all surrounding tiles
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    if (isSolidTile(x, y, z)) {
                        boundingBoxList.add(new AABB(x, y, z, x + 1, y + 1, z + 1));
                    }
                }
            }
        }
        return boundingBoxList;
    }


    /**
     * Set tile at position
     *
     * @param x  Tile position x
     * @param y  Tile position y
     * @param z  Tile position z
     * @param id Type of tile
     */
    public void setTile(int x, int y, int z, int id) {
        // Check if position is out of level
        if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height) {
            return;
        }

        // Set tile
        this.blocks[(y * this.height + z) * this.width + x] = (byte) id;

        // Update lightning
        this.calcLightDepths(x, z, 1, 1);

        // Notify tile changed
        for (LevelListener levelListener : this.levelListeners) {
            levelListener.tileChanged(x, y, z);
        }
    }

    /**
     * Register a level listener
     *
     * @param levelListener Listener interface
     */
    public void addListener(LevelListener levelListener) {
        this.levelListeners.add(levelListener);
    }

    /**
     * Check if the given tile position is in the sun
     *
     * @param x Tile position x
     * @param y Tile position y
     * @param z Tile position z
     * @return Tile is in the sun
     */
    public boolean isLit(int x, int y, int z) {
        return x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height || y >= this.lightDepths[x + z * this.width];
    }

    /**
     * Tick a random tile in the level
     */
    public void onTick() {
        // Amount of tiles in this level
        int totalTiles = this.width * this.height * this.depth;

        // Amount of tiles to process for this tick
        int ticks = totalTiles / 400;

        // Tick multiple tiles in one game tick
        for (int i = 0; i < ticks; ++i) {
            // Get random position of the tile
            int x = this.random.nextInt(this.width);
            int y = this.random.nextInt(this.depth);
            int z = this.random.nextInt(this.height);

            // Get tile type
            Tile tile = Tile.tiles[this.getTile(x, y, z)];
            if (tile != null) {
                // Tick tile
                tile.onTick(this, x, y, z, this.random);
            }
        }
    }
}
