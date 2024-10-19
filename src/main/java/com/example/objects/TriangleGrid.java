package com.example.objects;

import java.util.List;

public class TriangleGrid extends Mesh {
    public TriangleGrid(float width, float height, int rows, int cols, List<Integer> shaderProgramIDs) {
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

    // Indexy pro GL_TRIANGLES
    private static int[] createIndices(int rows, int cols) {
        int[] indices = new int[rows * cols * 6]; // 2 trojúhelníky na každý obdélník, 6 indexů na obdélník
        int index = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int topLeft = i * (cols + 1) + j;
                int topRight = topLeft + 1;
                int bottomLeft = (i + 1) * (cols + 1) + j;
                int bottomRight = bottomLeft + 1;

                // První trojúhelník (topLeft, bottomLeft, topRight)
                indices[index++] = topLeft;
                indices[index++] = bottomLeft;
                indices[index++] = topRight;

                // Druhý trojúhelník (topRight, bottomLeft, bottomRight)
                indices[index++] = topRight;
                indices[index++] = bottomLeft;
                indices[index++] = bottomRight;
            }
        }
        return indices;
    }
}
