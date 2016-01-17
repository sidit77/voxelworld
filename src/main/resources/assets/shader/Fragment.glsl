#version 400 core

out vec4 pixel;

in GS_OUT{
    vec3 normal;
    vec3 position;
    float visibility;
} fs_in;

uniform bool fog = false;

void main() {

    float light = 0.9 * clamp(dot(fs_in.normal,vec3(-0.5,1,-0.5)),0.35,1.0);
    
    //vec3 color = mix(vec3(0.5,0.5,0.5),vec3(0.25,0.6,0.25),clamp(dot(fs_in.normal, vec3(0,1,0))-0.25,0.0,1.0));
    //vec3 color = vec3((sin(fs_in.position.x/5)+1)/2,(cos(fs_in.position.y/5)+1)/2,(sin(fs_in.position.z/5)+1)/2) * 0.7f + vec3(0.3, 0.3, 0.3);
    vec3 color = vec3(0.25,0.6,0.25);
    //color = floor(color * 5) * 0.2;
    //light = floor(light * 5) * 0.2;
    pixel = vec4(color * light,1);
    if(fog){
        pixel = mix(vec4(0.7,0.7,0.7,1), pixel, fs_in.visibility);
    }
}
