#version 330 core

in vec3 fragColor; // Přijatá interpolovaná barva z vertex shaderu
out vec4 color;    // Výstupní barva pixelu

void main() {
    color = vec4(fragColor, 1.0); // Nastavení barvy
}
