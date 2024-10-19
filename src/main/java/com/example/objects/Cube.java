package com.example.objects;

import java.util.List;

public class Cube extends Mesh {
    public Cube(float size, List<Integer> shaderProgramIDs) {
        super(createVertices(size), createIndices(), shaderProgramIDs);
    }

    private static float[] createVertices(float size) {
        return new float[] {
                // Pozice // Barvy (RGB)
                -size, -size, -size, 1.0f, 1.0f, 1.0f, // 0
                size, -size, -size, 1.0f, 1.0f, 1.0f, // 1
                size, size, -size, 1.0f, 1.0f, 1.0f, // 2
                -size, size, -size, 1.0f, 1.0f, 1.0f, // 3
                -size, -size, size, 1.0f, 1.0f, 1.0f, // 4
                size, -size, size, 1.0f, 1.0f, 1.0f, // 5
                size, size, size, 1.0f, 1.0f, 1.0f, // 6
                -size, size, size, 1.0f, 1.0f, 1.0f // 7
        };
    }

    private static int[] createIndices() {
        return new int[] {
                // Přední čtverec
                0, 1, 2, 2, 3, 0,
                // Zadní čtverec
                4, 5, 6, 6, 7, 4,
                // Levý čtverec
                0, 3, 7, 7, 4, 0,
                // Pravý čtverec
                1, 5, 6, 6, 2, 1,
                // Horní čtverec
                3, 2, 6, 6, 7, 3,
                // Spodní čtverec
                0, 4, 5, 5, 1, 0
        };
    }
}
