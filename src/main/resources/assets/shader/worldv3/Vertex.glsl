#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 uv;
layout(location = 2) in float material;

out VS_OUT{
    vec3 position;
    vec2 uv;
    flat int material;
} vs_out;

uniform mat4 mvp;

void main() {
    gl_Position = mvp * vec4(position, 1);
    vs_out.position = position;
    vs_out.uv = uv;
    vs_out.material = int(material);
}
