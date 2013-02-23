package main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
//import org.newdawn.slick.opengl.Texture;
//import org.newdawn.slick.opengl.TextureLoader;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.geom.Rectangle;

/**
 * An object that handles its own on-screen graphical representation. It'll update itself when called upon by the main loop.
 * It also handles higher order game behaviors out of the scope of the physics defined by the Entity class
 */
public class Sprite extends Entity
{
	protected Camera camera;
	protected Texture texture;
	protected int score;
	protected ArrayList<String> damageTrigger;
	
	protected Vector2d screenPos; // position of the sprite translated according to the game camera's position
	protected double screenAngle;
	
	protected int currentFrame, totalFrames;
	protected int animationDirection; 
	protected int frameCount, frameDelay;
	protected int width, height, columns;
	protected double scale;
	
	protected boolean alive;
	protected int currentState; // POSSIBLE: define complex sprite states beyond 'alive' and 'dead'
	protected boolean screenLocked; // FIXME
	protected boolean screenWrapped; // FIXME
	protected boolean isMeshDrawn;
	protected boolean lookAtMouse;
	
	protected long shotTimer, shotDelay;
	
	public Sprite(Camera camera)
	{
		this.camera = camera;
		texture = null;
		name = "generic";
		score = 0;
		damageTrigger = new ArrayList<String>();
		
		screenPos = new Vector2d(0, 0);
		screenAngle = 0;
		
		currentFrame = 0;
		totalFrames = 1;
		animationDirection = 1;
		frameCount = 0;
		frameDelay = 0;
		width = 0;
		columns = 1;
		scale = 1;
		
		currentState = 0;
		alive = true;
		screenLocked = false;
		screenWrapped = false;
		isMeshDrawn = false;
		lookAtMouse = false;
		
		shotTimer = 0;
		shotDelay = (long)2e8;
	}
	
	public void load(String filename, TextureLoader loader, int columns, int totalFrames, int width, int height)
	{
		try 
		{
			texture = loader.getTexture(filename);
			width = texture.getImageWidth();
			height = texture.getImageHeight();
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void load(String filename, TextureLoader loader)
	{
		
		try 
		{
			texture = loader.getTexture(filename);
			width = texture.getImageWidth();
			height = texture.getImageHeight();
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public int width()
	{
		if(texture != null)
			return texture.getImageWidth();
		else
			return 0;
	}
	
	public int height()
	{
		if(texture != null)
			return texture.getImageHeight();
		else
			return 0;
	}
	
	public void scale(int scale)
	{
		this.scale = scale;
	}
	
	public void draw(double deltaTime)
	{
		update(deltaTime);
		// get the current frame
		int frameX = (currentFrame % columns) * width;
		int frameY = (currentFrame / columns) * height;
		
		/*
		if(image != null)
		{
			currentImage = image.getSubimage(frameX, frameY, width, height);
			g2d.drawImage(currentImage, transform, frame);
		}
		*/
		
		
		if(texture != null)
		{
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			
			glPushMatrix();
			glTranslated(screenPos.x, screenPos.y, 0);
			glRotated(screenAngle, 0, 0, 1);
			glScaled(scale, scale, 0);
			glTranslated(-width()/2, -height()/2, 0);
			
			//glColor3f(0.5f,0.5f,1.0f);
			texture.bind();
			glBegin(GL_QUADS);
			
				glTexCoord2f(0, 0);
				glVertex2f(0, 0); // upper-left
				
				glTexCoord2f(0, texture.getHeight());
				glVertex2f(0, height); // upper-right
				
				glTexCoord2f(texture.getWidth(), texture.getHeight());
				glVertex2f(width, height); // bottom-right
				
				glTexCoord2f(texture.getWidth(), 0);
				glVertex2f(width, 0); // bottom-left
			
			glEnd();
			glPopMatrix();
			
		}
	
		if(isMeshDrawn)
		{
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			glPushMatrix();
			
			glTranslated(screenPos.x, screenPos.y, 0);
			glRotated(screenAngle, 0, 0, 1);
			glScaled(scale, scale, 0);
			glTranslated(-hitBox.getWidth()/2, -hitBox.getHeight()/2, 0);
			
			glBindTexture(GL_TEXTURE_2D, 0);
			//glColor3f(1.0f,0.0f,0.0f);
			glPolygonMode(GL_FRONT, GL_LINE);
			glBegin(GL_QUADS);
			
				glVertex2f(0, 0); // upper-left
				glVertex2f(0, hitBox.getHeight()); // upper-right
				glVertex2f(hitBox.getWidth(), hitBox.getHeight()); // bottom-right
				glVertex2f(hitBox.getWidth(), 0); // bottom-left
			
			glEnd();
			glPolygonMode(GL_FRONT, GL_FILL);
			//glColor4f(1, 1, 1, 0);
			glPopMatrix();
		}
	}
	
	public void collision(String s)
	{
		//double relativeVel = 
		//double impulse = 1;
		if(damageTrigger.contains(s))
		{	
			alive = false;
		}	
	}
	
	public void collision(List returnObjects)
	{
		
	}
	
	public void update(double deltaTime)
	{
		if(lookAtMouse)
		{
			angle = Math.toDegrees(Math.atan2(screenPos.y - (Mouse.getY() * -1 + Display.getHeight()), screenPos.x - Mouse.getX())) - 90;
		}
		
		super.update(deltaTime);
		
		// update screen position
		// the most important lines of the Sprite class
		Point centerPos = new Point(camera.pos.x + Display.getWidth() / 2, camera.pos.y + Display.getHeight() / 2);
		double radius = Math.hypot(pos.x - centerPos.x, pos.y - centerPos.y);
		double theta = Math.atan2(pos.y - centerPos.y, pos.x - centerPos.x) - Math.toRadians(camera.angle);
		screenPos.x = ((pos.x - camera.pos.x) + (z * radius * Math.cos(theta) - (pos.x - centerPos.x)));
		screenPos.y = ((pos.y - camera.pos.y) + (z * radius * Math.sin(theta) - (pos.y - centerPos.y)));
		screenAngle = angle - camera.angle;
		
		//update animation
		if(totalFrames > 1)
		{
			frameCount++;
			if(frameCount > frameDelay)
			{
				frameCount = 0;
				currentFrame += animationDirection;
				if(currentFrame > totalFrames - 1)
				{
					currentFrame = 0;
				}
				else if(currentFrame < 0)
				{
					currentFrame = totalFrames - 1;
				}
			}
		}
	}
	
	public double rotationVelocity() { return angleVel; }
	public void setRotationVelocity(double rate) { angleVel = rate; }
	
	public int state() { return currentState; }
	public void setState(int state) { currentState = state; }
	
	public Vector2d position() { return pos; }
	public void setWorldPosition(Vector2d pos) { this.pos = pos; }
	public void setScreenPosition(Vector2d pos) { this.screenPos = pos; }
	public Vector2d velocity() { return vel; }
	public void setVelocity(Vector2d vel) { this.vel = vel; }
	
	public double getCenterX() 
	{
		return pos.x + width() / 2;
	}
	
	public double getCenterY()
	{
		return pos.y + height() / 2;
	}
	
	public Point center()
	{
		int x = (int)getCenterX();
		int y = (int)getCenterY();
		return new Point(x, y);
	}
	
	public boolean alive() { return alive; }
	public void setAlive(boolean alive) { this.alive = alive; }
	public void setScreenLock(boolean locked) { this.screenLocked = locked; }
	public void setScreenWrap(boolean wrapped) { this.screenWrapped = wrapped; }
	
	public String toString()
	{
		return name;
	}
}