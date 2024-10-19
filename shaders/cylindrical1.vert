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

float _wave;

vec3 calculatePos(float u, float v) {
    float r = 2.0; // Poloměr válce

    float angle1 = u * 2.0 * 3.1415926; // Azimutální úhel (0 až 2π)

    float x = r * cos(angle1);
    float y = r * sin(angle1);
    float z = v * 4.0 - 2.0;      // Výška (převod Y souřadnice do intervalu [-2, 2])

    // Vlnění
    _wave = sin(angle1 * 5.0 + time) * 0.2;
    vec3 transformedPosition = vec3(x, y + _wave, z);


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

    fragColor = vec3(0.29 + _wave, 0.1 + _wave * finalPosition.z, 0.6 + _wave);
    fragPosition = finalPosition;
    fragNormal = displacedNormal;

}
