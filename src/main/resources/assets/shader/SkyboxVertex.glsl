#version 400 core

out vec3 tc;

uniform mat4 view_matrix;

void main(void){
	vec3[8] vertices = vec3[8](vec3(-1.0, -1.0,  1.0),
							   vec3( 1.0, -1.0,  1.0),
							   vec3( 1.0,  1.0,  1.0),
							   vec3(-1.0,  1.0,  1.0),
							   vec3(-1.0, -1.0, -1.0),
                               vec3( 1.0, -1.0, -1.0),
                               vec3( 1.0,  1.0, -1.0),
                               vec3(-1.0,  1.0, -1.0));

    int[36] indices = int[36](// front
                              1, 0, 2,
                              3, 2, 0,
                              // top
                              5, 1, 6,
                              2, 6, 1,
                              // back
                              6, 7, 5,
                              4, 5, 7,
                              // bottom
                              0, 4, 3,
                              7, 3, 4,
                              // left
                              5, 4, 1,
                              0, 1, 4,
                              // right
                              2, 3, 6,
                              7, 6, 3);
	tc = normalize(vertices[indices[gl_VertexID]]);
	gl_Position = view_matrix * vec4(vertices[indices[gl_VertexID]], 1.0);
}