package com.example;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class App {
    private long _window;
    private int _shaderProgram;
    private int _vbo;

    public void run() {
        init();
        loop();
        // Při zrušení loopu:

        // Uvolnění bufferů
        glDeleteBuffers(_vbo);

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

        glfwMakeContextCurrent(_window); // Nastavení aktivního okna pro GLFW

        glfwSwapInterval(1); // Povolení vertikální synchronizace

        glfwShowWindow(_window); // Zobrazení okna

        GL.createCapabilities(); // Inicializace OpenGL

        setupKeyCallback();

        try {
            _shaderProgram = createShaderProgram();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Hlavní smyčka
    private void loop() {
        while (!glfwWindowShouldClose(_window)) {
            // Vykreslování
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f); // Nastavení barvy pozadí
            glClear(GL_COLOR_BUFFER_BIT); // Vymazání obrazovky

            // Aktivace shaderového programu
            glUseProgram(_shaderProgram);

            // Definice vrcholů trojúhelníku
            float[] vertices = {
                    // Pozice // Barva
                    0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, // Vrchol 1
                    -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, // Vrchol 2
                    0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f // Vrchol 3
            };

            // Vytvoření vertex buffer objektu (VBO)
            _vbo = glGenBuffers(); // Vytvoří VBO
            glBindBuffer(GL_ARRAY_BUFFER, _vbo); // Připojí VBO
            FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length); // Alokuje paměť
            vertexBuffer.put(vertices).flip(); // Vloží data o vrcholech do bufferu
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW); // Nahraje data z bufferu do GPU
            MemoryUtil.memFree(vertexBuffer); // Uvolní paměť

            // Nastavení atributů
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0); // Pozice: nastavuje typy, offsety, velikosti,..
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES); // Barva: nastavuje typy, offsety, velikosti,..
            glEnableVertexAttribArray(1);

            // Vykreslení trojúhelníku
            glDrawArrays(GL_TRIANGLES, 0, 3);

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
        int vertexShader = loadShader("shaders/vertex_shader.vert", GL_VERTEX_SHADER);
        int fragmentShader = loadShader("shaders/fragment_shader.frag", GL_FRAGMENT_SHADER);

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return shaderProgram;
    }

    // Načtení shaderu ze souboru
    private int loadShader(String file, int type) throws IOException {
        // Čte a ukládá zdrojový kód souboru shaderu řádek po řádku
        StringBuilder shaderSource = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
