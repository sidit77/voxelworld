#version 420 core

layout (location = 0) out vec4 color0;
layout (location = 1) out vec4 color1;

vec2 poissonDisk[16] = vec2[](
   vec2( -0.94201624, -0.39906216 ),
   vec2( 0.94558609, -0.76890725 ),
   vec2( -0.094184101, -0.92938870 ),
   vec2( 0.34495938, 0.29387760 ),
   vec2( -0.91588581, 0.45771432 ),
   vec2( -0.81544232, -0.87912464 ),
   vec2( -0.38277543, 0.27676845 ),
   vec2( 0.97484398, 0.75648379 ),
   vec2( 0.44323325, -0.97511554 ),
   vec2( 0.53742981, -0.47373420 ),
   vec2( -0.26496911, -0.41893023 ),
   vec2( 0.79197514, 0.19090188 ),
   vec2( -0.24188840, 0.99706507 ),
   vec2( -0.81409955, 0.91437590 ),
   vec2( 0.19984126, 0.78641367 ),
   vec2( 0.14383161, -0.14100790 )
);

in VS_OUT{
    vec3 position;
    vec4 lightpos;
    vec2 uv;
    vec3 normal;
    float light;
    float ao;
} fs_in;

layout(binding = 0) uniform sampler2D colortexture;
layout(binding = 1) uniform sampler2D shadowmap;

uniform float darkness;
uniform vec3 lightDir;

float linstep(float low, float high, float v){
    return clamp((v-low)/(high - low), 0.0, 1.0);
}

float random(vec4 seed){
    float dot_product = dot(seed, vec4(12.9898,78.233,45.164,94.673));
    return fract(sin(dot_product) * 43758.5453);
}

void main() {

    vec3 projCoords = fs_in.lightpos.xyz / fs_in.lightpos.w;
    projCoords = projCoords * 0.5 + 0.5;
    float shadow = 1.0;

    if(projCoords.z > 0 && projCoords.z < 1){

        for (int i=0;i<8;i++){
            int index = int(16.0*random(vec4(fs_in.position, i)))%16;

            vec2 moments = vec2(1) - texture(shadowmap, projCoords.xy + poissonDisk[index]/1500.0).xy;

            float p = step(projCoords.z, moments.x);
            float variance = max(moments.y - moments.x * moments.x, 0.00002);

            float d = projCoords.z - moments.x;
            float pMax = linstep(0.2, 1.0, variance / (variance + d*d));

            shadow += max(p, pMax);

        }

        shadow /= 8;

    }

    shadow = clamp(shadow, 0.0, 1.0);

    float sunlight = shadow * darkness * max(0.5, dot(normalize(fs_in.normal), normalize(lightDir)));//darkness *
    color0 = texture(colortexture, fs_in.uv);//dot(fs_in.normal, normalize(vec3(0.5,1,1)))

    color0.xyz *= max(fs_in.light, max(sunlight, 0.1) * fs_in.ao);//max(fs_in.light, sunlight)

    if(color0.w < 0.5){
        discard;
    }

    color1 = vec4(0,0,0,1);
    //color = vec4((fs_in.normal + 1)/2, 1);
}
