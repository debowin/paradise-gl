#version 400 core

in vec2 position;
out vec4 clipSpace;
out vec2 textureCoords;
out vec3 toCameraVector;
out vec3 toLightVector;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 cameraPosition;
uniform vec3 lightPosition;

const float rippleAmount = 5.0;

void main(void) {
    vec4 worldPosition = modelMatrix * vec4(position.x, 0.0, position.y, 1.0);
	clipSpace = projectionMatrix * viewMatrix * worldPosition;
	textureCoords = vec2(position.x/2.0 + 0.5, position.y/2.0 + 0.5) * rippleAmount;
	gl_Position = clipSpace;
	toCameraVector = cameraPosition - worldPosition.xyz;
	toLightVector = lightPosition - worldPosition.xyz;
}