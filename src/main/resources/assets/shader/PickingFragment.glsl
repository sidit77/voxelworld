#version 400 core

out vec4 color;

in vec3 position;

void main() {
    color = vec4(0,0,0,length(position)-0.25);


}
