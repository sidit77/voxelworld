#version 420 core

layout (location = 0) out vec4 color0;
layout (location = 1) out vec4 color1;

in vec3 position;
in vec3 normal;
in vec2 uv;
in vec4 lightpos;

layout(binding = 1) uniform sampler2D shadowmap;
uniform float dark;
uniform vec3 light;
uniform float torch;

float linstep(float low, float high, float v){
    return clamp((v-low)/(high - low), 0.0, 1.0);
}

void main() {

    vec3 projCoords = lightpos.xyz / lightpos.w;
    projCoords = projCoords * 0.5 + 0.5;
    float shadow = 1.0;

    if(projCoords.z > 0 && projCoords.z < 1){

        for (int i=0;i<8;i++){
            vec2 moments = vec2(1) - texture(shadowmap, projCoords.xy).xy;

            float p = step(projCoords.z, moments.x);
            float variance = max(moments.y - moments.x * moments.x, 0.00002);

            float d = projCoords.z - moments.x;
            float pMax = linstep(0.2, 1.0, variance / (variance + d*d));

            shadow += max(p, pMax);

        }

        shadow /= 8;

    }

    shadow = clamp(shadow, 0.0, 1.0);

    float sunlight = shadow * dark * max(0.5, dot(normalize(normal), normalize(light)));

    color0 = vec4(vec3(1,1,1) * max(max(sunlight, torch), 0.1) ,1);

    color1 = vec4(0,0,0,1);

}
