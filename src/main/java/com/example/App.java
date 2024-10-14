package com.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.example.objects.Cube;
import com.example.objects.Mesh;
import com.example.objects.TriangleGrid;
import com.example.objects.TriangleStripGrid;

public class App {
    private long _window;

    // SHADER PROGRAMS
    private int _shaderProgramDefault;
    private int _shaderProgramSkybox;

    // Kartézské
    private int _shaderProgramWaveAnimation;
    private int _shaderProgramPlaneWave;

    // Sférické
    private int _shaderProgramTorus;
    private int _shaderProgramSphericalRandom;

    // Cylindrické
    private int _shaderProgramCylindrical1;
    private int _shaderProgramCylindrical2;

    // Kamera
    private Camera _camera;

    // Objects
    private ArrayList<Mesh> _objects = new ArrayList<>();
    private ArrayList<Mesh> _triangleStripObjects = new ArrayList<>();
    private ArrayList<Mesh> _waveAnimationObjects = new ArrayList<>();
    private ArrayList<Mesh> _planeWaveObjects = new ArrayList<>();

    private Mesh _skybox;

    // Kartézské
    private Mesh _waveAnimation;
    private Mesh _planeWave;

    // Sférické
    private Mesh _torus;
    private Mesh _sphericalRandom;

    // Cylindrické
    private Mesh _cylindrical1;
    private Mesh _cylindrical2;

    // Draw modes
    private boolean _drawTriangles = true;
    private boolean _drawLines = false;
    private boolean _drawPoints = false;
    private boolean _drawTriangleStrips = true;

    // UNIFORMS
    // default
    private int _timeDefaultUniformLocation;
    // skybox
    private int _timeSkyboxUniformLocation;

    // Kartézské
    private int _timeWaveAnimationUniformLocation;

    private int _timePlaneWaveUniformLocation;
    private int _xOffsetPlaneWaveUniformLocation;

    // Sférické
    private int _timeTorusUniformLocation;
    private int _xOffsetTorusUniformLocation;

    private int _timeSphericalRandomUniformLocation;
    private int _xOffsetSphericalRandomUniformLocation;

    // Cylindrické
    private int _timeCylindrical1UniformLocation;
    private int _xOffsetCylindrical1UniformLocation;

    private int _timeCylindrical2UniformLocation;
    private int _xOffsetCylindrical2UniformLocation;

    public void run() {
        init();
        loop();
        // Po zrušení loopu:

        // Uvolnění bufferů
        for (Mesh mesh : _objects) {
            mesh.cleanup();
        }
        for (Mesh mesh : _triangleStripObjects) {
            mesh.cleanup();
        }

        for (Mesh mesh : _planeWaveObjects) {
            mesh.cleanup();
        }

        // Uvolnění okna a terminace GLFW
        glfwDestroyWindow(_window);
        glfwTerminate();
    }

    private void init() {
        // Inicializace GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Nelze inicializovat GLFW");
        }

        // Nastavení pro okno
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Skrytí okna

        // Vytvoření okna
        _window = glfwCreateWindow(1400, 900, "PGRF3 - LWJGL projekt1 - Minařík Matěj - minarma1@uhk.cz", NULL, NULL);
        if (_window == NULL) {
            throw new IllegalStateException("Nelze vytvořit okno");
        }

