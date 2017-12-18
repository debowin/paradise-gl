#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCameraVector;
in vec3 toLightVector;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform sampler2D depthMap;
uniform vec3 lightColor;
uniform float moveFactor;
uniform float nearPlane;
uniform float farPlane;

// distortion and fresnel
const float waveStrength = 0.01;
const float reflectiveness = 1;
// specular
const float shineDamper = 20.0;
const float reflectivity = 0.6;

void main(void) {
    vec2 ndc = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;
    vec2 refractionTextureCoords = vec2(ndc.x, ndc.y);
    vec2 reflectionTextureCoords = vec2(ndc.x, -ndc.y);

    float depth = texture(depthMap, refractionTextureCoords).r;
    float floorDistance = 2.0 * nearPlane * farPlane / (farPlane + nearPlane - (2.0 * depth - 1.0) * (farPlane - nearPlane));

    depth = gl_FragCoord.z;
    float waterDistance = 2.0 * nearPlane * farPlane / (farPlane + nearPlane - (2.0 * depth - 1.0) * (farPlane - nearPlane));

    float waterDepth = floorDistance - waterDistance;

    // water wave distortion with clamping for glitch correction
    vec2 distortedTexCoords = texture(dudvMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg * 0.1;
    distortedTexCoords = textureCoords + vec2(distortedTexCoords.x, distortedTexCoords.y + moveFactor);
    vec2 totalDistortion = (texture(dudvMap, distortedTexCoords).rg * 2.0 - 1.0) * waveStrength * clamp(waterDepth/20.0, 0.0, 1.0);

    reflectionTextureCoords += totalDistortion;
    reflectionTextureCoords.x = clamp(reflectionTextureCoords.x, 0.001, 0.999);
    reflectionTextureCoords.y = clamp(reflectionTextureCoords.y, -0.999, -0.001);

    refractionTextureCoords += totalDistortion;
    refractionTextureCoords = clamp(refractionTextureCoords, 0.001, 0.999);

    vec4 reflectionColor = texture(reflectionTexture, reflectionTextureCoords);
    vec4 refractionColor = texture(refractionTexture, refractionTextureCoords);

	vec4 normalMapColor = texture(normalMap, distortedTexCoords);
	vec3 normal = vec3(normalMapColor.r*2.0-1.0, normalMapColor.b * 3.0, normalMapColor.g*2.0-1.0);
	normal = normalize(normal);

	vec3 viewVector = normalize(toCameraVector);
    float refractiveFactor = dot(viewVector, normal);
    refractiveFactor = pow(refractiveFactor, reflectiveness);
    refractiveFactor = clamp(refractiveFactor, 0.0, 1.0);

	vec3 halfVector = normalize(normalize(toCameraVector) + normalize(toLightVector));
	float specular = max(dot(halfVector, normal), 0.0);
	specular = pow(specular, shineDamper);
	vec3 specularHighlight = lightColor * specular * reflectivity * clamp(waterDepth/5.0, 0.0, 1.0);

	out_Color = mix(reflectionColor, refractionColor, refractiveFactor);
	out_Color = mix(out_Color, vec4(0, 0.3, 0.5, 1.0), 0.2) + vec4(specularHighlight, 0.0);
	out_Color.a = clamp(waterDepth/5.0, 0.0, 1.0);
}