#version 400 core

in vec2 position;
out vec4 clipSpace;
out vec2 textureCoords;
out vec3 toCameraVector;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 cameraPosition;

const float tiling = 3.0;

void main(void) {
    vec4 worldPosition = modelMatrix * vec4(position.x, 0.0, position.y, 1.0);
	clipSpace = projectionMatrix * viewMatrix * worldPosition;
	textureCoords = vec2(position.x/2.0 + 0.5, position.y/2.0 + 0.5) * tiling;
	gl_Position = clipSpace;
	toCameraVector = cameraPosition - worldPosition.xyz;
}