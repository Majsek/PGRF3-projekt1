#version 330 core

layout(location = 0) in vec3 position; // Pozice vrcholu
layout(location = 1) in vec3 color;    // Barva vrcholu

out vec3 fragColor; // Barva, která se pošle do fragment shaderu
uniform mat4 camMatrix;
uniform float xOffset = 0;

uniform float time;

void main() {
    float R = 3.0;
    float r = 1.0;

    // Souřadnice (X, Y) gridu budou reprezentovat úhly u a v
    float u = position.x * 2.0 * 3.1415926; // Převod X souřadnice do úhlu (0 až 2π)
    float v = position.y * 2.0 * 3.1415926; // Převod Y souřadnice do úhlu (0 až 2π)

    // Výpočet pozice vrcholu na torusu
    float x = (R + r * cos(v)) * cos(u);
    float z = r * sin(v);
    float y = (R + r * cos(v)) * sin(u);

    // Vlnění
    float wave = sin(z * 1000.0 + time) * 0.8; // 10.0 je frekvence vlny, 0.1 je amplituda
    float wave2 = sin(z * 10.0 + time) * 0.2;

    vec3 torusPosition = vec3(x + xOffset, y, z + wave2);

    gl_Position = camMatrix * vec4(torusPosition, 1.0); // Nastavení pozice
    fragColor = vec3(0.29 + wave, 0.1 + wave * torusPosition.z, 0.6 + wave);
    //fragColor = vec3(color.x, color.y, color.z);
    //fragColor = vec3(0.1,0.6,1.0);
}
