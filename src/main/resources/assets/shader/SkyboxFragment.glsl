#version 420 core

in vec3 tc;
flat in int id;

out vec4 color;

layout(binding = 0) uniform sampler2D glow;
layout(binding = 1) uniform sampler2D moon;
layout(binding = 2) uniform sampler2D sun;
layout(binding = 3) uniform samplerCube night;
layout(binding = 4) uniform samplerCube day;

uniform vec3 lightDir;
uniform float darkness;

vec4 skycolor(sampler2D glow, vec3 lightDir, vec3 tc);

void main(void){

    if(id == 0){
        color = mix(texture(night, tc),texture(day, tc),clamp(darkness,0,1));
        vec4 fogcolor = skycolor(glow, lightDir, tc);
        color = mix(color, fogcolor, clamp(1-(normalize(tc)*0.8).y,0,1));
        //skycolor(sky, glow, lightDir, tc);
    }
    if(id == 1){
        color = texture(moon, tc.xy);
        color.a -= darkness*1.3;
    }
    if(id == 2){
        color = texture(sun, tc.xy);
    }

    //color = mix(color, mix(skycolor(sky, glow, lightDir, lightDir),vec4(1,1,1,1),0.25f), pow(clamp(dot(normalize(lightDir), normalize(tc))-0.993,0,1), 0.2) * 10);
}
