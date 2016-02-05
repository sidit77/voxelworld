#version 400 core

out vec3 tc;
flat out int id;

uniform vec3 lightDir;

const vec3[8] vertices = vec3[8](vec3(-1.0, -1.0,  1.0),
							   vec3( 1.0, -1.0,  1.0),
							   vec3( 1.0,  1.0,  1.0),
							   vec3(-1.0,  1.0,  1.0),
							   vec3(-1.0, -1.0, -1.0),
                               vec3( 1.0, -1.0, -1.0),
                               vec3( 1.0,  1.0, -1.0),
                               vec3(-1.0,  1.0, -1.0));

const vec2[4] vertices2 = vec2[4](vec2(-1, -1),
							      vec2( 1, -1),
							      vec2( 1,  1),
							      vec2(-1,  1));

const int[36] indices = int[36](// front
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


uniform mat4 view_matrix;

void main(void){
    if(gl_VertexID < 36){
	    tc = vertices[indices[gl_VertexID]];
	    id = 0;
	    gl_Position = view_matrix * vec4(vertices[indices[gl_VertexID]], 1.0);
	}
	if(gl_VertexID >= 36 && gl_VertexID < 42){
	    tc = vec3((vertices2[indices[gl_VertexID-36]]+1)/2,0);
        id = 1;
        vec3 v = normalize(-lightDir);
        vec3 left = normalize(cross(v, vec3(1,0,0)));
        vec3 top = normalize(cross(v, left));

        vec3 final = v + vertices2[indices[gl_VertexID-36]].x * left * 0.1 + vertices2[indices[gl_VertexID-36]].y * top * 0.1;
        final = normalize(final);
        gl_Position = view_matrix * vec4(final, 1.0);
	}
	if(gl_VertexID >= 42 && gl_VertexID < 48){
        tc = vec3((vertices2[indices[gl_VertexID-42]]+1)/2,0);
        id = 2;
        vec3 v = normalize(lightDir);
        vec3 left = normalize(cross(v, vec3(1, 0,0)));
        vec3 top = normalize(cross(v, left));

        vec3 final = v + vertices2[indices[gl_VertexID-42]].x * left * 0.15 + vertices2[indices[gl_VertexID-42]].y * top * 0.15;
        final = normalize(final);
        gl_Position = view_matrix * vec4(final, 1.0);
    }

}