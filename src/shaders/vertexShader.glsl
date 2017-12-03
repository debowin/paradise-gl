#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector;
out vec3 toCameraVector;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition;
uniform bool useFakeLighting;

void main(){
    vec4 worldPosition = transformationMatrix * vec4(position, 1);
    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1);
    pass_textureCoords = textureCoords;
    vec3 actualNormal = normal;
    if(useFakeLighting)
        actualNormal = vec3(0, 1, 0);
    surfaceNormal = normalize((transpose(inverse(viewMatrix*transformationMatrix)) * vec4(actualNormal, 0.0)).xyz);
    toLightVector = lightPosition - worldPosition.xyz;
    toCameraVector = (inverse(viewMatrix) * vec4(0, 0, 0, 1)).xyz - worldPosition.xyz;
}