#version 330 core

layout(location = 0) in vec3 position; // Pozice vrcholu
layout(location = 1) in vec3 color;    // Barva vrcholu

out vec3 fragColor; // Barva, která se pošle do fragment shaderu
out vec3 fragPosition;
uniform mat4 camMatrix;
uniform float xOffset = 0;

uniform float time;

void main() {
    float r = 3.0; // Poloměr koule

    // Přepočítání souřadnic
    float u = position.x * 2.0 * 3.1415926; // Azimutální úhel (0 až 2π)
    float v = position.y * 3.1415926;       // Zenitální úhel (0 až π)

    // Výpočet pozice vrcholu
    float x = r * sin(v) * cos(u);
    float z = r * sin(v) * sin(u);
    float y = r * cos(v);

    // Vlnění
    float wave = sin(z * 10.0 + time) * 0.2;
    vec3 finalPosition = vec3(x + xOffset, y + wave, z);

    gl_Position = camMatrix * vec4(finalPosition, 1.0); // Nastavení pozice
    fragColor = vec3(0.29 + wave, 0.1 + wave * finalPosition.z, 0.6 + wave);
    fragPosition = finalPosition;
}
