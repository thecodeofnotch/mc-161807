package com.mojang.minecraft.level;

import com.mojang.minecraft.Player;

import java.util.Comparator;

public class DirtyChunkSorter implements Comparator<Chunk> {

    private final long now = System.currentTimeMillis();

    private final Player player;
    private final Frustum frustum;

    /**
     * Sort the chunk render order.
     * - Chunks that are visible in the camera have a higher priority than chunks behind the camera.
     * - Chunks with a higher dirty duration have a higher priority.
     * - Chunks closer to the player have a higher priority.
     *
     * @param player The player for the distance priority.
     * @param frustum Frustum for the visible-in-camera priority
     */
    public DirtyChunkSorter(Player player, Frustum frustum) {
        this.player = player;
        this.frustum = frustum;
    }

    @Override
    public int compare(Chunk chunk1, Chunk chunk2) {
        // Matching chunk instance
        if (chunk1.equals(chunk2))
            return 0;

        boolean chunk1Visible = this.frustum.isVisible(chunk1.boundingBox);
        boolean chunk2Visible = this.frustum.isVisible(chunk2.boundingBox);

        // Return priority if one of the chunks is not visible
        if (chunk1Visible && !chunk2Visible) {
            return -1;
        }
        if (chunk2Visible && !chunk1Visible) {
            return 1;
        }

        // Get the duration since last chunk dirty
        int dirtyDuration1 = (int) ((this.now - chunk1.dirtiedTime) / 2000L);
        int dirtyDuration2 = (int) ((this.now - chunk2.dirtiedTime) / 2000L);

        // Return priority if one of the chunks has a bigger dirty duration
        if (dirtyDuration1 < dirtyDuration2) {
            return -1;
        }
        if (dirtyDuration1 > dirtyDuration2) {
            return 1;
        }

        // Decide priority using the distance to the player
        return (chunk1.distanceToSqr(this.player) < chunk2.distanceToSqr(this.player)) ? -1 : 1;
    }
}
