#version 330 core

layout(location = 0) in vec3 position; // Pozice vrcholu
layout(location = 1) in vec3 color;    // Barva vrcholu

out vec3 fragColor; // Barva, která se pošle do fragment shaderu
uniform mat4 camMatrix;

uniform float time;

void main() {
    // Vlnění
    float wave = sin(position.x * 5.0 + time) * 0.3; // 10.0 je frekvence vlny, 0.1 je amplituda
    vec3 modifiedPosition = position + vec3(0.0, wave, wave);

    gl_Position = camMatrix * vec4(modifiedPosition, 1.0); // Nastavení pozice
    fragColor = vec3(color.x + modifiedPosition.z/1.5, color.y + modifiedPosition.z/1.5, color.z + modifiedPosition.z/1.5);
}
