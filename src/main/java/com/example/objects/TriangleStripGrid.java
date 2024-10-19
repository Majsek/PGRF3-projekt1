package com.example.objects;

import java.util.List;

public class TriangleStripGrid extends Mesh {
    public TriangleStripGrid(float width, float height, int rows, int cols, List<Integer> shaderProgramIDs) {
        super(createVertices(width, height, rows, cols), createIndices(rows, cols), shaderProgramIDs);
    }

    private static float[] createVertices(float width, float height, int rows, int cols) {
        float[] vertices = new float[(rows + 1) * (cols + 1) * 6]; // 3 pozice + 3 barvy
        float dx = width / cols;
        float dy = height / rows;
        int index = 0;

        // Vertexy
        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j <= cols; j++) {
                float x = -width / 2 + j * dx;
                float y = -height / 2 + i * dy;
                float z = 0.0f;

                // Pozice
                vertices[index++] = x;
                vertices[index++] = y;
                vertices[index++] = z;

                // Barvy
                vertices[index++] = (float) j / cols; // R
                vertices[index++] = (float) i / rows; // G
                vertices[index++] = 0.5f; // B
            }
        }
        return vertices;
    }

   // Indexy pro GL_TRIANGLE_STRIP
    private static int[] createIndices(int rows, int cols) {
        int[] indices = new int[2 * (rows + 1) * cols];
        int index = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= cols; j++) {
                int top = i * (cols + 1) + j;
                int bottom = (i + 1) * (cols + 1) + j;

                indices[index++] = top;
                indices[index++] = bottom;
            }
        }
        return indices;
    }
}
