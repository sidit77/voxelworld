#version 400 core

layout(location = 0) in vec3 positions;
layout(location = 1) in vec2 uvs;
layout(location = 2) in vec3 normals;

uniform vec3 pos;
uniform mat4 mvp;
uniform mat3 rot;

out vec3 position;
out vec2 uv;
out vec3 normal;

void main() {

    position = rot * positions;
    normal = rot * normals;
    uv = uvs;

    gl_Position = mvp * vec4(position+pos, 1);



}
