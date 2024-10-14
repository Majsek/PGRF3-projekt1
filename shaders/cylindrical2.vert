#version 330 core

layout(location = 0) in vec3 position; // Pozice vrcholu
layout(location = 1) in vec3 color;    // Barva vrcholu

out vec3 fragColor; // Barva, která se pošle do fragment shaderu
uniform mat4 camMatrix;
uniform float xOffset = 0;
uniform float time;

void main() {
    float baseRadius = 2.0;  // Poloměr základní spirály
    float heightScale = 4.0; // Jak rychle se spirála zvedá

    // Přepočítání souřadnic
    float u = position.x * 2.0 * 3.1415926;
    float z = position.y * heightScale - heightScale / 2.0; // Výška v intervalu [-h/2, h/2]

    float radius = baseRadius + sin(time + u * 2.0) * 0.5; // Změna poloměru podle času a úhlu
    float waveHeight = sin(z * 10.0 + time * 2.0) * 0.5;   // Vlnění výšky v čase

    // Výpočet pozice vrcholu na spirále
    float x = radius * cos(u);
    float y = radius * sin(u);

    vec3 finalPosition = vec3(x + xOffset, y, z + waveHeight);

    gl_Position = camMatrix * vec4(finalPosition, 1.0); // Nastavení pozice
    fragColor = vec3(0.5 + sin(time + u), 0.3 + waveHeight, 0.8 + cos(u));
}
