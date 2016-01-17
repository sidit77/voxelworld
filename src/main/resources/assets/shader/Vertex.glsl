#version 400 core

layout(location = 0) in vec3 position;
//layout(location = 1) in vec3 normal;

uniform mat4 mvp;
uniform vec3 pos;

out VS_OUT{
    vec3 normal;
    vec3 position;
    float visibility;
} vs_out;

const float density = 0.01;
const float gradient = 1.5;

void main() {
    gl_Position = mvp * vec4(position,1);
    vs_out.normal = vec3(0,1,0);
    vs_out.position = position;
    vs_out.visibility = exp(-pow((length(vs_out.position - pos)*density),gradient));
    vs_out.visibility = clamp(vs_out.visibility, 0.0, 1.0);
}
