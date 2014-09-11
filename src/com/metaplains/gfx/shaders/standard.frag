#version 150 core

in vec4 pass_Color;
out vec4 out_Color;

vec4 getTexture();
float getDiffuse();
//float getShadow();

void main() {
	//TODO: Add ambient lighting support.
	out_Color = pass_Color;//getTexture() * /*gl_Color */ getDiffuse();// * getShadow();
}