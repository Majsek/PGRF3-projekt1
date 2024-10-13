#version 330 core

layout(location = 0) in vec3 position; // Pozice vrcholu
layout(location = 1) in vec3 color;    // Barva vrcholu

out vec3 fragPos;

uniform mat4 view;
uniform mat4 projection;

void main() {
    // Odstranění translační části matice kamery
    mat4 viewNoTranslation = mat4(mat3(view));

    vec3 rotated = vec3(position.x, position.z, position.y);
    fragPos = position;
    gl_Position = projection * viewNoTranslation * vec4(rotated, 1.0); // Nastavení pozice

}
