#version 330 core

in vec3 fragPosition;
out vec4 FragColor;

void main()
{
    vec3 normalizedPosition = normalize(fragPosition);

    FragColor = vec4(normalizedPosition * 0.5 + 0.5, 1.0);
    FragColor = vec4(fragPosition, 1.0);;
}