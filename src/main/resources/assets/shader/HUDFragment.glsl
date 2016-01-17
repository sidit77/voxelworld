#version 400 core

out vec4 pixel;

void main() {
    vec2 p = gl_PointCoord * 2.0 - vec2(1.0);
    if (dot(p, p) > 1.0 || dot(p, p) < 0.5)
        discard;

    pixel = vec4(0.9,0.7,0.9,1);
}
