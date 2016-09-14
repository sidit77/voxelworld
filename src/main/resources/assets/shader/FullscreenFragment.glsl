#version 420

out vec4 color;

layout(binding = 0) uniform sampler2D image;
layout(binding = 1) uniform sampler2D blur;
layout(binding = 2) uniform sampler2D depth;
layout(binding = 3) uniform sampler2D godrays;

in vec2 texCoords;

float LinearizeDepth(vec2 uv){
    float n = 1.0; // camera z near
    float f = 100.0; // camera z far
    float z = texture(depth, uv).x;
    return (2.0 * n) / (f + n - z * (f - n));
}

vec3 saturation(vec3 rgb, float adjustment){
    // Algorithm from Chapter 16 of OpenGL Shading Language
    const vec3 W = vec3(0.2125, 0.7154, 0.0721);
    vec3 intensity = vec3(dot(rgb, W));
    return mix(intensity, rgb, adjustment);
}

void main() {

   //float len = length(texCoords-0.5f);
    //+ vec2(sin(texCoords.y * 4*2*3.14159),0)/100)
    //color = texture(depth,texCoords) * (1-len/1.5);
    //color.w = 1;
    //color = mix(texture(image,texCoords), blur9(image, texCoords, vec2(1280, 720), vec2(2)), abs(LinearizeDepth(texCoords)-LinearizeDepth(vec2(0.5,0.5)))*1.3);

    color = mix(texture(image, texCoords), texture(blur, texCoords), clamp(abs(LinearizeDepth(vec2(0.5,0.5))-LinearizeDepth(texCoords))*5,0, 1));
    //color = mix(texture(image, texCoords), texture(blur, texCoords), clamp(length((texCoords-0.5f)*2),0,1));

    color += texture(godrays, texCoords)* 0.75;

    color = vec4(saturation(color.xyz, 1.5f),color.w)*(1.2-length(texCoords-0.5f));
}
