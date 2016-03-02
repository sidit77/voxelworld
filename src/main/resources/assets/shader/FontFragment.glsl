#version 420 core

out vec4 out_color;

layout(binding = 0) uniform sampler2D fonttexture;

in vec2 texcoord;

uniform vec3 color;
uniform float transparency;

const float width = 0.47;
const float edge = 0.05;

void main() {

    float distance = 1.0 - texture(fonttexture, texcoord).a;
    float alpha = 1.0 - smoothstep(width, width + edge, distance);

    out_color = vec4(color, alpha * transparency);
}
