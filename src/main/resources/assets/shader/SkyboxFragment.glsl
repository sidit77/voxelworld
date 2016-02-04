#version 420 core

in vec3 tc;

out vec4 color;

layout(binding = 0) uniform sampler2D sky;
layout(binding = 1) uniform sampler2D glow;

uniform vec3 lightDir;

vec4 skycolor(sampler2D sky, sampler2D glow, vec3 lightDir, vec3 tc);

void main(void){
    color = skycolor(sky, glow, lightDir, tc);
    color = mix(color, mix(skycolor(sky, glow, lightDir, lightDir),vec4(1,1,1,1),0.25f), pow(clamp(dot(normalize(lightDir), normalize(tc))-0.993,0,1), 0.2) * 10);
}
