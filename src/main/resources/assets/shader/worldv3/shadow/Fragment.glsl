#version 400 core

out vec4 color;

void main() {

    float depth = gl_FragCoord.z;

    float dx = dFdx(depth);
    float dy = dFdy(depth);
    float moment2 = depth * depth + 0.25 * (dx * dx + dy * dy);

    color = vec4(1-depth,1-moment2,0,1);


}
