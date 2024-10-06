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

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.example.objects.Mesh;

public class App {
    private long _window;
    private int _shaderProgram;
    private Mesh _mesh1;
    private Mesh _mesh2;
    private IBO _ibo;

    // Kamera
    private Camera camera;

    public void run() {
        init();
        loop();
        // Po zrušení loopu:

        // Uvolnění bufferů
        _mesh1.cleanup();
        _mesh2.cleanup();

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
        camera = new Camera(
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

        // Definice vrcholů jehlanu
        float[] vertices1 = {
                // Pozice // Barva
                0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, // Vrchol 1
                -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, // Vrchol 2
                0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, // Vrchol 3
                0.0f, -0.5f, 0.5f, 0.0f, 1.0f, 1.0f // Vrchol 4
        };

        // Definice vrcholů jehlanu
        float[] vertices2 = {
                // Pozice // Barva
                1.0f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, // Vrchol 1
                0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, // Vrchol 2
                1.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, // Vrchol 3
                1.0f, -0.5f, 0.5f, 0.0f, 1.0f, 1.0f // Vrchol 4
        };

        // Definice indexů jehlanu
        int[] indices1 = new int[] {
                0, 1, 2, // První trojúhelník
                0, 2, 3, // Druhý trojúhelník
                0, 3, 1, // Třetí trojúhelník
                1, 2, 3 // Čtvrtý trojúhelník
        };

        int[] indices2 = new int[] {
            0, 1, 3, // První trojúhelník
            2, 1, 3, // Druhý trojúhelník
            2, 3, 1, // Třetí trojúhelník
            1, 2, 3 // Čtvrtý trojúhelník
    };

        // Vytvoření vertex buffer objektu (VBO)
        _mesh1 = new Mesh(vertices1, indices1);// Vytvoří VBO
        _mesh2 = new Mesh(vertices2, indices2);// Vytvoří VBO
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
            camera.processInputs(_window, deltaTime);

            // Vykreslování
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f); // Nastavení barvy pozadí
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Vymazání obrazovky

            // Aktivace shaderového programu
            glUseProgram(_shaderProgram);
            
            glEnable(GL_DEPTH_TEST); // Povolení hloubkového testu
            
            // Nastavení kamery - předání matic do shaderu
            camera.setCameraMatrix(70.0f, 0.1f, 100.0f, _shaderProgram, "camMatrix");
            
            // Vykreslení jehlanu
            _mesh1.draw();
            _mesh2.draw();

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
