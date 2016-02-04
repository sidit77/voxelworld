#version 420 core

vec4 skycolor(sampler2D sky, sampler2D glow, vec3 lightDir, vec3 tc){
    vec3 V = normalize(tc);
    vec3 L = normalize(lightDir);

    float vl = dot(V, L);

    vec4 Kc = texture2D(sky, vec2((L.y + 1.0) / 2.0, 1-(V.y+1)/2));
    vec4 Kg = texture2D(glow,vec2((L.y + 1.0) / 2.0, 1-vl));

    return vec4(Kc.rgb + Kg.rgb * Kg.a / 2.0, Kc.a);
}
