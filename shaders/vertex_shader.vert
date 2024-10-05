#version 330 core

layout(location = 0) in vec3 position; // Pozice vrcholu
layout(location = 1) in vec3 color;    // Barva vrcholu

out vec3 fragColor; // Barva, která se pošle do fragment shaderu
uniform mat4 camMatrix;

void main() {
    gl_Position = camMatrix * vec4(position, 1.0); // Nastavení pozice
    fragColor = color;
}
