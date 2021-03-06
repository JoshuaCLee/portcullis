package main;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11.glColor3f;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class Tester 
{
	private int width = 1280;
	private int height = 720;
	public long deltaTime;
	private long fps;
	
	private Rectangle r1;
	private Rectangle r2;

	public static void main(String[] args) 
	{
		new Tester();
	}
	
	public Tester()
	{
		try 
		{
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle("Test");
			//Display.setIcon(new ByteBuffer[] { loadIcon(getClass().getResource("images/keeper.png")) } );
			Display.create();
			
			// Controller controller = ControllerEnvironment.getDefaultEnvironment.getControllers();

			// enable textures since we're going to use these for our sprites
			glEnable(GL_TEXTURE_2D);
			
			glShadeModel(GL11.GL_SMOOTH); 
			
			// enable transparency
			glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			// disable the OpenGL depth test since we're rendering 2D graphics
			glDisable(GL_DEPTH_TEST);

			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();

			glOrtho(0, width, height, 0, -1, 1);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			glViewport(0, 0, width, height);
			
			fps = 0;
		} 
		catch (LWJGLException e) { e.printStackTrace(); }
		run();
	}
	
	public void initTest()
	{
		r1 = new Rectangle(0, 0, 100, 100);
		r2 = new Rectangle(Display.getWidth()/2, Display.getHeight()/2, 50, 50);
		System.out.println(r1);
	}
	
	public void run()
	{
		long start, end, totalTime = 0;
		deltaTime = 1;
		int ticks = 0;
		initTest();
		while(!Display.isCloseRequested())
		{
			start = (Sys.getTime() * 1000000000) / Sys.getTimerResolution();
			gameUpdate();
			end = (Sys.getTime() * 1000000000) / Sys.getTimerResolution();
			ticks++;
			//deltaTime = (long)(end - start + (1e6 * sleepDelay)); // 1e6 nanoseconds in 1 millisecond
			deltaTime = (long)(end - start);
			totalTime += deltaTime;
			if(totalTime >= 1e9) // 1e9 nanoseconds in 1 second
			{
				fps = ticks;
				ticks = 0;
				totalTime -= 1e9;
			}

		}
		Display.destroy();
	}
	
	public void gameUpdate()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		Display.sync(120);
		
		buttonPressed();
		
		//updateBackground();
		//updateCamera();
		updateSprites();
		
		updateStats();
		
		Display.update();
	}
	
	public void buttonPressed()
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_Q))
		{
			r1.setAngle(r1.getAngle() - 1);
			System.out.println(r1.getAngle());
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_E))
		{
			r1.setAngle(r1.getAngle() + 1);
			System.out.println(r1.getAngle());
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
			r1.setLocation(r1.getX(), r1.getY() - 1);
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
			r1.setLocation(r1.getX() - 1, r1.getY());
		if(Keyboard.isKeyDown(Keyboard.KEY_S))
			r1.setLocation(r1.getX(), r1.getY() + 1);
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
			r1.setLocation(r1.getX() + 1, r1.getY());
		r1.createVertices();
	}
	
	public void updateSprites()
	{
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glPushMatrix();
		glPolygonMode(GL_FRONT, GL_LINE);
		glColor3f(1, 1, 1);
		glBegin(GL_QUADS);
		
			glVertex2d(r1.getVertices().get(0).x, r1.getVertices().get(0).y); // upper-left
			glVertex2d(r1.getVertices().get(1).x, r1.getVertices().get(1).y); // upper-right
			glVertex2d(r1.getVertices().get(2).x, r1.getVertices().get(2).y); // bottom-right
			glVertex2d(r1.getVertices().get(3).x, r1.getVertices().get(3).y); // bottom-left
		
		glEnd();
		glPolygonMode(GL_FRONT, GL_FILL);
		glPopMatrix();
		
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glPushMatrix();
		glPolygonMode(GL_FRONT, GL_LINE);
		glBegin(GL_QUADS);
		
			glVertex2d(r2.getVertices().get(0).x, r2.getVertices().get(0).y); // upper-left
			glVertex2d(r2.getVertices().get(1).x, r2.getVertices().get(1).y); // upper-right
			glVertex2d(r2.getVertices().get(2).x, r2.getVertices().get(2).y); // bottom-right
			glVertex2d(r2.getVertices().get(3).x, r2.getVertices().get(3).y); // bottom-left
		
		glEnd();
		glPolygonMode(GL_FRONT, GL_FILL);
		glPopMatrix();
		
		Vector2d collision = r1.intersect(r2);
		if(collision != null)
		{
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			glPushMatrix();
			glPolygonMode(GL_FRONT, GL_LINE);
			glColor3f(1, 0, 0);
			glBegin(GL_QUADS);
			
				glVertex2d(collision.x - 2, collision.y - 2); // upper-left
				glVertex2d(collision.x + 2, collision.y - 2); // upper-right
				glVertex2d(collision.x + 2, collision.y + 2); // bottom-right
				glVertex2d(collision.x - 2, collision.y + 2); // bottom-left
			
			glEnd();
			glPolygonMode(GL_FRONT, GL_FILL);
			//glColor4f(1, 1, 1, 0);
			glPopMatrix();
		}
		
	}
	
	public void updateStats()
	{
		
	}
}
