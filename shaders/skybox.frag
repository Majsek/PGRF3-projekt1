#version 330 core

// Vstupy z vertex shaderu (pozice, normály, atd.)
in vec3 fragPos;

// Výstup barev
out vec4 FragColor;

// Uniformy
uniform float time; // čas pro animaci mraků

// Funkce pro generování "noise" (zjednodušená verze pro mraky)
float noise(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

// Funkce pro interpolovaný noise (smooth noise)
float smoothNoise(vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);

    // Váhy pro interpolační funkce
    float a = noise(i);
    float b = noise(i + vec2(1.0, 0.0));
    float c = noise(i + vec2(0.0, 1.0));
    float d = noise(i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - c) * u.x * u.y;
}

// Funkce pro simulaci mraků
float clouds(vec2 uv) {
    float cloudDensity = 0.4; // Hustota mraků
    uv *= 0.5; // Zoom na mraky
    uv.x += time * 0.1; // Pohyb mraků v čase
    float noiseVal = smoothNoise(uv) * cloudDensity;
    return smoothstep(0.3, 0.6, noiseVal); // Threshold pro mraky
}

void main() {
    // Normála pro "up" směrem k obloze
    vec3 up = normalize(fragPos);

    // Gradient oblohy (světlejší směrem k "zenitu")
    vec3 skyColor = mix(vec3(0.4, 0.6, 1.0), vec3(0.0, 0.0, 0.4), up.y * 0.5 + 0.5);

    // Mraky
    vec2 uv = fragPos.xy * 0.2; // Převod pozice do 2D
    float cloudFactor = clouds(uv); // Výpočet hodnoty mraků

    // Přidání mraků k obloze
    vec3 finalColor = mix(skyColor, vec3(1.0, 1.0, 1.0), cloudFactor);

    // Výstupní barva
    FragColor = vec4(finalColor, 1.0);
}
