#version 330 core

layout(location = 0) in vec3 position; // Pozice vrcholu
layout(location = 1) in vec3 color;    // Barva vrcholu

out vec3 fragColor; // Barva, která se pošle do fragment shaderu
out vec3 fragPosition;
out vec3 fragNormal;
uniform mat4 camMatrix;
uniform mat4 modelMatrix;

uniform float time;

vec3 calculatePos(float u, float v) {
    // Výpočet pozice vrcholu na torusu
    float x = u;
    float y = v;
    float z = 0;

    // Vlnění
    float wave = sin(position.x * 5.0 + time) * 0.3; // 10.0 je frekvence vlny, 0.1 je amplituda
    vec3 transformedPosition = vec3(x,y+ wave,z+wave);

    vec4 point = vec4(transformedPosition.x, transformedPosition.y, transformedPosition.z, 1.0);
    vec4 transformedPoint = modelMatrix * point;

    return transformedPoint.xyz;
}

void main() {
    float u = position.x;
    float v = position.y;

    vec3 finalPosition = calculatePos(u, v);
    float smallvalue = 0.001; // Malý posun pro výpočet derivací
    vec3 neighbour1 = calculatePos(u + smallvalue, v);
    vec3 neighbour2 = calculatePos(u, v + smallvalue);

    vec3 tangent = neighbour1 - finalPosition;
    vec3 bitangent = neighbour2 - finalPosition;
    vec3 displacedNormal = cross(tangent, bitangent);

    gl_Position = camMatrix * vec4(finalPosition, 1.0); // Nastavení pozice

    fragColor = vec3(color.x, color.y, color.z);
    fragPosition = finalPosition;
    fragNormal = normalize(displacedNormal);
}
