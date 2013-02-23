package main;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.IOException;

public class Button extends Entity 
{
	private Texture texture;
	
	public Button(String filename, TextureLoader loader)
	{
		super();
		try 
		{
			texture = loader.getTexture(filename);
		} 
		catch (IOException e) { e.printStackTrace(); }
		
	}
	
	public void draw()
	{
		//update();
		
		if(texture != null)
		{
			//glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glLoadIdentity();
			glPushMatrix();
			
			glTranslated(pos.x, pos.y, 0);
			glRotated(angle, 0, 0, 1);
			glTranslated(-width()/2, -height()/2, 0);
			//glColor3f(0.5f,0.5f,1.0f);
			texture.bind();
			glBegin(GL_QUADS);
			
				glTexCoord2f(0, 0);
				glVertex2f(0, 0); // upper-left
				
				glTexCoord2f(0, texture.getHeight());
				glVertex2f(0, height()); // upper-right
				
				glTexCoord2f(texture.getWidth(), texture.getHeight());
				glVertex2f(width(), height()); // bottom-right
				
				glTexCoord2f(texture.getWidth(), 0);
				glVertex2f(width(), 0); // bottom-left
			
			glEnd();
			glPopMatrix();
		}
	}
	
	public void update()
	{
		
	}
	
	public int width()
	{
		if(texture != null)
			return texture.getImageWidth();
		return 0;
	}
	
	public int height()
	{
		if(texture != null)
			return texture.getImageHeight();
		return 0;
	}
}
