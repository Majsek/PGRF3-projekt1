#version 330 core

layout(location = 0) in vec3 position; // Pozice vrcholu
layout(location = 1) in vec3 color;    // Barva vrcholu

out vec3 fragColor; // Barva, která se pošle do fragment shaderu
uniform mat4 camMatrix;
uniform float xOffset = 0;

uniform float time;

void main() {
    float x = position.x*4;
    float y = position.y*3;
    float z = 0.5 * cos(sqrt(20.0 * x * x + 20.0 * y * y)) * 0.8;

    // Vlnění
    float wave = sin(z * 5.0 + time) * 0.2; // 10.0 je frekvence vlny, 0.1 je amplituda
    vec3 wavePosition = vec3(x + xOffset, y, z+wave);

    gl_Position = camMatrix * vec4(wavePosition, 1.0); // Nastavení pozice
    fragColor = vec3(0.2 + z, 0.4 + z * 0.5, 0.6 + z);
}
