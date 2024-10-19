#version 330 core

// Příjem dat z vertex shaderu
in vec3 fragNormal;   // Normála z vertex shaderu
in vec3 fragPosition; // Pozice vrcholu z vertex shaderu
in vec3 fragColor;

// Výstupní barva fragmentu
out vec4 color;

uniform float time;
uniform vec3 viewPos;

// Uniformy pro světlo a kameru
uniform vec3 lightPos; // Pozice světelného zdroje
uniform vec3 lightColor = vec3(1.0, 1.0, 1.0);    // Barva světla

void main() {

    // Normalizace normály
    vec3 normal = normalize(fragNormal);

    // Výpočet směru ke světlu
    //vec3 lightDir = normalize(lightPos - fragPosition);
    vec3 lightDir = normalize(normalize(lightPos) - fragPosition);

    // Lambertův zákon - difúzní osvětlení
    float diff = max(dot(normal, lightDir), 0.0);

    // Barva objektu kombinovaná s difúzním osvětlením
    vec3 diffuse = diff * lightColor * fragColor;

    // Ambientní složka (můžete upravit intenzitu ambientního světla)
    vec3 ambient = 0.01 * fragColor;

    // Výpočet spekulární složky
    vec3 viewDir = normalize(viewPos - fragPosition); // Směr k pozici kamery
    vec3 reflectDir = reflect(-lightDir, normal); // Odražený směr světla

    // Vypočítání spekulární složky
    float shininess = 32.0;
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 specular = spec * lightColor; // Vynásobení barvou světla

    // Kombinace ambientní a difúzní složky
    vec3 result = ambient + diffuse + specular;
    //  vec3 result = diff*fragColor*lightColor;
    //vec3 result = vec3(1.0, 1.0, 1.0);
    // vec3 result = lightColor;

    // Výstupní barva fragmentu
    color = vec4(result, 1.0);
}
