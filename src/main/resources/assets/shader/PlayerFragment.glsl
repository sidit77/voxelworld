#version 400 core

out vec4 color;

in vec3 position;

void main() {

    float p = floor((position.y+1.5)/1.9 * 4) * 0.25;
    color = vec4(1-p,0,p,1);


}
