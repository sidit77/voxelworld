#version 400 core

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;

in VS_OUT{
    vec3 position;
    vec4 lightpos;
    vec2 uv;
} gs_in[3];

out GS_OUT{
    vec3 position;
    vec3 normal;
    vec4 lightpos;
    vec2 uv;
} gs_out;

void createVertex(int index, vec3 normal){
    gl_Position = gl_in[index].gl_Position;
    gs_out.position = gs_in[index].position;
    gs_out.lightpos = gs_in[index].lightpos;
    gs_out.uv = gs_in[index].uv;
    gs_out.normal = normal;
    EmitVertex();
}

void createTriangle(int i0, int i1, int i2){
     vec3 ab = normalize(gs_in[i1].position - gs_in[i0].position);
     vec3 ac = normalize(gs_in[i2].position - gs_in[i0].position);
     vec3 normal = normalize(cross(ab, ac));
    createVertex(i0, normal);
    createVertex(i1, normal);
    createVertex(i2, normal);
    EndPrimitive();
}

void main() {
    createTriangle(0,1,2);
}
