#version 330 core

layout(location = 0) in vec3 position; // Pozice vrcholu
layout(location = 1) in vec3 color;    // Barva vrcholu

out vec3 fragColor; // Barva, která se pošle do fragment shaderu
uniform mat4 camMatrix;

uniform float time;

void main() {

    gl_Position = camMatrix * vec4(position, 1.0); // Nastavení pozice
    fragColor = vec3(color.x + position.z/1.5, color.y + position.z/1.5, color.z + position.z/1.5);
}
