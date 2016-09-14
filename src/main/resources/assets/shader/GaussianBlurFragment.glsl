#version 420

out vec4 color;

layout(binding = 0) uniform sampler2D image;

in vec2 texCoords[21];

const float weights[] = float[21](
0.004481,
0.008089,
0.013722,
0.021874,
0.032768,
0.046128,
0.061021,
0.075856,
0.088613,
0.097274,
0.100346,
0.097274,
0.088613,
0.075856,
0.061021,
0.046128,
0.032768,
0.021874,
0.013722,
0.008089,
0.004481
);

void main() {

    color = vec4(0.0);
    for(int i = 0; i < 21; i++){
        color += texture(image, texCoords[i]) * weights[i];
    }

}
