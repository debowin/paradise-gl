#version 400 core
#define MAX_LIGHTS 11

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 relativeLightPosition[MAX_LIGHTS];
out vec3 relativePosition;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[MAX_LIGHTS];

uniform bool useFakeLighting;

// fogging
uniform float fogDensity;
uniform float fogGradient;

// texture atlas
uniform int numberOfRows;
uniform vec2 offset;

void main(){
    vec4 worldPosition = transformationMatrix * vec4(position, 1);
    vec4 positionRelativeToCamera = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionRelativeToCamera;
    pass_textureCoords = (textureCoords/numberOfRows) + offset;

    vec3 actualNormal = normal;
    if(useFakeLighting)
        actualNormal = vec3(0, 1, 0);

    surfaceNormal = normalize((transpose(inverse(viewMatrix*transformationMatrix)) * vec4(actualNormal, 0.0)).xyz);
    for(int i = 0; i < MAX_LIGHTS; i++)
        relativeLightPosition[i] = (viewMatrix * vec4(lightPosition[i], 1)).xyz;
    relativePosition = positionRelativeToCamera.xyz;

    float distance = length(positionRelativeToCamera.xyz);
    visibility = clamp(exp(-pow(distance*fogDensity, fogGradient)), 0, 1);
}