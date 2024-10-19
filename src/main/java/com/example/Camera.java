package com.example;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import org.lwjgl.opengl.GL20;

public class Camera {
    private Vector3f _position;
    private Vector3f _orientation;
    private Vector3f _up;

    private float _speed = 5.0f;
    private float _sensitivity = 0.5f;

    private boolean _clickedIn = false;
    private boolean _justClicked = false;
    private int _width, _height;
    Matrix4f _perspectiveProjection;
    Matrix4f _orthogonalProjection;
    Matrix4f _projection;
    boolean _isPerspectiveProjection;
    private boolean _cKeyPressedLastFrame;

    public Camera(Vector3f position, Vector3f orientation, Vector3f up, int width, int height) {
        _position = position;
        _orientation = orientation;
        _up = up;
        _width = width;
        _height = height;
        calculatePerspectiveProjection();
        calculateOrthogonalProjection();

        _projection = _perspectiveProjection;
        _isPerspectiveProjection = true;
    }

    public void setCameraMatrixIntoShader(int shader) {
        Matrix4f view = new Matrix4f();
        Matrix4f projection = new Matrix4f(_projection);

        // Nastaví kameru, aby se dívala správným směrem
        view.lookAt(_position, _position.add(_orientation, new Vector3f()), _up);

        // Odeslání matice do shaderu
        int uniformCamMatrixLocation = GL20.glGetUniformLocation(shader, "camMatrix");
        GL20.glUniformMatrix4fv(uniformCamMatrixLocation, false, projection.mul(view).get(new float[16]));

        int uniformViewPosLocation = GL20.glGetUniformLocation(shader, "viewPos");
        GL20.glUniform3f(uniformViewPosLocation, _position.x, _position.y, _position.z);
    }

    public void setCameraMatrixIntoShaderWithCustomFarPlane(int shader, float farPlane) {
        Matrix4f view = new Matrix4f();
        Matrix4f projection = new Matrix4f();

        float FOVdeg = 70f;
        float nearPlane = 0.1f;

        // Nastaví kameru, aby se dívala správným směrem
        view.lookAt(_position, _position.add(_orientation, new Vector3f()), _up);

        projection.perspective((float) Math.toRadians(FOVdeg), (float) _width / _height, nearPlane, farPlane);

        // Odeslání matice do shaderu
        int uniformCamMatrixLocation = GL20.glGetUniformLocation(shader, "camMatrix");
        GL20.glUniformMatrix4fv(uniformCamMatrixLocation, false, projection.mul(view).get(new float[16]));

        int uniformViewPosLocation = GL20.glGetUniformLocation(shader, "viewPos");
        GL20.glUniform3f(uniformViewPosLocation, _position.x, _position.y, _position.z);
    }

    public void setCameraViewAndProjectionIntoShader(int shader) {
        Matrix4f view = new Matrix4f();
        Matrix4f projection = new Matrix4f();

        float FOVdeg = 70f;
        float farPlane = 200.0f;
        float nearPlane = 0.1f;

        // Nastaví kameru, aby se dívala správným směrem
        view.lookAt(_position, _position.add(_orientation, new Vector3f()), _up);

        // Nastavení perspektivy
        projection.perspective((float) Math.toRadians(FOVdeg), (float) _width / _height, nearPlane, farPlane);

        // Odeslání matice do shaderu
        int uniformLocationView = GL20.glGetUniformLocation(shader, "view");
        int uniformLocationProjection = GL20.glGetUniformLocation(shader, "projection");
        GL20.glUniformMatrix4fv(uniformLocationView, false, view.get(new float[16]));
        GL20.glUniformMatrix4fv(uniformLocationProjection, false, projection.get(new float[16]));

        int uniformViewPosLocation = GL20.glGetUniformLocation(shader, "viewPos");
        GL20.glUniform3f(uniformViewPosLocation, _position.x, _position.y, _position.z);
    }

    public void processInputs(long window, float deltaTime) {
        // Pohyb dopředu (W)
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            movePosition(new Vector3f(_orientation).mul(_speed * deltaTime));
        }

        // Pohyb vlevo (A)
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            movePosition(new Vector3f(_orientation).cross(_up).normalize().mul(-_speed * deltaTime));
        }

        // Pohyb dozadu (S)
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            movePosition(new Vector3f(_orientation).mul(-_speed * deltaTime));
        }

        // Pohyb vpravo (D)
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            movePosition(new Vector3f(_orientation).cross(_up).normalize().mul(_speed * deltaTime));
        }

        // Pohyb dolů (levý Ctrl)
        if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
            movePosition(new Vector3f(_up).mul(-_speed * deltaTime));
        }

        // Pohyb nahoru (mezerník)
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            movePosition(new Vector3f(_up).mul(_speed * deltaTime));
        }

        // Zrychlení pohybu (levý Shift)
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            _speed = 15.0f;
        } else if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_RELEASE) {
            _speed = 5.0f;
        }

        // Přepnutí orthogonální projekce
        if (glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS && !_cKeyPressedLastFrame) {
            _projection = _isPerspectiveProjection ? _orthogonalProjection : _perspectiveProjection;
            _isPerspectiveProjection = !_isPerspectiveProjection;
        }
        _cKeyPressedLastFrame = glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS;

        // Ovládání myši
        handleMouseInput(window);
    }

    private void handleMouseInput(long window) {

        if (!_clickedIn) {
            if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
                _justClicked = true;
            }
            if (_justClicked && glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_RELEASE) {
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                glfwSetCursorPos(window, _width / 2, _height / 2);
                _clickedIn = true;
                _justClicked = false;
            }
            return;
        }

        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        glfwGetCursorPos(window, mouseX, mouseY);

        float rotX = _sensitivity * (float) (_height / 2 - mouseY[0]) / _height;
        float rotY = _sensitivity * (float) (_width / 2 - mouseX[0]) / _width;

        // Vypočítá novou vertikální orientaci kamery
        Quaternionf rotation = new Quaternionf().rotateAxis((float) Math.toRadians(rotX * 40f),
                new Vector3f(_orientation).cross(_up).normalize());
        Vector3f newOrientation = new Vector3f(_orientation).rotate(rotation);

        // Rozhodne, zda je nová vertikální orientace platná
        if (Math.abs(newOrientation.angle(_up) - Math.toRadians(90.0f)) <= Math.toRadians(85.0f)) {
            _orientation.set(newOrientation);
        }

        glfwSetCursorPos(window, _width / 2, _height / 2);
        _orientation.rotateY(rotY);

        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            _justClicked = true;
        }
        if (_justClicked && glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_RELEASE) {
            glfwSetCursorPos(window, _width / 2, _height / 2);
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            _clickedIn = false;
            _justClicked = false;
        }

    }

    private void movePosition(Vector3f offset) {
        _position.add(offset);
    }

    private void calculatePerspectiveProjection() {
        float FOVdeg = 70f;
        float farPlane = 1000.0f;
        float nearPlane = 0.1f;
        _perspectiveProjection = new Matrix4f().perspective((float) Math.toRadians(FOVdeg), (float) _width / _height,
                nearPlane, farPlane);
    }

    private void calculateOrthogonalProjection() {
        float left = -_width / 2.0f;
        float right = _width / 2.0f;
        float bottom = -_height / 2.0f;
        float top = _height / 2.0f;
        float farPlane = 500.0f;
        float nearPlane = -100f;
        _orthogonalProjection = new Matrix4f().ortho(left, right, bottom, top, nearPlane, farPlane);
    }
}
