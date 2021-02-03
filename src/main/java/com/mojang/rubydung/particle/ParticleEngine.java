package com.mojang.rubydung.particle;

import com.mojang.rubydung.Player;
import com.mojang.rubydung.Textures;
import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.Tessellator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class ParticleEngine {

    protected final Level level;

    private final List<Particle> particles = new ArrayList<>();

    public ParticleEngine(Level level) {
        this.level = level;
    }

    /**
     * Add particle to engine
     *
     * @param particle The particle to add
     */
    public void add(Particle particle) {
        this.particles.add(particle);
    }

    /**
     * Tick all particles and remove dead particles
     */
    public void onTick() {
        // Tick all particles
        Iterator<Particle> iterator = this.particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();

            // Tick this particle
            particle.onTick();

            // Remove particle when removed flag is set
            if (particle.removed) {
                iterator.remove();
            }
        }
    }

    /**
     * Render all particles
     *
     * @param player       The player
     * @param partialTicks Ticks for interpolation
     * @param layer        Shadow layer
     */
    public void render(Player player, Tessellator tessellator, float partialTicks, int layer) {
        glEnable(GL_TEXTURE_2D);

        // Bind terrain texture
        int id = Textures.loadTexture("/terrain.png", 9728);
        glBindTexture(GL_TEXTURE_2D, id);

        // Get camera angel
        double cameraX = -Math.cos(Math.toRadians(player.yRotation));
        double cameraY = Math.cos(Math.toRadians(player.xRotation));
        double cameraZ = -Math.sin(Math.toRadians(player.yRotation));

        // Get additional camera rotation
        double cameraXWithY = -cameraZ * Math.sin(Math.toRadians(player.xRotation));
        double cameraZWithY = cameraX * Math.sin(Math.toRadians(player.xRotation));

        // Start rendering
        glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
        tessellator.init();

        // Render all particles in correct layer
        for (Particle particle : this.particles) {
            if (particle.isLit() ^ layer == 1) {
                particle.render(tessellator, partialTicks, (float) cameraX, (float) cameraY, (float) cameraZ, (float) cameraXWithY, (float) cameraZWithY);
            }
        }

        // Finish rendering
        tessellator.flush();
        glDisable(GL_TEXTURE_2D);
    }
}
