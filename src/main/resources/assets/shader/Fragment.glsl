#version 420 core

out vec4 pixel;

in GS_OUT{
    mat3 tbn;
    vec3 position;
    float visibility;
    vec2 uv;
    flat int material;
} fs_in;

layout(binding = 0) uniform sampler2D grass;
layout(binding = 1) uniform sampler2D grass_normal;
layout(binding = 2) uniform sampler2D glow;

uniform vec3 pos;

uniform bool fog = false;
uniform vec3 lightDir = vec3(-0.5,1,-0.5);
uniform float lightPower = 1;
uniform float shineDamper = 1f;
uniform float reflectivity = 0.25f;

vec4 skycolor(sampler2D glow, vec3 lightDir, vec3 tc);

void main() {

    vec3 normal = (2*texture(grass_normal, fs_in.uv).xyz)-1;
    normal = normalize(fs_in.tbn * normal);//vec3(0,0,1)

    float diffuselight =  0.9 * clamp(dot(normal, lightDir),0.35,1.0);
    float diffuselight2 = 0.3 * clamp(dot(normal,-lightDir),0.35,1.0);

    float specularLight = reflectivity * pow(max(dot(reflect(lightDir, normal), normalize(fs_in.position - pos)),0),shineDamper);

    //vec3 color = mix(vec3(0.5,0.5,0.5),vec3(0.25,0.6,0.25),clamp(dot(fs_in.normal, vec3(0,1,0))-0.25,0.0,1.0));
    //vec3 color = vec3((sin(fs_in.position.x/5)+1)/2,(cos(fs_in.position.y/5)+1)/2,(sin(fs_in.position.z/5)+1)/2) * 0.7f + vec3(0.3, 0.3, 0.3);
    //vec4 color = blended_color;//vec4(0.25,0.6,0.25,1)
    //color.b = mix(color.b, 1, fs_in.material- 1);

    vec4[] matcolor = vec4[](vec4(1,0,0,1),vec4(0,0,1,1), vec4(0,1,0,1));
    vec4 color = matcolor[fs_in.material - 1];
    color = mix(color, texture(grass, fs_in.uv), 0.9);

    //color = floor(color * 5) * 0.2;
    //light = floor(light * 5) * 0.2;
    //diffuselight = floor(diffuselight * 5) * 0.2;
    //specularLight = floor(specularLight * 5) * 0.2;
    pixel = color * diffuselight * lightPower + color * specularLight * lightPower + color * diffuselight2 * (1-lightPower);//vec4(color * light,1);
    if(fog){
        vec4 skycolor = skycolor(glow, lightDir, normalize(fs_in.position - pos));
        pixel = mix(skycolor, pixel, fs_in.visibility);
    }
}
