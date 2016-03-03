#version 420 core

out vec4 color;

in GS_OUT{
    vec3 position;
    vec3 normal;
    vec2 uv;
    float lightlevel;
    vec4 lightpos;
    flat int material;
} fs_in;

layout(binding = 0) uniform sampler2DArray colortexture;
layout(binding = 1) uniform sampler2D shadowmap;

uniform float darkness;
uniform vec3 lightDir;

void main() {

    vec3 projCoords = fs_in.lightpos.xyz / fs_in.lightpos.w;
    projCoords = projCoords * 0.5 + 0.5;
    float shadow = 0.0;

    if(projCoords.z <= 1.0){
        shadow = 0;
        float currentDepth = projCoords.z;
        float bias = max(0.005 * (1.0 - dot(normalize(fs_in.normal), normalize(lightDir))), 0.0005);
        vec2 texelSize = 1.0 / textureSize(shadowmap, 0);
        for(int x = -1; x <= 1; ++x){
            for(int y = -1; y <= 1; ++y){
                float pcfDepth = texture(shadowmap, projCoords.xy + vec2(x, y) * texelSize).r;
                shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;
            }
        }
        shadow /= 9.0;
    }

    float sunlight = (1-shadow) * darkness * max(0.5, dot(normalize(fs_in.normal), normalize(lightDir)));//darkness *
    color = texture(colortexture, vec3(fs_in.uv, float(fs_in.material))) * max(max(fs_in.lightlevel, sunlight),0.1);//dot(fs_in.normal, normalize(vec3(0.5,1,1)))
    //color = vec4((fs_in.normal + 1)/2, 1);
}
