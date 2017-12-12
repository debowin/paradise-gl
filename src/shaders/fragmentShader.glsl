#version 400 core
#define MAX_LIGHTS 11

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 relativeLightPosition[MAX_LIGHTS];
in vec3 relativePosition;
in float visibility;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColour[MAX_LIGHTS];
uniform vec3 lightAttenuation[MAX_LIGHTS];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main() {
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
    vec4 textureColor = texture(textureSampler, pass_textureCoords);
    if(textureColor.a < 0.5)
        discard;
    out_Color = vec4(totalDiffuse, 1) * textureColor + vec4(totalSpecular, 1);
    out_Color = mix(vec4(skyColor, 1), out_Color, visibility);
}