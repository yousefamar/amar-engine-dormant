package com.metaplains.gfx.gui;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static com.metaplains.gfx.GL.*;
import org.lwjgl.util.glu.GLU;

import com.metaplains.core.GameScreen;
import com.metaplains.gfx.GL;
import com.metaplains.gfx.Shader;
import com.metaplains.gfx.TrueTypeFont;
import com.metaplains.gfx.VBOHelper;
import com.metaplains.gfx.Vertex;

public class GUIButton extends GUIElement {

	protected int id;
	private String text;
	private int vaoID;
	private int vboIID;
	private int idCount;

	public GUIButton(int x, int y, int width, int height, GUIElement parent, int id, String text) {
		super(x, y, width, height, parent);
		this.id = id;
		this.text = text;
		setupQuad();
	}
	
	@Override
	public boolean mousePressed(int button, int x, int y) {
		parent.elementClicked(this);
		return true;
	}

	public void setupQuad() {
		FloatBuffer vBuffer = VBOHelper.createVertexBuffer(4);
		vBuffer.put(-100).put(100).put(0f).put(1f);	vBuffer.put(1).put(0).put(0).put(1f);
		vBuffer.put(-100).put(-100).put(0f).put(1f);	vBuffer.put(0).put(1).put(0).put(1f);
		vBuffer.put(100).put(-100).put(0f).put(1f);	vBuffer.put(0).put(0).put(1).put(1f);
		vBuffer.put(100).put(100).put(0f).put(1f);	vBuffer.put(1).put(1).put(1).put(1f);
		vBuffer.flip();

		int[] indices = {
				0, 1, 2,
				2, 3, 0
		};
		idCount = indices.length;
		IntBuffer idBuffer = VBOHelper.createIndexBuffer(idCount);
		idBuffer.put(indices);
		idBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		glBindBuffer(GL_ARRAY_BUFFER, glGenBuffers());
		glBufferData(GL_ARRAY_BUFFER, vBuffer, GL_STATIC_DRAW);
		// Put the positions in attribute list 0
		glVertexAttribPointer(0, 4, GL_FLOAT, false, Vertex.sizeInBytes, 0);
		// Put the colors in attribute list 1
		glVertexAttribPointer(1, 4, GL_FLOAT, false, Vertex.sizeInBytes, 
				Vertex.elementBytes * 4);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		glBindVertexArray(0);

		// Create a new VBO for the indices and select it (bind) - INDICES
		vboIID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, idBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void render(GameScreen screen) {
		pushMatrix();
		scale(100, 100, 1);
		useProgram(Shader.STD);
		screen.renderIndexedVAO(vaoID, vboIID, idCount);
		useProgram(0);
		popMatrix();
		/*screen.setColor(mouseOver?Color.GRAY:Color.DARK_GRAY);
		screen.fillRect(x, y, width, height);
		screen.setColor(Color.LIGHT_GRAY);
		screen.drawRect(x, y, width, height);
		screen.drawString(text, x+width/2, y+(height-screen.getTTFont().getHeight())/2-1, 1, 1, TrueTypeFont.ALIGN_CENTER);*/
	}
}