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

    // Shader programs
    private int _shaderProgramDefault;
    private int _shaderProgramSkybox;
    private int _shaderProgramWaveAnimation;
    private int _shaderProgramTorus;

    // Kamera
    private Camera _camera;

    // Objects
    private ArrayList<Mesh> _objects = new ArrayList<>();
    private ArrayList<Mesh> _triangleStripObjects = new ArrayList<>();
    private ArrayList<Mesh> _waveAnimationObjects = new ArrayList<>();
    private Mesh _skybox;

    // Draw modes
    private boolean _drawTriangles = true;
    private boolean _drawLines = false;
    private boolean _drawPoints = false;
    private boolean _drawTriangleStrips = true;
    private int _timeDefaultUniformLocation;
    private int _timeSkyboxUniformLocation;
    private int _timeTorusUniformLocation;
    private int _timeWaveAnimationUniformLocation;
    private int _xOffsetTorusUniformLocation;

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
            _shaderProgramWaveAnimation = createShaderProgram("waveAnimation");
            _shaderProgramTorus = createShaderProgram("torus");
        } catch (IOException e) {
            e.printStackTrace();
        }

        glPointSize(2.0f); // Nastaví velikost bodů

        // Získání ID uniform proměnné z shaderu
        _timeDefaultUniformLocation = glGetUniformLocation(_shaderProgramDefault, "time");
        _timeSkyboxUniformLocation = glGetUniformLocation(_shaderProgramSkybox, "time");

        _timeTorusUniformLocation = glGetUniformLocation(_shaderProgramTorus, "time");
        _timeWaveAnimationUniformLocation = glGetUniformLocation(_shaderProgramWaveAnimation, "time");

        _xOffsetTorusUniformLocation = glGetUniformLocation(_shaderProgramTorus, "xOffset");


        // ============================== OBJEKTY ==============================
        // _objects.add(new Cube(1f));

        // _objects.add(new Cube(500f));
        // _objects.add(new Cube(500f));

        _waveAnimationObjects.add(new TriangleGrid(5f, 7f, 40, 40));
        //_triangleStripObjects.add(new TriangleStripGrid(5f, 7f, 40, 40));

        //_objects.add(new Torus(5f, 10f, 40, 40));
        _objects.add(new TriangleGrid(1, 2, 100, 100));
        _skybox = new Cube(100f);
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

        // SKYBOX
            glUseProgram(_shaderProgramSkybox);
            glUniform1f(_timeSkyboxUniformLocation, currentFrameTime);
            _camera.setCameraViewAndProjectionIntoShader(_shaderProgramSkybox);
            _skybox.draw(GL_TRIANGLES);

        // TORUS
            glUseProgram(_shaderProgramTorus);
            // Pošle uniformy do aktivního shaderu
            glUniform1f(_timeTorusUniformLocation, currentFrameTime);
            glUniform1f(_xOffsetTorusUniformLocation, 10f);

            _camera.setCameraMatrix(70.0f, 0.1f, 1000.0f, _shaderProgramTorus, "camMatrix");
            // Nastavení kamery - předání matic do shaderu
            // Vykreslení objektů
            for (Mesh mesh : _objects) {
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

            
            // Vykreslení objektů typu GL_TRIANGLE_STRIP
            for (Mesh mesh : _triangleStripObjects) {
                if (_drawTriangles) {
                    mesh.draw(GL_TRIANGLES);
                } // Pro plné trojúhelníky
                if (_drawLines) {
                    mesh.draw(GL_LINES);
                } // Pro vykreslení hran
                if (_drawPoints) {
                    mesh.draw(GL_POINTS);
                } // Pro vykreslení bodů
                if (_drawTriangleStrips) {
                    mesh.draw(GL_TRIANGLE_STRIP);
                } // Pro vykreslení triangle strips
            }

            // WAVE ANIMATION
            glUseProgram(_shaderProgramWaveAnimation);
            glUniform1f(_timeWaveAnimationUniformLocation, currentFrameTime);
            _camera.setCameraMatrix(70.0f, 0.1f, 1000.0f, _shaderProgramWaveAnimation, "camMatrix");
            
            // Vykreslení objektů které používají WaveAnimation shader
            for (Mesh mesh : _waveAnimationObjects) {

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

            // Přepínání bufferů
            glfwSwapBuffers(_window);
            glfwPollEvents();
        }
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
