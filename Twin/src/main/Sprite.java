package main;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
//import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;
//import org.newdawn.slick.opengl.Texture;
//import org.newdawn.slick.opengl.TextureLoader;
import static org.lwjgl.opengl.GL11.*;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.geom.Rectangle;

public class Sprite extends Entity
{
	protected Camera camera;
	protected Toolkit toolkit;
	protected Polygon bounds;
	protected Area area;
	protected Texture texture;
	protected String name;
	protected int score;
	protected ArrayList<String> deathTrigger;
	private ArrayList<Sprite> childSprites;
	
	protected Point screenPos; // position of the sprite translated according to the game camera's position
	protected double screenAngle;
	
	protected int currentFrame, totalFrames;
	protected int animationDirection; 
	protected int frameCount, frameDelay;
	protected int width, height, columns;
	protected double scale;
	
	protected boolean vector;
	protected boolean alive;
	protected int currentState;
	protected boolean screenLocked;
	protected boolean screenWrapped;
	protected boolean isMeshDrawn, fillVector;
	protected Color drawColor;
	protected boolean lookAtMouse;
	
	protected long shotTimer, shotDelay;
	
	public Sprite(Camera camera)
	{
		this.camera = camera;
		bounds = null;
		texture = null;
		name = "generic";
		score = 0;
		deathTrigger = new ArrayList<String>();
		childSprites = new ArrayList<Sprite>();
		
		screenPos = new Point(0, 0);
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
		fillVector = false;
		drawColor = Color.RED;
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
		//entity.g2d.drawImage(entity.getImage(), entity.at, entity.frame);
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
			//glClear(GL_COLOR_BUFFER_BIT);
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
	
	public void drawImageBounds(Color c)
	{
		
	}
	
	public void drawBounds(Color c, boolean fill)
	{
		
	}
	
	public void collision(String s)
	{
		//double relativeVel = 
		//double impulse = 1;
		if(deathTrigger.contains(s))
		{	
			alive = false;
		}	
	}
	
	public void collision(Sprite s)
	{
		
	}
	
	public void update(double deltaTime)
	{
		if(lookAtMouse)
		{
			worldAngle = Math.toDegrees(Math.atan2(screenPos.y - (Mouse.getY() * -1 + Display.getHeight()), screenPos.x - Mouse.getX())) - 90;
		}
		
		super.update(deltaTime);
		
		// update screen position
		Point centerPos = new Point(camera.worldPos.x + Display.getWidth() / 2, camera.worldPos.y + Display.getHeight() / 2);
		double radius = Math.hypot(worldPos.x - centerPos.x, worldPos.y - centerPos.y);
		double theta = Math.atan2(worldPos.y - centerPos.y, worldPos.x - centerPos.x) - Math.toRadians(camera.worldAngle);
		screenPos.x = (float) ((worldPos.x - camera.worldPos.x) + (z * radius * Math.cos(theta) - (worldPos.x - centerPos.x)));
		screenPos.y = (float) ((worldPos.y - camera.worldPos.y) + (z * radius * Math.sin(theta) - (worldPos.y - centerPos.y)));
		screenAngle = worldAngle - camera.worldAngle;
		
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
	
	public ArrayList<Sprite> getChildSprites()
	{
		return childSprites;
	}
	
	public double rotationVelocity() { return angleVel; }
	public void setRotationVelocity(double rate) { angleVel = rate; }
	
	public int state() { return currentState; }
	public void setState(int state) { currentState = state; }
	
	/*
	public Rectangle getImageBounds() 
	{ 
		
	}
	*/
	
	public Polygon getVectorBounds()
	{
		if(bounds != null)
		{	
			int npoints = bounds.npoints;
			int[] xpoints = new int[npoints];
			int[] ypoints = new int[npoints];

			for(int i = 0; i < npoints; i++)
				xpoints[i] = bounds.xpoints[i];

			for(int i = 0; i < npoints; i++)
				ypoints[i] = bounds.ypoints[i];

			for(int i = 0; i < npoints; i++)
			{
				xpoints[i] += screenPos.x;
				ypoints[i] += screenPos.y;		
			}

			Polygon p;
			p = new Polygon(xpoints, ypoints, npoints);
			area = new Area(p);
			xpoints = null;
			ypoints = null;
			return p;
		}
		
		else
		{
			int npoints = 4;
			int[] xpoints = {0, 40, 40,  0};
			int[] ypoints = {0, 0,  40, 40};
			
			for(int i = 0; i < npoints; i++)
			{
				xpoints[i] += screenPos.x;
				ypoints[i] += screenPos.y;		
			}
			Polygon p = new Polygon(xpoints, ypoints, npoints);
			area = new Area(p);
			return p;
		}
	}
	
	public void setBounds(Shape s)
	{
		this.bounds = (Polygon)s;
	}
	
	public Point position() { return worldPos; }
	public void setWorldPosition(Point pos) { this.worldPos = pos; }
	public void setScreenPosition(Point pos) { this.screenPos = pos; }
	public Point velocity() { return vel; }
	public void setVelocity(Point vel) { this.vel = vel; }
	
	public double getCenterX() 
	{
		return worldPos.x + width() / 2;
	}
	
	public double getCenterY()
	{
		return worldPos.y + height() / 2;
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
	
	//move angle indicates direction sprite is moving 
	
	//check for collision with a rectangular shape 
	
	//check for collision with another sprite 
	
	//check for collision with a point 
	public boolean collidesWith(Point point) { return (getVectorBounds().contains(point.x, point.y)); } 
	
	private URL getURL(String filename) 
	{ 
		URL url = null; 
		try 
		{ 
			url = this.getClass().getResource(filename); 
		} 
		catch (Exception e) { } 
		return url; 
	}
}