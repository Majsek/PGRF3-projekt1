#version 330 core

layout(location = 0) in vec3 position; // Pozice vrcholu
layout(location = 1) in vec3 color;    // Barva vrcholu

out vec3 fragColor; // Barva, která se pošle do fragment shaderu
uniform mat4 camMatrix;
uniform float xOffset = 0;

uniform float time;

void main() {
    float r = 2.0; // Poloměr válce

    // Přepočítání souřadnic
    float u = position.x * 2.0 * 3.1415926; // Azimutální úhel (0 až 2π)
    float z = position.y * 4.0 - 2.0;      // Výška (převod Y souřadnice do intervalu [-2, 2])

    // Výpočet pozice vrcholu na válci
    float x = r * cos(u);
    float y = r * sin(u);

    // Vlnění
    float wave = sin(u * 5.0 + time) * 0.2;
    vec3 cylinderPosition = vec3(x + xOffset, y + wave, z);

    gl_Position = camMatrix * vec4(cylinderPosition, 1.0); // Nastavení pozice
    fragColor = vec3(0.29 + wave, 0.1 + wave * cylinderPosition.z, 0.6 + wave);
}
