package com.metaplains.gfx.gui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;

import com.bulletphysics.collision.shapes.VertexData;
import com.metaplains.core.GameClient;
import com.metaplains.core.GameScreen;
import com.metaplains.gfx.GL;
import com.metaplains.gfx.Shader;
import com.metaplains.gfx.Texture;
import com.metaplains.gfx.TrueTypeFont;
import com.metaplains.gfx.VBOHelper;
import com.metaplains.world.scenes.Scene;

public class GUIWorld extends GUIElement {

	private int fps;
	private int tps;
	private int renderTickCounter;
	private int tickCounter;
	private long lastFPS = (System.nanoTime() / 1000000);
	private long lastTPS = (System.nanoTime() / 1000000);
	private float usedRAM;

	public GUIWorld() {
		super();
		Dimension screenDims = GameClient.game.screen.screenDims;
		//this.subElements.add(new GUIActionBar(99, screenDims.height - 78, world.user));
		//this.subElements.add(new GUIMinimap(0, screenDims.height - 98, world));
		this.subElements.add(new GUIChat(5, screenDims.height - 32));
		createVBOs();
	}

	public void setChatVisible() {
		((GUIChat) this.subElements.get(0)).setVisible();
	}

	public void tick(){
		super.tick();
		
		if ((System.nanoTime() / 1000000) - lastTPS > 1000) {
			tps = tickCounter;
			tickCounter = 0;
			lastTPS += 1000;
		}
		
		if (tickCounter == 0)
			usedRAM = ((float)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()))/(1024F * 1024F);
		
		tickCounter++;
	}
	
	private int vaoId;
	private int vboId;
	private int vbocId;
	private int vboiId;
	private int idCount;
	
	private void createVBOs() {
		//TODO: Create helper class.
		int x = 5, y = 90;
		float[] vertices = {
				x, y, 0, 1,
				x, y+100, 0, 1,
				x+100, y+100, 0, 1,
				x+100, y, 0, 1
		};
		FloatBuffer vBuffer = BufferUtils.createFloatBuffer(vertices.length);
		vBuffer.put(vertices);
		vBuffer.flip();
		
		float[] colors = {
				1f, 0f, 0f, 1f,
				0f, 1f, 0f, 1f,
				0f, 0f, 1f, 1f,
				1f, 1f, 1f, 1f,
		};
		FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(colors.length);
		colorsBuffer.put(colors);
		colorsBuffer.flip();
		
		// OpenGL expects to draw vertices in counter clockwise order by default
		int[] indices = {
				0, 1, 2,
				2, 3, 0
		};
		IntBuffer idBuffer = BufferUtils.createIntBuffer(idCount=indices.length);
		idBuffer.put(indices);
		idBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		// A VAO can have up to 16 attributes (VBO's) assigned to it by default
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		// A VBO is a collection of Vectors which in this case resemble the location of each vertex.
		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, vBuffer, GL_STATIC_DRAW);
		// Put the VBO in the attributes list at index 0
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		// Deselect (bind to 0) the VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		glBindVertexArray(0);

		// Create a new VBO for the indices and select it (bind) - COLORS
		vbocId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbocId);
		glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		// Create a new VBO for the indices and select it (bind)
		vboiId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, idBuffer, GL_STATIC_DRAW);
		// Deselect (bind to 0) the VBO
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void render(GameScreen screen) {
		if ((System.nanoTime() / 1000000) - lastFPS > 1000) {
			fps = renderTickCounter;
			renderTickCounter = 0;
			lastFPS += 1000;
		}

		super.render(screen);
//		if (world.user.heldStructure != null) {
//			Composite tempComp = gfx.getComposite();
//			AlphaComposite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6F);
//		    gfx.setComposite(alphaComp);
//			world.user.heldStructure.render(gfx);
//			gfx.setComposite(tempComp);
//		}

		renderTestImage();
		
		screen.setColor(Color.WHITE);
		screen.drawString("FPS: "+fps, 0, 0, 1, 1, TrueTypeFont.ALIGN_LEFT);
		screen.drawString("TPS: "+tps, 0, 20, 1, 1, TrueTypeFont.ALIGN_LEFT);
		screen.drawString("RAM: "+usedRAM+" MB", 0, 40, 1, 1, TrueTypeFont.ALIGN_LEFT);
		//if (!GameClient.game.currentScene.isSinglePlayer())
		//	screen.drawString("Ping: "+GameClient.game.netIOManager.ping+" ms", 0, 60, 1, 1, TrueTypeFont.ALIGN_LEFT);

		renderTickCounter++;
	}
	
	private void renderTestImage() {
		// Bind to the VAO that has all the information about the vertices
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		// Bind to the index VBO that has all the information about the order of the vertices
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);

		// Draw the vertices
		glDrawElements(GL_TRIANGLES, idCount, GL_INT, 0);

		// Put everything back to default (deselect)
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);


//		//TODO: Use screen method (and consider putting glEnable(GL_TEXTURE_2D); in it).
//		glEnable(GL_TEXTURE_2D);
//		glColor3f(1, 1, 1);
//		glBindTexture(GL_TEXTURE_2D, Texture.NOSIGNAL);
//		GL.useProgram(Shader.STD);
//		glBegin(GL_QUADS);
//		glTexCoord2f(0, 1);
//		glVertex3f(x, y+100, 0);
//		glTexCoord2f(1, 1);
//		glVertex3f(x+100, y+100, 0);
//		glTexCoord2f(1, 0);
//		glVertex3f(x+100, y, 0);
//		glTexCoord2f(0, 0);
//		glVertex3f(x, y, 0);
//		glEnd();
//		glDisable(GL_TEXTURE_2D);
	}
}