#version 400 core

const vec2[4] vertices = vec2[](vec2(0,0),
                                vec2(0,1),
                                vec2(1,0),
                                vec2(1,1));

out vec2 texcoord;

uniform mat4 ortho;
uniform vec2 pos;

struct Char{
    vec2 pos;
    vec2 dim;
    vec2 texpos;
    vec2 texdim;
} ;

layout(std140) uniform Chars {
    Char charinfo[128];
};

void main() {
    gl_Position = ortho * vec4(pos + charinfo[gl_InstanceID].pos + vertices[gl_VertexID] * charinfo[gl_InstanceID].dim,0,1);

    texcoord = vertices[gl_VertexID];
    texcoord = charinfo[gl_InstanceID].texpos+charinfo[gl_InstanceID].texdim*texcoord;
}
