package com.example;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.*;

import org.lwjgl.system.MemoryUtil;

public class VBO {
    private int _id;
    float[] _vertices;

    public VBO(float[] vertices) {
        _id = glGenBuffers();
        _vertices = vertices;

        // Připojí VBO a nahraje data na GPU jen jednou
        glBindBuffer(GL_ARRAY_BUFFER, _id);
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(_vertices.length); // Alokuje paměť
        vertexBuffer.put(_vertices).flip(); // Vloží data o vrcholech do bufferu
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW); // Nahraje data z bufferu do GPU
        MemoryUtil.memFree(vertexBuffer); // Uvolní paměť

    }

    public void bindVBOAndSetupAttributes() {
        glBindBuffer(GL_ARRAY_BUFFER, _id); // Nastaví VBO

        // Nastavení atributů
        // Pozice: nastavuje typy, offsety, velikosti,..
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        // Barva: nastavuje typy, offsety, velikosti,..
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    // Uvolnění bufferu
    public void delete() {
        glDeleteBuffers(_id);
    }

    public int getId() {
        return _id;
    }
}
