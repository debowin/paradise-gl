#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColour;
uniform float shineDamper;
uniform float reflectivity;

void main() {
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);

    float brightness = max(dot(unitNormal, unitLightVector), 0.2);
    vec3 diffuse = brightness * lightColour;

    vec3 unitCameraVector = normalize(toCameraVector);
    vec3 unitHalfDirection = normalize(unitLightVector + unitCameraVector);
    float specularFactor = max(dot(unitNormal, unitHalfDirection), 0);
    float dampedFactor = pow(specularFactor, shineDamper);
    vec3 finalSpecular = dampedFactor * reflectivity * lightColour;

    vec4 textureColor = texture(textureSampler, pass_textureCoords);
    if(textureColor.a<0.5)
        discard;

    out_Color = vec4(diffuse, 1.0) * textureColor + vec4(finalSpecular, 1);
}