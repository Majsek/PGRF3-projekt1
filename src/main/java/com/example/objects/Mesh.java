package com.example.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import org.lwjgl.opengl.GL20;

import com.example.IBO;
import com.example.VBO;

public abstract class Mesh {
    private Map<Integer, Integer> _uniformModelMatrixLocations = new HashMap<>();

    private VBO _vbo;
    private IBO _ibo;

    private float[] _vertices;
    private int[] _indices;

    private Matrix4f _modelMatrix; // Modelová matice

    public Mesh(float[] vertices, int[] indices, List<Integer> shaderProgramIDs) {
        _vertices = vertices;
        _indices = indices;
        _vbo = new VBO(_vertices);
        _ibo = new IBO(_indices);

        // Inicializace modelové matice (jednotkové)
        _modelMatrix = new Matrix4f().identity();

        // Pro každý shader program získáme lokaci pro modelovou matici
        for (int shaderProgramID : shaderProgramIDs) {
            int modelMatrixLocation = GL20.glGetUniformLocation(shaderProgramID, "modelMatrix");
            _uniformModelMatrixLocations.put(shaderProgramID, modelMatrixLocation);
        }

    }

    public void draw(int drawMode, int shaderProgramID) {
        _vbo.bindVBOAndSetupAttributes();
        _ibo.bindIBO();

        // Pošleme modelovou matici do shaderu
        float[] matrixData = new float[16];
        _modelMatrix.get(matrixData); // Získáme hodnoty matice jako pole
        GL20.glUniformMatrix4fv(_uniformModelMatrixLocations.get(shaderProgramID), false, matrixData);

        glDrawElements(drawMode, _indices.length, GL_UNSIGNED_INT, 0);
    }

    // translation
    public void translate(float x, float y, float z) {
        _modelMatrix.translate(x, y, z);
    }

    // rotation
    public void rotate(float angle, float x, float y, float z) {
        _modelMatrix.rotate((float) Math.toRadians(angle), x, y, z);
    }

    // scale
    public void scale(float x, float y, float z) {
        _modelMatrix.scale(x, y, z);
    }

    public void cleanup() {
        _vbo.delete();
        _ibo.delete();
    }

    public Vector3f getTranslation() {
        float x = _modelMatrix.m30(); // prvek [3][0]
        float y = _modelMatrix.m31(); // prvek [3][1]
        float z = _modelMatrix.m32(); // prvek [3][2]

        return new Vector3f(x, y, z);
    }
}
