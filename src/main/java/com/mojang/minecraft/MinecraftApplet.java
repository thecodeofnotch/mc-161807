package com.mojang.minecraft;

import java.applet.Applet;
import java.awt.*;

/**
 * Applet to run the game
 */
public class MinecraftApplet extends Applet {

    /**
     * Applet canvas
     */
    private Canvas canvas;

    /**
     * Game instance
     */
    private Minecraft minecraft;

    /**
     * Game main thread
     */
    private Thread thread;

    @Override
    public void init() {
        this.canvas = new Canvas() {
            @Override
            public void addNotify() {
                super.addNotify();

                startGameThread();
            }

            @Override
            public void removeNotify() {
                stopGameThread();

                super.removeNotify();
            }
        };

        // Create game instance
        this.minecraft = new Minecraft(this.canvas, getWidth(), getHeight(), false);
        this.minecraft.appletMode = true;

        // Setup canvas
        this.setLayout(new BorderLayout());
        this.add(this.canvas, "Center");
        this.canvas.setFocusable(true);
        this.validate();
    }

    /**
     * Start game loop in new thread
     */
    public void startGameThread() {
        if (this.thread == null) {
            (this.thread = new Thread(this.minecraft)).start();
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        stopGameThread();
    }

    /**
     * Stop the game loop and destroy everything
     */
    public void stopGameThread() {
        if (this.thread == null) {
            return;
        }

        // Stop the game loop
        this.minecraft.stop();

        try {
            // Wait for 5 seconds
            this.thread.join(5000L);
        } catch (InterruptedException interruptedException) {

            // Destroy display, mouse and keyboard
            try {
                this.minecraft.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.thread = null;
    }
}