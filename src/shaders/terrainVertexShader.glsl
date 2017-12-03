#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector;
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition;
uniform float fogDensity;
uniform float fogGradient;

void main(){
    vec4 worldPosition = transformationMatrix * vec4(position, 1);
    vec4 positionRelativeToCamera = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionRelativeToCamera;
    pass_textureCoords = textureCoords * 40;

    surfaceNormal = normalize((transpose(inverse(viewMatrix*transformationMatrix)) * vec4(normal, 0.0)).xyz);
    toLightVector = lightPosition - worldPosition.xyz;
    toCameraVector = (-viewMatrix * worldPosition).xyz;

    float distance = length(positionRelativeToCamera.xyz);
    visibility = clamp(exp(-pow(distance*fogDensity, fogGradient)), 0, 1);
}