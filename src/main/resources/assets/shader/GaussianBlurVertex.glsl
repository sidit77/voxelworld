#version 400

const vec2[] vertices = vec2[]( vec2(-1, 1),
                                vec2(-1,-1),
                                vec2( 1, 1),
                                vec2( 1, 1),
                                vec2(-1,-1),
                                vec2( 1,-1));

out vec2 texCoords[21];

uniform vec2 screen = vec2(1280, 720);
uniform vec2 direction;

void main() {
    gl_Position = vec4(vertices[gl_VertexID],0,1);
    vec2 centerTexCoords = (vertices[gl_VertexID] + vec2(1,1))/2;
    vec2 pixelSize = vec2(1.0 / screen.x, 1.0 / screen.y);

    for(int i = -10; i <= 10; i++){
        texCoords[i+10] = centerTexCoords + pixelSize * i * direction;
    }
}