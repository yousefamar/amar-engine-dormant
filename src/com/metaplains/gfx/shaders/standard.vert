#version 150 core

in vec4 in_Position;
in vec4 in_Color;

out vec4 pass_Color;

mat4 getModelViewProjectionMatrix();
void updateTexCoords();
void updateLighting();
//void updateShadow();

void main() {
	//updateTexCoords();
	//updateLighting();
	//updateShadow();

	gl_Position = getModelViewProjectionMatrix() * in_Position;
	pass_Color = in_Color;
}