        // Centrování okna
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(_window, pWidth, pHeight);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(_window,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2);
        }

        // Inicializace kamery
        _camera = new Camera(
                new Vector3f(0.0f, 0.0f, 3.0f), // pozice kamery
                new Vector3f(0.0f, 0.0f, -1.0f), // směr kamery
                new Vector3f(0.0f, 1.0f, 0.0f), // up vektor
                1400, 900 // rozměry okna
        );

        glfwMakeContextCurrent(_window); // Nastavení aktivního okna pro GLFW
        glfwSwapInterval(1); // Povolení vertikální synchronizace
        glfwShowWindow(_window); // Zobrazení okna

        GL.createCapabilities(); // Inicializace OpenGL

        setupKeyCallback(); // Nastavení input callbacků

        try {
            _shaderProgramDefault = createShaderProgram("default");
            _shaderProgramSkybox = createShaderProgram("skybox");

            // Kartézské
            _shaderProgramWaveAnimation = createShaderProgram("waveAnimation");
            _shaderProgramPlaneWave = createShaderProgram("planeWave");

            // Sférické
            _shaderProgramTorus = createShaderProgram("torus");
            _shaderProgramSphericalRandom = createShaderProgram("sphericalRandom");

            // Cylindrické
            _shaderProgramCylindrical1 = createShaderProgram("cylindrical1");
            _shaderProgramCylindrical2 = createShaderProgram("cylindrical2");

        } catch (IOException e) {
            e.printStackTrace();
        }

        glPointSize(2.0f); // Nastaví velikost bodů

        // Získání ID uniform proměnné z shaderu
        _timeDefaultUniformLocation = glGetUniformLocation(_shaderProgramDefault, "time");
        _timeSkyboxUniformLocation = glGetUniformLocation(_shaderProgramSkybox, "time");

        // Kartézské
        _timeWaveAnimationUniformLocation = glGetUniformLocation(_shaderProgramWaveAnimation, "time");

        _timePlaneWaveUniformLocation = glGetUniformLocation(_shaderProgramPlaneWave, "time");
        _xOffsetPlaneWaveUniformLocation = glGetUniformLocation(_shaderProgramPlaneWave, "xOffset");

        // Sférické
        _timeTorusUniformLocation = glGetUniformLocation(_shaderProgramTorus, "time");
        _xOffsetTorusUniformLocation = glGetUniformLocation(_shaderProgramTorus, "xOffset");

        _timeSphericalRandomUniformLocation = glGetUniformLocation(_shaderProgramSphericalRandom, "time");
        _xOffsetSphericalRandomUniformLocation = glGetUniformLocation(_shaderProgramSphericalRandom, "xOffset");

        // Cylindrické
        _timeCylindrical1UniformLocation = glGetUniformLocation(_shaderProgramCylindrical1, "time");
        _xOffsetCylindrical1UniformLocation = glGetUniformLocation(_shaderProgramCylindrical1, "xOffset");

        _timeCylindrical2UniformLocation = glGetUniformLocation(_shaderProgramCylindrical2, "time");
        _xOffsetCylindrical2UniformLocation = glGetUniformLocation(_shaderProgramCylindrical2, "xOffset");

        // ============================== OBJEKTY ==============================

        _skybox = new Cube(100f);

        // --------- KARTÉZSKÉ ---------
        // Wave animation
        _waveAnimationObjects.add(new TriangleGrid(5f, 7f, 40, 40));

        // Plane wave
        _planeWaveObjects.add(new TriangleGrid(1, 1, 100, 100));

        // Triangle strip grid
        // _triangleStripObjects.add(new TriangleStripGrid(5f, 7f, 40, 40));

        // --------- SFÉRICKÉ ---------
        // Torus
        _objects.add(new TriangleGrid(1, 2, 100, 100));

        // Spherical random
        _sphericalRandom = new TriangleGrid(1, 2, 300, 300);

        // --------- CYLINDRICKÉ ---------
        // Spherical random
        _cylindrical1 = new TriangleGrid(1, 2, 100, 100);

        // Spherical random
        _cylindrical2 = new TriangleGrid(1, 2, 10, 100);
    }

    // Hlavní smyčka
    private void loop() {
        float lastFrameTime = 0.0f;

        while (!glfwWindowShouldClose(_window)) {

            // Delta času
            float currentFrameTime = (float) glfwGetTime();
            float deltaTime = currentFrameTime - lastFrameTime;
            lastFrameTime = currentFrameTime;

            // Vykreslování
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f); // Nastavení barvy pozadí
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Vymazání obrazovky
            glEnable(GL_DEPTH_TEST); // Povolení hloubkového testu

            // Zpracování vstupů kamery
            _camera.processInputs(_window, deltaTime);

            // ----------------------------- SKYBOX -----------------------------
            glUseProgram(_shaderProgramSkybox);
            glUniform1f(_timeSkyboxUniformLocation, currentFrameTime);
            // Nastavení kamery - předání matic do shaderu
            _camera.setCameraViewAndProjectionIntoShader(_shaderProgramSkybox);
            // Vykreslení objektu
            _skybox.draw(GL_TRIANGLES);

            // ----------------------------- TORUS -----------------------------
            glUseProgram(_shaderProgramTorus);
            // Pošle uniformy do aktivního shaderu
            glUniform1f(_timeTorusUniformLocation, currentFrameTime);
            glUniform1f(_xOffsetTorusUniformLocation, 20f);

            // Nastavení kamery - předání matic do shaderu
            _camera.setCameraMatrixIntoShader(_shaderProgramTorus);

            // Vykreslení objektů
            for (Mesh mesh : _objects) {
                drawMesh(mesh);
            }

            // ----------------------------- SPHERICAL RANDOM -----------------------------
            glUseProgram(_shaderProgramSphericalRandom);
            // Pošle uniformy do aktivního shaderu
            glUniform1f(_timeSphericalRandomUniformLocation, currentFrameTime);
            glUniform1f(_xOffsetSphericalRandomUniformLocation, 30f);

            // Nastavení kamery - předání matic do shaderu
            _camera.setCameraMatrixIntoShader(_shaderProgramSphericalRandom);

            // Vykreslení objektu
            drawMesh(_sphericalRandom);

            // ----------------------------- CYLINDRICAL1 -----------------------------
            glUseProgram(_shaderProgramCylindrical1);
            // Pošle uniformy do aktivního shaderu
            glUniform1f(_timeCylindrical1UniformLocation, currentFrameTime);
            glUniform1f(_xOffsetCylindrical1UniformLocation, 40f);

            // Nastavení kamery - předání matic do shaderu
            _camera.setCameraMatrixIntoShader(_shaderProgramCylindrical1);

            // Vykreslení objektu
            drawMesh(_cylindrical1);

            // ----------------------------- CYLINDRICAL2 -----------------------------
            glUseProgram(_shaderProgramCylindrical2);
            // Pošle uniformy do aktivního shaderu
            glUniform1f(_timeCylindrical2UniformLocation, currentFrameTime);
            glUniform1f(_xOffsetCylindrical2UniformLocation, 50f);

            // Nastavení kamery - předání matic do shaderu
            _camera.setCameraMatrixIntoShader(_shaderProgramCylindrical2);

            // Vykreslení objektu
            drawMesh(_cylindrical2);

            // ----------------------------- PlANE WAVE -----------------------------
            glUseProgram(_shaderProgramPlaneWave);
            // Pošle uniformy do aktivního shaderu
            glUniform1f(_timePlaneWaveUniformLocation, currentFrameTime);
            glUniform1f(_xOffsetPlaneWaveUniformLocation, 10f);

            _camera.setCameraMatrixIntoShader(_shaderProgramPlaneWave);
            // Nastavení kamery - předání matic do shaderu
            // Vykreslení objektů
            for (Mesh mesh : _objects) {
                drawMesh(mesh);
            }

            // Vykreslení objektů typu GL_TRIANGLE_STRIP
            for (Mesh mesh : _triangleStripObjects) {
                drawMesh(mesh);
                if (_drawTriangleStrips) {
                    mesh.draw(GL_TRIANGLE_STRIP);
                } // Pro vykreslení triangle strips
            }

            // WAVE ANIMATION
            glUseProgram(_shaderProgramWaveAnimation);
            glUniform1f(_timeWaveAnimationUniformLocation, currentFrameTime);
            _camera.setCameraMatrixIntoShader(_shaderProgramWaveAnimation);

            // Vykreslení objektů které používají WaveAnimation shader
            for (Mesh mesh : _waveAnimationObjects) {
                drawMesh(mesh);
            }

            // Přepínání bufferů
            glfwSwapBuffers(_window);
            glfwPollEvents();
        }
    }

    private void drawMesh(Mesh mesh) {
        if (_drawTriangles) {
            mesh.draw(GL_TRIANGLES);
        } // Pro plné trojúhelníky
        if (_drawLines) {
            mesh.draw(GL_LINES);
        } // Pro vykreslení hran
        if (_drawPoints) {
            mesh.draw(GL_POINTS);
        } // Pro vykreslení bodů
    }

    // Nastaví key callbacky
    private void setupKeyCallback() {
        glfwSetKeyCallback(_window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true); // Zavře okno
            } else if (key == GLFW_KEY_R && action == GLFW_PRESS) {
                restart(); // Restart aplikace
            }

            if (key == GLFW_KEY_I && action == GLFW_PRESS) {
                _drawTriangles = !_drawTriangles;
            }
            if (key == GLFW_KEY_O && action == GLFW_PRESS) {
                _drawLines = !_drawLines;
            }
            if (key == GLFW_KEY_P && action == GLFW_PRESS) {
                _drawPoints = !_drawPoints;
            }
            if (key == GLFW_KEY_U && action == GLFW_PRESS) {
                _drawTriangleStrips = !_drawTriangleStrips;
            }
        });
    }

    // Zničí a znovu inicializuje okno a spustí loop
    private void restart() {
        glfwDestroyWindow(_window);
        init();
    }

    // Vytvoří a vrátí shader program z shader souborů
    private int createShaderProgram(String shaderName) throws IOException {
        int vertexShader = loadShaderFromPath("shaders/" + shaderName + ".vert", GL_VERTEX_SHADER);
        int fragmentShader = loadShaderFromPath("shaders/" + shaderName + ".frag", GL_FRAGMENT_SHADER);

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return shaderProgram;
    }

    // Načtení shaderu ze souboru
    private int loadShaderFromPath(String filePath, int type) throws IOException {
        // Čte a ukládá zdrojový kód souboru shaderu řádek po řádku
        StringBuilder shaderSource = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
        }

        // Nastaví do shaderu zdrojový kód a zkompiluje ho
        int shader = glCreateShader(type);
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);

        // Exception při chybné kompilaci
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Failed to compile shader: " + glGetShaderInfoLog(shader));
        }

        // Vrací ID shaderu
        return shader;
    }

}
