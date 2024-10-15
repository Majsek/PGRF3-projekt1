package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.example.objects.Cube;
import com.example.objects.Mesh;
import com.example.objects.TriangleGrid;

public class App {
    private long _window;

    // SHADER PROGRAMS
    private int _shaderProgramDefault;
    private int _shaderProgramSkybox;

    // Kartézské
    private ArrayList<Integer> _shaderProgramsWaveAnimation = new ArrayList<>();
    private ArrayList<Integer> _shaderProgramsPlaneWave = new ArrayList<>();

    // Sférické
    private ArrayList<Integer> _shaderProgramsTorus = new ArrayList<>();
    private ArrayList<Integer> _shaderProgramsSphericalRandom = new ArrayList<>();

    // Cylindrické
    private ArrayList<Integer> _shaderProgramsCylindrical1 = new ArrayList<>();
    private ArrayList<Integer> _shaderProgramsCylindrical2 = new ArrayList<>();

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
    private ArrayList<Integer> _timeWaveAnimationUniformLocations = new ArrayList<>();

    private ArrayList<Integer> _timePlaneWaveUniformLocations = new ArrayList<>();
    private ArrayList<Integer> _xOffsetPlaneWaveUniformLocations = new ArrayList<>();

    // Sférické
    private ArrayList<Integer> _timeTorusUniformLocations = new ArrayList<>();
    private ArrayList<Integer> _xOffsetTorusUniformLocations = new ArrayList<>();

    private ArrayList<Integer> _timeSphericalRandomUniformLocations = new ArrayList<>();
    private ArrayList<Integer> _xOffsetSphericalRandomUniformLocations = new ArrayList<>();

    // Cylindrické
    private ArrayList<Integer> _timeCylindrical1UniformLocations = new ArrayList<>();
    private ArrayList<Integer> _xOffsetCylindrical1UniformLocations = new ArrayList<>();

    private ArrayList<Integer> _timeCylindrical2UniformLocations = new ArrayList<>();
    private ArrayList<Integer> _xOffsetCylindrical2UniformLocations = new ArrayList<>();

    private int _shaderMode = 0;
    private int _shaderModeMax;

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
            _shaderProgramsWaveAnimation.add(createShaderProgram("waveAnimation"));
            _shaderProgramsPlaneWave.add(createShaderProgram("planeWave"));

            // Sférické
            _shaderProgramsTorus.add(createShaderProgram("torus"));
            _shaderProgramsSphericalRandom.add(createShaderProgram("sphericalRandom"));

