package com.mojang.minecraft.character;

import static org.lwjgl.opengl.GL11.*;

public class Polygon {

    public Vertex[] vertices;
    public int vertexCount;

    /**
     * Create polygon without UV mappings
     *
     * @param vertices Vertex array
     */
    public Polygon(Vertex[] vertices) {
        this.vertices = vertices;
        this.vertexCount = vertices.length;
    }

    /**
     * Bind UV mappings on the vertices
     *
     * @param vertices Vertex array
     * @param minU     Minimum U coordinate
     * @param minV     Minimum V coordinate
     * @param maxU     Maximum U coordinate
     * @param maxV     Maximum V coordinate
     */
    public Polygon(Vertex[] vertices, int minU, int minV, int maxU, int maxV) {
        this(vertices);

        // Map UV on vertices
        vertices[0] = vertices[0].remap(maxU, minV);
        vertices[1] = vertices[1].remap(minU, minV);
        vertices[2] = vertices[2].remap(minU, maxV);
        vertices[3] = vertices[3].remap(maxU, maxV);
    }

    public void render() {
        // Set color of polygon
        glColor3f(1.0F, 1.0F, 1.0F);

        // Render all vertices
        for (int i = 3; i >= 0; i--) {
            Vertex vertex = this.vertices[i];

            // Bind UV mappings
            glTexCoord2f(vertex.u / 64.0F, vertex.v / 32.0F);

            // Render vertex
            glVertex3f(vertex.position.x, vertex.position.y, vertex.position.z);
        }
    }
}
