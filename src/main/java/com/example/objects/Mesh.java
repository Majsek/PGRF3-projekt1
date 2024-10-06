package com.example.objects;

import static org.lwjgl.opengl.GL11.*;

import com.example.IBO;
import com.example.VBO;

public abstract class Mesh {
    private VBO _vbo;
    private IBO _ibo;

    private float[] _vertices;
    private int[] _indices;

    public Mesh(float[] vertices, int[] indices) {
        _vertices = vertices;
        _indices = indices;
        _vbo = new VBO(_vertices);
        _ibo = new IBO(_indices);
    }

    public void draw() {
        _vbo.bindVBOAndSetupAttributes();
        _ibo.bindIBO();
         glDrawElements(GL_TRIANGLES, _indices.length, GL_UNSIGNED_INT, 0);
        //glDrawArrays(GL_TRIANGLE_STRIP, 0, _vertices.length);
    }

    public void cleanup() {
        _vbo.delete();
        _ibo.delete();
    }
}
