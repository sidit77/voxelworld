#version 400 core

layout (location = 0) out vec4 color0;
layout (location = 1) out vec4 color1;

in vec3 position;

void main() {

    float p = floor((position.y+1.5)/1.9 * 4) * 0.25;
    color0 = vec4(1-p,0,p,1);
    color1 = vec4(0,0,0,1);

}
