#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform float moveFactor;

const float waveStrength = 0.01;

void main(void) {
    vec2 ndc = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;
    vec2 refractionTextureCoords = vec2(ndc.x, ndc.y);
    vec2 reflectionTextureCoords = vec2(ndc.x, -ndc.y);

    // water wave distortion with clamping for glitch correction
    vec2 distortion1 = (texture(dudvMap,
            vec2(textureCoords.x + moveFactor, textureCoords.y)).rg * 2.0 - 1.0)
            * waveStrength;
    vec2 distortion2 = (texture(dudvMap,
                vec2(-textureCoords.x + moveFactor, textureCoords.y + moveFactor)).rg * 2.0 - 1.0)
                * waveStrength;

    vec2 totalDistortion = distortion1 + distortion2;
    reflectionTextureCoords += totalDistortion;
    reflectionTextureCoords.x = clamp(reflectionTextureCoords.x, 0.001, 0.999);
    reflectionTextureCoords.y = clamp(reflectionTextureCoords.y, -0.999, -0.001);

    refractionTextureCoords += totalDistortion;
    refractionTextureCoords = clamp(refractionTextureCoords, 0.001, 0.999);

    vec4 reflectionColor = texture(reflectionTexture, reflectionTextureCoords);
    vec4 refractionColor = texture(refractionTexture, refractionTextureCoords);

	out_Color = mix(reflectionColor, refractionColor, 0.5);
}