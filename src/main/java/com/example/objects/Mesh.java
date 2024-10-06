package com.example.objects;

import static org.lwjgl.opengl.GL11.*;

import com.example.IBO;
import com.example.VBO;

public class Mesh {
    private VBO _vbo;
    private IBO _ibo;
    private int[] _indices;

    public Mesh(float[] vertices, int[] indices) {
        _vbo = new VBO(vertices);
        _ibo = new IBO(indices);
        _indices = indices;
    }

    public void draw() {
        _vbo.bindVBOAndSetupAttributes();
        _ibo.bindIBO();
        glDrawElements(GL_TRIANGLES, _indices.length, GL_UNSIGNED_INT, 0);
    }

    public void cleanup() {
        _vbo.delete();
        _ibo.delete();
    }
}
