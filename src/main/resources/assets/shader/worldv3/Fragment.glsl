#version 420 core

out vec4 color;

in GS_OUT{
    vec3 position;
    vec3 normal;
    vec2 uv;
    flat int material;
} fs_in;

layout(binding = 0) uniform sampler2DArray colortexture;

void main() {
    color = texture(colortexture, vec3(fs_in.uv, float(fs_in.material))) * max(dot(fs_in.normal, normalize(vec3(0.5,1,1))),0.4);
    //color = vec4((fs_in.normal + 1)/2, 1);
}
