#version 420 core

out vec4 color;

in vec2 uvs;

layout(binding = 0) uniform sampler2D colortexture;

void main() {

    if(texture(colortexture, uvs).w < 0.5f){
        discard;
    }

    float depth = gl_FragCoord.z;

    float dx = dFdx(depth);
    float dy = dFdy(depth);
    float moment2 = depth * depth + 0.25 * (dx * dx + dy * dy);

    color = vec4(1-depth,1-moment2,0,1);


}
