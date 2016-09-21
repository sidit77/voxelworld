#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 uv;
layout(location = 2) in vec3 normal;
layout(location = 3) in float light;

out VS_OUT{
    vec3 position;
    vec4 lightpos;
    vec2 uv;
    vec3 normal;
    float light;
} vs_out;

uniform mat4 mvp;
uniform mat4 lightmatrix;

void main() {
    gl_Position = mvp * vec4(position, 1);
    vs_out.position = position;
    vs_out.lightpos = lightmatrix * vec4(position, 1);
    vs_out.uv = uv;
    vs_out.normal = normal;
    vs_out.light = light;
}
