#version 400 core

layout(lines_adjacency) in;
layout(triangle_strip, max_vertices = 6) out;

const vec2[] uvs = vec2[4](vec2(0,0), vec2(1,0), vec2(1,1),vec2(0,1));

in VS_OUT{
    vec3 position;
    float visibility;
    flat int material;
} gs_in[4];

out GS_OUT{
    mat3 tbn;
    vec3 position;
    float visibility;
    vec2 uv;
    flat int material;
} gs_out;

void createVertex(int index, vec3 normal, vec3 tangent, vec3 bitangent){
    gl_Position = gl_in[index].gl_Position;
    gs_out.position = gs_in[index].position;
    gs_out.visibility = gs_in[index].visibility;
    gs_out.tbn = mat3(tangent, bitangent, normal);
    gs_out.material = gs_in[index].material;
    gs_out.uv = -uvs[index];
    EmitVertex();
}

void createTriangle(int i0, int i1, int i2){
    vec3 ab = normalize(gs_in[i1].position - gs_in[i0].position);
    vec3 ac = normalize(gs_in[i2].position - gs_in[i0].position);
    vec3 normal = normalize(cross(ab, ac));
    vec3 tangent = cross(normal, vec3(1,0,1));//vec3(-1,0,0)
    vec3 bitangent = cross(tangent, normal);

    createVertex(i0, normal, tangent, bitangent);
    createVertex(i1, normal, tangent, bitangent);
    createVertex(i2, normal, tangent, bitangent);
    EndPrimitive();
}

void main(void){
    createTriangle(0,1,2);
    createTriangle(2,3,0);
}
