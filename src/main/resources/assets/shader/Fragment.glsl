#version 420 core

out vec4 pixel;

in GS_OUT{
    vec3 normal;
    vec3 position;
    float visibility;
} fs_in;

uniform bool fog = false;

layout(binding = 0) uniform sampler2D grass;

void main() {
    vec3 blend_weights = abs(fs_in.normal);
    blend_weights = (blend_weights - 0.2) * 7;
    blend_weights = max(blend_weights, 0);
    blend_weights /= (blend_weights.x + blend_weights.y + blend_weights.z ).xxx;

    vec4 blended_color = texture(grass, fs_in.position.yz) * blend_weights.x +
                         texture(grass, fs_in.position.zx) * blend_weights.y +
                         texture(grass, fs_in.position.xy) * blend_weights.z;

    float light = 0.9 * clamp(dot(fs_in.normal,vec3(-0.5,1,-0.5)),0.35,1.0);
    
    //vec3 color = mix(vec3(0.5,0.5,0.5),vec3(0.25,0.6,0.25),clamp(dot(fs_in.normal, vec3(0,1,0))-0.25,0.0,1.0));
    //vec3 color = vec3((sin(fs_in.position.x/5)+1)/2,(cos(fs_in.position.y/5)+1)/2,(sin(fs_in.position.z/5)+1)/2) * 0.7f + vec3(0.3, 0.3, 0.3);
    vec4 color = vec4(0.25,0.6,0.25,1) * blended_color;
    //color = floor(color * 5) * 0.2;
    //light = floor(light * 5) * 0.2;
    pixel = color * light;//vec4(color * light,1);
    if(fog){
        pixel = mix(vec4(0.7,0.7,0.7,1), pixel, fs_in.visibility);
    }
}
