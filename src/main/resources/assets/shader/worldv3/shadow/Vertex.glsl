#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 uv;

uniform mat4 mvp;

out vec2 uvs;

void main() {
    gl_Position = mvp * vec4(position, 1);
    uvs = uv;
}