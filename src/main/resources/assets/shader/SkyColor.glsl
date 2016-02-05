#version 420 core

vec4 skycolor(sampler2D glow, vec3 lightDir, vec3 tc){
    vec3 V = normalize(tc);
    vec3 L = normalize(lightDir);

    float vl = dot(V, L);
    float vl2 = dot(V, -L);

    vec3 fogcolor = mix(vec3(0.3,0.3,0.4), vec3(0.7,0.7,0.7), (L.y + 1.0) / 2.0);
    vec4 sunlight = texture2D(glow,vec2((L.y + 1.0) / 2.0, 1-vl));
    vec4 moonlight = vec4(0.9,0.9,0.9, vl2 * 0.35);

    return vec4(fogcolor + sunlight.rgb * sunlight.a / 2.0 + moonlight.rgb * moonlight.a / 2, 1);
}
