#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColour;
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main() {
    // Multi-Texturing
    vec4 blendMapColor = texture(blendMap, pass_textureCoords);
    vec2 tiledTextureCoords = pass_textureCoords * 40;

    float backgroundTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
    vec4 backgroundTextureColor = texture(backgroundTexture, tiledTextureCoords) * backgroundTextureAmount;

    vec4 rTextureColor = texture(rTexture, tiledTextureCoords) * blendMapColor.r;
    vec4 gTextureColor = texture(gTexture, tiledTextureCoords) * blendMapColor.g;
    vec4 bTextureColor = texture(bTexture, tiledTextureCoords) * blendMapColor.b;

    vec4 terrainColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;

    // Diffuse & Specular Lighting
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);

    float brightness = max(dot(unitNormal, unitLightVector), 0.2);
    vec3 diffuse = brightness * lightColour;

    vec3 unitCameraVector = normalize(toCameraVector);
    vec3 unitHalfDirection = normalize(unitLightVector + unitCameraVector);
    float specularFactor = max(dot(unitNormal, unitHalfDirection), 0);
    float dampedFactor = pow(specularFactor, shineDamper);
    vec3 finalSpecular = dampedFactor * reflectivity * lightColour;

    out_Color = vec4(diffuse, 1.0) * terrainColor + vec4(finalSpecular, 1);
    // Fog Effect
    out_Color = mix(vec4(skyColor, 1), out_Color, visibility);
}