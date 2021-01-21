package com.mojang.rubydung;

import com.mojang.rubydung.level.Level;
import org.lwjgl.input.Keyboard;

public class Player extends Entity {

    /**
     * The player that is controlling the camera of the game
     *
     * @param level Level of the player
     */
    public Player(Level level) {
        super(level);

        this.heightOffset = 1.62f;
    }

    @Override
    public void onTick() {
        super.onTick();

        float forward = 0.0F;
        float vertical = 0.0F;

        // Reset the position of the player
        if (Keyboard.isKeyDown(19)) { // R
            resetPosition();
        }

        // Player movement
        if (Keyboard.isKeyDown(200) || Keyboard.isKeyDown(17)) { // Up, W
            forward--;
        }
        if (Keyboard.isKeyDown(208) || Keyboard.isKeyDown(31)) { // Down, S
            forward++;
        }
        if (Keyboard.isKeyDown(203) || Keyboard.isKeyDown(30)) { // Left, A
            vertical--;
        }
        if (Keyboard.isKeyDown(205) || Keyboard.isKeyDown(32)) {  // Right, D
            vertical++;
        }
        if ((Keyboard.isKeyDown(57) || Keyboard.isKeyDown(219)) && this.onGround) { // Space, Windows Key
            this.motionY = 0.5F;
        }

        // Add motion to the player using keyboard input
        moveRelative(vertical, forward, this.onGround ? 0.1F : 0.02F);

        // Apply gravity motion
        this.motionY -= 0.08D;

        // Move the player using the motion
        move(this.motionX, this.motionY, this.motionZ);

        // Decrease motion
        this.motionX *= 0.91F;
        this.motionY *= 0.98F;
        this.motionZ *= 0.91F;

        // Decrease motion on ground
        if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
        }
    }

}
