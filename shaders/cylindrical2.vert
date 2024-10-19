#version 330 core

layout(location = 0) in vec3 position; // Pozice vrcholu
layout(location = 1) in vec3 color;    // Barva vrcholu

out vec3 fragNormal;
out vec3 fragPosition;
out vec3 fragColor;
// Uniformy
uniform float time;
uniform mat4 camMatrix;
uniform mat4 modelMatrix;

float _waveHeight;

vec3 calculatePos(float u, float v) {
    float baseRadius = 2.0;  // Poloměr základní spirály
    float heightScale = 4.0; // Jak rychle se spirála zvedá

    float angle1 = u * 2.0 * 3.1415926;
    float z = v * heightScale - heightScale / 2.0; // Výška v intervalu [-h/2, h/2]

    float radius = baseRadius + sin(time + angle1 * 2.0) * 0.5; // Změna poloměru podle času a úhlu
    _waveHeight = sin(z * 10.0 + time * 2.0) * 0.5;   // Vlnění výšky v čase

    float x = radius * cos(angle1);
    float y = radius * sin(angle1);

    vec3 transformedPosition = vec3(x, y, z + _waveHeight);

    vec4 point = vec4(transformedPosition.x, transformedPosition.y, transformedPosition.z, 1.0);
    vec4 transformedPoint = modelMatrix * point;

    return transformedPoint.xyz;
}

void main() {
    float u = position.x;
    float v = position.y;

    vec3 finalPosition = calculatePos(u, v);
    float smallvalue = 0.01; // Malý posun pro výpočet derivací
    vec3 neighbour1 = calculatePos(u + smallvalue, v);
    vec3 neighbour2 = calculatePos(u, v + smallvalue);

    vec3 tangent = neighbour1 - finalPosition;
    vec3 bitangent = neighbour2 - finalPosition;
    vec3 displacedNormal = cross(tangent, bitangent);

    gl_Position = camMatrix * vec4(finalPosition, 1.0); // Nastavení pozice

    fragColor = vec3(0.5 + sin(time + u), 0.3 + _waveHeight, 0.8 + cos(u));
    fragPosition = finalPosition;
    fragNormal = displacedNormal;
}
