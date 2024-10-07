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
    private int _shaderProgram;

    // Kamera
    private Camera _camera;

    // Objects
    private ArrayList<Mesh> _objects = new ArrayList<>();
    private ArrayList<Mesh> _triangleStripObjects = new ArrayList<>();

    // Draw modes
    private boolean _drawTriangles = true;
    private boolean _drawLines = false;
    private boolean _drawPoints = false;
    private boolean _drawTriangleStrips = true;

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
        _window = glfwCreateWindow(800, 600, "PGRF3 - LWJGL projekt1 - Minařík Matěj - minarma1@uhk.cz", NULL, NULL);
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
                800, 600 // rozměry okna
        );

        glfwMakeContextCurrent(_window); // Nastavení aktivního okna pro GLFW
        glfwSwapInterval(1); // Povolení vertikální synchronizace
        glfwShowWindow(_window); // Zobrazení okna

        GL.createCapabilities(); // Inicializace OpenGL

        setupKeyCallback(); // Nastavení input callbacků

        try {
            _shaderProgram = createShaderProgram();
        } catch (IOException e) {
            e.printStackTrace();
        }

        glPointSize(8.0f); // Nastaví velikost bodů

        // _objects.add(new Cube(1f));

        // _objects.add(new Cube(500f));
        // _objects.add(new Cube(500f));

        _objects.add(new TriangleGrid(5f, 10f, 10, 10));
        //_triangleStripObjects.add(new TriangleStripGrid(5f, 10f, 10, 10));

    }

    // Hlavní smyčka
    private void loop() {
        float lastFrameTime = 0.0f;

        while (!glfwWindowShouldClose(_window)) {

            // Delta času
            float currentFrameTime = (float) glfwGetTime();
            float deltaTime = currentFrameTime - lastFrameTime;
            lastFrameTime = currentFrameTime;

            // Zpracování vstupů kamery
            _camera.processInputs(_window, deltaTime);

            // Vykreslování
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f); // Nastavení barvy pozadí
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Vymazání obrazovky

            // Aktivace shaderového programu
            glUseProgram(_shaderProgram);

            glEnable(GL_DEPTH_TEST); // Povolení hloubkového testu

            // Nastavení kamery - předání matic do shaderu
            _camera.setCameraMatrix(70.0f, 0.1f, 1000.0f, _shaderProgram, "camMatrix");

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
    private int createShaderProgram() throws IOException {
        int vertexShader = loadShaderFromPath("shaders/vertex_shader.vert", GL_VERTEX_SHADER);
        int fragmentShader = loadShaderFromPath("shaders/fragment_shader.frag", GL_FRAGMENT_SHADER);

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
