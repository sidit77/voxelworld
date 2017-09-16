#version 420 core

in vec3 tc;
flat in int id;

layout (location = 0) out vec4 color0;
layout (location = 1) out vec4 color1;

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
        color0 = mix(texture(night, tc),texture(day, tc),clamp(darkness,0,1));
        vec4 fogcolor = skycolor(glow, lightDir, tc);
        color0 = mix(color0, fogcolor, clamp(1-(normalize(tc)*0.8).y,0,1));
        //skycolor(sky, glow, lightDir, tc);
    }
    if(id == 1){
        color0 = texture(moon, tc.xy);
        color0.a -= darkness*1.3;
    }
    if(id == 2){
        color0 = texture(sun, tc.xy);
    }

    if(id != 0){
        color1 = vec4(0,0,0,0);
    }else{
        color1 = vec4(0,0,0,1);
        if(dot(normalize(lightDir), normalize(tc)) > 0.995){
            color1 = skycolor(glow, lightDir, tc);
        }
    }


    //color = mix(color, mix(skycolor(sky, glow, lightDir, lightDir),vec4(1,1,1,1),0.25f), pow(clamp(dot(normalize(lightDir), normalize(tc))-0.993,0,1), 0.2) * 10);
}
