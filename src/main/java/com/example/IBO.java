package com.example;

import static org.lwjgl.opengl.GL15.*;
import java.nio.*;

import org.lwjgl.system.MemoryUtil;

public class IBO {
    private int _id;
    int[] _indices;

    public IBO(int[] indices) {
        _id = glGenBuffers();
        _indices = indices;
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _id);
        IntBuffer indexBuffer = MemoryUtil.memAllocInt(_indices.length);
        indexBuffer.put(_indices).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(indexBuffer);
    }

    public void bindIBO() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _id); // Nastaví IBO
    }

    // Uvolnění bufferu
    public void delete() {
        glDeleteBuffers(_id);
    }

    public int getId() {
        return _id;
    }
}
