#version 150 core

in vec3 in_Normal;
in vec2 in_TexCoord;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

mat4 getModelViewProjectionMatrix() {
	return projectionMatrix * viewMatrix * modelMatrix;
}


out vec2 texCoord;

void updateTexCoords() {
	texCoord = in_TexCoord;
}


out vec3 lightPos;
out vec3 vertNormal;
//uniform samplerCube shadowMap;

void updateLighting() {
	/* NB: This will only work for uniform scaling for obvious reasons but supporting non-uniform scaling is overkill. */
	vertNormal = normalize(modelMatrix * vec4(in_Normal, 0.0)).xyz;
	lightPos = vec3(0.0, 0.1, 0.0);//normalize(gl_LightSource[0].position.xyz);
}


out vec4 shadowCoord;

void updateShadow() {
	/* Transform shadow fragment world coordinate with shadowMap matrix. */
	//shadowCoord = gl_TextureMatrix[7] * modelMatrix * gl_Vertex;
}