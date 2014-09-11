package com.metaplains.gfx;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.opengl.*;

import java.io.*;
import java.net.URL;

/**
 * A class with static methods to handle shader program creation operations.
 * @author Paraknight
 */
public class Shader {

	public static final int VERTEX_SHADER = GL_VERTEX_SHADER;
	public static final int FRAGMENT_SHADER = GL_FRAGMENT_SHADER;
	
	public static int NONE;
	public static int STD;
	public static int TERRAIN;
	public static int WATER;
	
	private static int COMMON_VERT;
	private static int COMMON_FRAG;
	
	public static void loadLocalShaderProgs() {
		//TODO: WTF MACOSX WHY ARE YOU MAKING ME DO THIS!!!
		GL30.glBindVertexArray(GL30.glGenVertexArrays());
		
		COMMON_VERT = loadLocalShader("common.vert", VERTEX_SHADER);
		COMMON_FRAG = loadLocalShader("common.frag", FRAGMENT_SHADER);
		
		//TODO: Consider directory walk.
		NONE = 0;
		STD = loadLocalShaderProg("standard");
		//TERRAIN = loadLocalShaderProg("terrain");
		//WATER = loadLocalShaderProg("water");
	}

	public static int createProgram() {
		int programID = glCreateProgram();
		if (programID == 0)
			flagError("An error has occured while creating a shader program object.");
		glAttachShader(programID, COMMON_VERT);
		glAttachShader(programID, COMMON_FRAG);
		return programID;
	}
	
	public static void attachShader(int programID, int shaderID) {
		glAttachShader(programID, shaderID);
	}
	
	public static void linkAndValidateProgram(int programID, String name) {
		glLinkProgram(programID);
		if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE)
			flagError("Unable to link GLSL program \""+name+"\":\n"+glGetProgramInfoLog(programID, glGetProgrami(programID, GL_INFO_LOG_LENGTH)));

		glBindAttribLocation(programID, 0, "in_Position");
		glBindAttribLocation(programID, 1, "in_Color");
		//glBindAttribLocation(programID, 1, "vNormal");
		//glBindAttribLocation(programID, 2, "vTexCoord");

		glValidateProgram(programID);
		if (glGetProgrami(programID, GL_VALIDATE_STATUS) == GL_FALSE)
			flagError("Unable to link GLSL program \""+name+"\":\n"+glGetProgramInfoLog(programID, glGetProgrami(programID, GL_INFO_LOG_LENGTH)));
	}
	
	public static int loadShader(URL url, int type) {
		int shaderID = glCreateShader(type);
		if (shaderID == 0)
			flagError("An error has occured while creating a shader object.");
		StringBuilder shaderSrc = new StringBuilder();
		try {
			InputStream in = url.openStream();
			InputStreamReader ir = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(ir);
			String line;
			while ((line = br.readLine()) != null) {
				shaderSrc.append(line).append('\n');
			}
			br.close();
			ir.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		glShaderSource(shaderID, shaderSrc);
		glCompileShader(shaderID);
		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			String urlStr = url.toString();
			flagError("Unable to compile GLSL shader \""+urlStr.substring(urlStr.lastIndexOf("/")+1, urlStr.length())+"\":\n"+
					glGetShaderInfoLog(shaderID, glGetShaderi(shaderID, GL_INFO_LOG_LENGTH)));
		}
		return shaderID;
	}

	private static int loadLocalShader(String fileName, int type) {
		return loadShader(Shader.class.getResource("shaders/"+fileName), type);
	}
	
	private static int loadLocalShaderProg(String name) {
		int programID = createProgram();
		//TODO: Use static methods in this class instead.
		glAttachShader(programID, loadLocalShader(name+".vert", VERTEX_SHADER));
		glAttachShader(programID, loadLocalShader(name+".frag", FRAGMENT_SHADER));
		linkAndValidateProgram(programID, name);
		return programID;
	}
	
	private static void flagError(String errorMessage) {
		//TODO: Standardise error output and crash.
		System.err.println(errorMessage);
		Display.destroy();
		System.exit(1);
	}
}