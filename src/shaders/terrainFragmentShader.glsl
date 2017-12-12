#version 400 core
#define MAX_LIGHTS 11

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 relativeLightPosition[MAX_LIGHTS];
in vec3 relativePosition;
in float visibility;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColour[MAX_LIGHTS];
uniform vec3 lightAttenuation[MAX_LIGHTS];
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
    vec3 unitCameraVector = normalize(-relativePosition);
    vec3 totalDiffuse = vec3(0, 0, 0), totalSpecular = vec3(0, 0, 0);
    for(int i = 0; i < MAX_LIGHTS; i++){
        // do lighting calculations for each light
        if(lightColour[i] == vec3(0, 0, 0))
            // if the light has no colour
            continue;
        float distanceToLight = length(relativeLightPosition[i] - relativePosition);
        float attenuationFactor = lightAttenuation[i].x
                                + lightAttenuation[i].y * distanceToLight
                                + lightAttenuation[i].z * distanceToLight * distanceToLight;
        if(attenuationFactor > 10)
            // if the light is too far away
            continue;
        vec3 unitLightVector = normalize(relativeLightPosition[i] - relativePosition);
        float brightness = max(dot(unitNormal, unitLightVector), 0);
        vec3 unitHalfDirection = normalize(unitLightVector + unitCameraVector);
        float specularFactor = max(dot(unitNormal, unitHalfDirection), 0);
        float dampedFactor = pow(specularFactor, shineDamper);
        totalDiffuse += (brightness * lightColour[i]) / attenuationFactor;
        totalSpecular += (dampedFactor * reflectivity * lightColour[i]) / attenuationFactor;
    }
    totalDiffuse = max(totalDiffuse, 0.2);
    out_Color = vec4(totalDiffuse, 1.0) * terrainColor + vec4(totalSpecular, 1);
    // Fog Effect
    out_Color = mix(vec4(skyColor, 1), out_Color, visibility);
}