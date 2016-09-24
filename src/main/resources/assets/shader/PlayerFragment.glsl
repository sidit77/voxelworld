#version 400 core

layout (location = 0) out vec4 color0;
layout (location = 1) out vec4 color1;

in vec3 position;
in vec3 normal;
in vec2 uv;

uniform float dark;
uniform vec3 light;

void main() {

    float sunlight = dark * max(0.5, dot(normalize(normal), normalize(light)));

    color0 = vec4(vec3(1,1,1) * max(sunlight, 0.1) ,1);


    color1 = vec4(0,0,0,1);

}