            // Cylindrické
            _shaderProgramsCylindrical1.add(createShaderProgram("cylindrical1"));
            _shaderProgramsCylindrical2.add(createShaderProgram("cylindrical2"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        glPointSize(2.0f); // Nastaví velikost bodů

        // Získání ID uniform proměnné z shaderu
        _timeDefaultUniformLocation = glGetUniformLocation(_shaderProgramDefault, "time"); 
        _timeSkyboxUniformLocation = glGetUniformLocation(_shaderProgramSkybox, "time");

        _shaderModeMax = _shaderProgramsWaveAnimation.size() - 1;
        for (int i = 0; i < _shaderProgramsWaveAnimation.size(); i++) {
            // Kartézské
            _timeWaveAnimationUniformLocations.add(glGetUniformLocation(_shaderProgramsWaveAnimation.get(i), "time"));

            _timePlaneWaveUniformLocations.add(glGetUniformLocation(_shaderProgramsPlaneWave.get(i), "time"));
            _xOffsetPlaneWaveUniformLocations.add(glGetUniformLocation(_shaderProgramsPlaneWave.get(i), "xOffset"));

            // Sférické
            _timeTorusUniformLocations.add(glGetUniformLocation(_shaderProgramsTorus.get(i), "time"));
            _xOffsetTorusUniformLocations.add(glGetUniformLocation(_shaderProgramsTorus.get(i), "xOffset"));

            _timeSphericalRandomUniformLocations.add(glGetUniformLocation(_shaderProgramsSphericalRandom.get(i), "time"));
            _xOffsetSphericalRandomUniformLocations.add(glGetUniformLocation(_shaderProgramsSphericalRandom.get(i),
                    "xOffset"));

            // Cylindrické
            _timeCylindrical1UniformLocations.add(glGetUniformLocation(_shaderProgramsCylindrical1.get(i), "time"));
            _xOffsetCylindrical1UniformLocations.add(glGetUniformLocation(_shaderProgramsCylindrical1.get(i), "xOffset"));

            _timeCylindrical2UniformLocations.add(glGetUniformLocation(_shaderProgramsCylindrical2.get(i), "time"));
            _xOffsetCylindrical2UniformLocations.add(glGetUniformLocation(_shaderProgramsCylindrical2.get(i), "xOffset"));
        }

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

            glUseProgram(_shaderProgramsTorus.get(_shaderMode));
            // Pošle uniformy do aktivního shaderu
            glUniform1f(_timeTorusUniformLocations.get(_shaderMode), currentFrameTime);
            glUniform1f(_xOffsetTorusUniformLocations.get(_shaderMode), 20f);

            // Nastavení kamery - předání matic do shaderu
            _camera.setCameraMatrixIntoShader(_shaderProgramsTorus.get(_shaderMode));

            // Vykreslení objektů
            for (Mesh mesh : _objects) {
                drawMesh(mesh);
            }

            // ----------------------------- SPHERICAL RANDOM -----------------------------
            glUseProgram(_shaderProgramsSphericalRandom.get(_shaderMode));
            // Pošle uniformy do aktivního shaderu
            glUniform1f(_timeSphericalRandomUniformLocations.get(_shaderMode), currentFrameTime);
            glUniform1f(_xOffsetSphericalRandomUniformLocations.get(_shaderMode), 30f);

            // Nastavení kamery - předání matic do shaderu
            _camera.setCameraMatrixIntoShader(_shaderProgramsSphericalRandom.get(_shaderMode));

            // Vykreslení objektu
            drawMesh(_sphericalRandom);

            // ----------------------------- CYLINDRICAL1 -----------------------------
            glUseProgram(_shaderProgramsCylindrical1.get(_shaderMode));
            // Pošle uniformy do aktivního shaderu
            glUniform1f(_timeCylindrical1UniformLocations.get(_shaderMode), currentFrameTime);
            glUniform1f(_xOffsetCylindrical1UniformLocations.get(_shaderMode), 40f);

            // Nastavení kamery - předání matic do shaderu
            _camera.setCameraMatrixIntoShader(_shaderProgramsCylindrical1.get(_shaderMode));

            // Vykreslení objektu
            drawMesh(_cylindrical1);

            // ----------------------------- CYLINDRICAL2 -----------------------------
            glUseProgram(_shaderProgramsCylindrical2.get(_shaderMode));
            // Pošle uniformy do aktivního shaderu
            glUniform1f(_timeCylindrical2UniformLocations.get(_shaderMode), currentFrameTime);
            glUniform1f(_xOffsetCylindrical2UniformLocations.get(_shaderMode), 50f);

            // Nastavení kamery - předání matic do shaderu
            _camera.setCameraMatrixIntoShader(_shaderProgramsCylindrical2.get(_shaderMode));

            // Vykreslení objektu
            drawMesh(_cylindrical2);

            // ----------------------------- PlANE WAVE -----------------------------
            glUseProgram(_shaderProgramsPlaneWave.get(_shaderMode));
            // Pošle uniformy do aktivního shaderu
            glUniform1f(_timePlaneWaveUniformLocations.get(_shaderMode), currentFrameTime);
            glUniform1f(_xOffsetPlaneWaveUniformLocations.get(_shaderMode), 10f);

            _camera.setCameraMatrixIntoShader(_shaderProgramsPlaneWave.get(_shaderMode));
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
            glUseProgram(_shaderProgramsWaveAnimation.get(_shaderMode));
            glUniform1f(_timeWaveAnimationUniformLocations.get(_shaderMode), currentFrameTime);
            _camera.setCameraMatrixIntoShader(_shaderProgramsWaveAnimation.get(_shaderMode));

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
            if (key == GLFW_KEY_Q && action == GLFW_PRESS) {
                changeShaderMode(-1);
            }
            if (key == GLFW_KEY_E && action == GLFW_PRESS) {
                changeShaderMode(+1);
            }
        });
    }

    private void changeShaderMode(int how){
        int newMode = _shaderMode+how;
        if (newMode < 0 || newMode > _shaderModeMax) {
            return;
        }
        _shaderMode += how;
        glfwSetWindowTitle(_window, "Shader mode: " + _shaderMode);
    }

    // Zničí a znovu inicializuje okno a spustí loop
    private void restart() {
        glfwDestroyWindow(_window);
        init();
    }

    // Vytvoří a vrátí shader program z shader souborů
    private int createShaderProgram(String vertShaderName, String fragShaderName) throws IOException {
        int vertexShader = loadShaderFromPath("shaders/" + vertShaderName + ".vert", GL_VERTEX_SHADER);
        int fragmentShader = loadShaderFromPath("shaders/" + fragShaderName + ".frag", GL_FRAGMENT_SHADER);

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return shaderProgram;
    }

    private int createShaderProgram(String shaderName) throws IOException {
        return createShaderProgram(shaderName, shaderName);
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
