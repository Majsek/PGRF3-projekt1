#version 330 core

layout(location = 0) in vec3 position; // Pozice vrcholu
layout(location = 1) in vec3 color;    // Barva vrcholu

out vec3 fragColor; // Barva, která se pošle do fragment shaderu
out vec3 fragPosition;
out vec3 fragNormal;
uniform mat4 camMatrix;
uniform mat4 modelMatrix;

uniform float time;
float _wave;
float _wave2;

vec3 calculatePos(float u, float v) {
    float R = 3.0; // Vzdálenost od středu torusu k centru díry
    float r = 1.0; // Poloměr torusu

    // Výpočet pozice vrcholu na torusu
    float x = (R + r * cos(v)) * cos(u);
    float y = (R + r * cos(v)) * sin(u);
    float z = r * sin(v);

    vec4 point = vec4(x, y, z + _wave2, 1.0);
    vec4 transformedPoint = modelMatrix * point;

    return transformedPoint.xyz;
}

void main() {
    // Souřadnice (X, Y) gridu budou reprezentovat úhly u a v
    float u = position.x * 2.0 * 3.1415926; // Převod X souřadnice do úhlu (0 až 2π)
    float v = position.y * 2.0 * 3.1415926; // Převod Y souřadnice do úhlu (0 až 2π)

    // Vlnění
    float angle = sin(v);
    _wave = sin(angle * 1000.0 + time) * 0.8; // 10.0 je frekvence vlny, 0.1 je amplituda
    _wave2 = sin(angle * 10.0 + time) * 0.2;


    vec3 finalPosition = calculatePos(u, v);
    float smallvalue = 0.01; // Malý posun pro výpočet derivací
    vec3 neighbour1 = calculatePos(u + smallvalue, v);
    vec3 neighbour2 = calculatePos(u, v + smallvalue);

    vec3 tangent = neighbour1 - finalPosition;
    vec3 bitangent = neighbour2 - finalPosition;
    vec3 displacedNormal = cross(tangent, bitangent);

    gl_Position = camMatrix * vec4(finalPosition, 1.0); // Nastavení pozice

    fragColor = vec3( + 0.29 + _wave, 0.1 + _wave, 0.6 + _wave);
    fragPosition = finalPosition;
    fragNormal = displacedNormal;
    fragPosition = finalPosition;
}
