#version 400 core

layout(location = 0) in vec3 positions;
layout(location = 1) in vec2 uvs;
layout(location = 2) in vec3 normals;

uniform vec3 pos;
uniform mat4 mvp;
uniform mat3 rot;
uniform mat4 lightmatrix;

out vec3 position;
out vec2 uv;
out vec3 normal;
out vec4 lightpos;

void main() {

    position = rot * positions + pos;
    normal = rot * normals;
    uv = uvs;
    lightpos = lightmatrix * vec4(position, 1);

    gl_Position = mvp * vec4(position, 1);



}
