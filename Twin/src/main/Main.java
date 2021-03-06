package main;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT; // so many imports
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
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
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11.glShadeModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
//import org.lwjgl.input.Controller;
//import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import net.java.games.input.*;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
//import org.lwjgl.util.Rectangle;
import org.lwjgl.util.input.ControllerAdapter;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.geom.Rectangle;

public class Main
{
	private int width = 1280;
	private int height = 720;
	//private int width = 1920;
	//private int height = 1080;
	public long deltaTime; // the amount of time that has passed since the last tick
	private long fps;      // however many ticks (frames) happen per second
	private int sleepDelay;
	private int score;
	
	private UnicodeFont font;
	private DecimalFormat f = new DecimalFormat("#.##");
	private FloatBuffer perspectiveProjectionMatrix = reserveData(16);
	private FloatBuffer orthographicProjectionMatrix = reserveData(16);
	
	//private Texture bg;
	//private Sprite bg;
	
	private Camera camera;
	private SpriteManager manager;
	private Sprite player;
	private Sprite arena;
	private Square testSquare;
	private ArrayList<Sprite> bg;
	
	private boolean oneShot = false;
	private boolean paused;
	private ArrayList<Button> buttons;
	private long shotTimer;
	private long spawnTimer;
	
	public static void main(String[] args) 
	{
		new Main();
	}
	
	public Main()
	{
		try 
		{
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle("Portcullis");
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
			
			sleepDelay = 20; // in milliseconds
			score = 0;
		} 
		catch (LWJGLException e) { e.printStackTrace(); }
		
		camera = new Camera(width, height);
		camera.pos = new Vector2d(0, 0);
		
		manager = SpriteManager.getInstance();
		manager.setCamera(camera);
		
		arena = manager.getArena();
		
		bg = new ArrayList<Sprite>();
		for(int i = -1; i < 5; i++)
		{
			for(int k = -1; k < 5; k++)
			{
				Sprite s = new Sprite(camera);
				s.load("images/background.png", manager.getLoader());
				s.pos = new Vector2d(i * s.width()*2, k * s.height()*2);
				s.setPhysicsMode(Entity.PHYSMODE_STATIC);
				//s.scale = 1.5;
				s.z = .5;
				bg.add(s);
			}
		}
		
		player = new Sprite(camera);
		player.name = "player";
		//player.isMeshDrawn = true;
		player.pos = new Vector2d(Display.getWidth()/2, Display.getHeight()/2);
		player.angle = 0;
		player.mass = .5;
		player.thrust = 1000;
		player.lookAtMouse = true;
		player.hitBox = new Rectangle((int)player.pos.x, (int)player.pos.y, 24, 24);
		player.setPhysicsMode(Entity.PHYSMODE_DYNAMIC);
		player.load("images/keeper.png", manager.getLoader());
		//player.damageTrigger.add("enemy");
		//player.damageTrigger.add("enemy_bullet");
		manager.addSprite(player);
		
		testSquare = new Square(camera, manager.getLoader());
		testSquare.pos = new Vector2d(200, 200);
		testSquare.friction = 3;
		testSquare.setPhysicsMode(Entity.PHYSMODE_DYNAMIC);
		manager.addSprite(testSquare);
		
		shotTimer = 0;
		
		paused = false;
		buttons = new ArrayList<Button>();
		for(int i = 0; i < 3; i++)
		{
			Button b = new Button("images/button.png", manager.getLoader());
			b.pos.x = Display.getWidth()/2;
			b.pos.y = Display.getHeight()/3 + i * b.height()/.8;
			buttons.add(b);
		}
		
		initText();
		run();
	}
	
	public void initText()
	{
		try 
		{
			font = new UnicodeFont("fonts/ProFontWindows.ttf", 13, false, false);
			font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
			font.addAsciiGlyphs();
			font.loadGlyphs();
		} 
		catch (SlickException e) { e.printStackTrace(); }
		
		/*
		try
		{
			InputStream inputStream = ResourceLoader.getResourceAsStream("fonts/ProFontWindows.ttf");
			 
			java.awt.Font awtFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, inputStream);
			awtFont = awtFont.deriveFont(12f); // set font size
			font = new TrueTypeFont(awtFont, true);
		}
		catch(Exception e) { e.printStackTrace(); }
		*/
	}
	
	public void run()
	{
		long start, end, totalTime = 0;
		deltaTime = 1;
		int ticks = 0;
		while(!Display.isCloseRequested())
		{
			start = (Sys.getTime() * 1000000000) / Sys.getTimerResolution();
			if(paused)
				pause();
			else
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
		
		updateBackground();
		updateCamera();
		updateSprites();
		
		updateStats();
		
		Display.update();
	}
	
	public void pause()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		Display.sync(120);
		
		int x = Display.getWidth()/2 - font.getWidth("Paused")/2;
		int y = 5 + font.getHeight("Paused");
		font.drawString(x, y, "Paused");
		
		for(int i = 0; i < buttons.size(); i++)
		{
			buttons.get(i).draw();
		}
		
		if(!oneShot && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
			oneShot = true;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && oneShot)
		{	
			paused = false;
			oneShot = false;
		}
		
		Display.update();
	}
	
	public void buttonPressed()
	{	
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			player.netForce.y -= player.thrust;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			player.netForce.y += player.thrust;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			player.netForce.x += player.thrust;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			player.netForce.x -= player.thrust;
		}
		
		if(!oneShot && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
			oneShot = true;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && oneShot)
		{
			paused = true;
			oneShot = false;
		}
		
		if(!(shotTimer >= 3e8))
		{
			shotTimer += deltaTime;
		}

		if(Mouse.isButtonDown(0) && shotTimer >= 3e8 && player.alive)
		{
			double sin = Math.sin(Math.toRadians(player.angle) - Math.PI/2); // turn 90 degrees (pi/2) to look at mouse
			double cos = Math.cos(Math.toRadians(player.angle) - Math.PI/2);
			for(int i = -1; i < 2; i++) // translate bullet left and right of center according to player angle to give 3 bullet spray effect
			{
				Sprite s = new Sprite(camera);
				s.name = "player_bullet";
				s.pos = new Vector2d(player.pos.x + Math.cos(Math.toRadians(player.angle)) * i * 13, player.pos.y + Math.sin(Math.toRadians(player.angle)) * i * 13);
				s.friction = 0;
				s.vel.y = sin*1300 + player.vel.y;
				s.vel.x = cos*1300 + player.vel.x;
				s.angle = player.angle;
				s.hitBox = new Rectangle((int)s.pos.x, (int)s.pos.y, 3, 32);
				s.load("images/bullet.png", manager.getLoader());
				s.damageTrigger.add("enemy");
				s.damageTrigger.add("enemy_bullet");
				manager.addSprite(s);
			}
			shotTimer = 0;
		}
	}
	
	public void updateBackground()
	{
		for(int i = 0; i < bg.size(); i++)
			bg.get(i).draw(deltaTime);
	}
	
	public void updateStats()
	{
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glPushMatrix();
		int x = 10;
		int y = 10;
		
		font.drawString(x, y, "FPS: " + fps);
		font.drawString(x, y+=15, "Player World Position: (" + f.format(player.pos.x) + ", " + f.format(player.pos.y) + ")");
		font.drawString(x, y+=15, "Player Screen Position: (" + f.format(player.screenPos.x) + ", " + f.format(player.screenPos.y) + ")");
		font.drawString(x, y+=15, "Player World Angle: (" + f.format(player.angle) + ")");
		font.drawString(x, y+=15, "Mouse Position: (" + f.format(Mouse.getX()) + ", " + f.format(Mouse.getY() * -1 + Display.getHeight()) +")");
		font.drawString(x, y+=15, "Shot Timer: (" + f.format(shotTimer) + ")");
		x = Display.getWidth()/2 - font.getWidth(Integer.toString(score))/2;
		y = Display.getHeight() - font.getHeight(Integer.toString(score)) - 5;
		font.drawString(x, y, Integer.toString(manager.getScore()));
		glPopMatrix();
	}
	
	public void updateSprites()
	{
		manager.updateSprites(deltaTime);
		//spawnEnemies();
	}
	
	public void spawnEnemies()
	{
		if(!(spawnTimer >= 3e9)) // 3 seconds
		{
			spawnTimer += deltaTime;
		}
		else
		{
			Square e1 = new Square(camera, manager.getLoader());
			if(Math.round(Math.random()) == 0)
			{
				e1.pos = new Vector2d(arena.hitBox.getWidth() + 32, arena.hitBox.getHeight()*2/3 + 50);
				e1.vel.x = -100;
			}
			else
			{
				e1.pos = new Vector2d(-32, arena.hitBox.getHeight()*2/3 + 18);
				e1.vel.x = 100;
			}
			manager.addSprite(e1);
			
			Square e2 = new Square(camera, manager.getLoader());
			if(Math.round(Math.random()) == 0)
			{
				e2.pos = new Vector2d(arena.hitBox.getWidth() + 32, arena.hitBox.getHeight() / 3 - 18);
				e2.vel.x = -100;
			}
			else
			{
				e2.pos = new Vector2d(-32, arena.hitBox.getHeight()/3 - 50);
				e2.vel.x = 100;
			}
			manager.addSprite(e2);
			
			spawnTimer = 0;
		}
	}
	
	public void updateCamera()
	{
		camera.pos.x = (player.pos.x - Display.getWidth() / 2);
		camera.pos.y = (player.pos.y - Display.getHeight() / 2);
	}
	
	public FloatBuffer reserveData(int size)
	{
		FloatBuffer data = BufferUtils.createFloatBuffer(size);
		return data;
	}
	
	private ByteBuffer loadIcon(URL url) throws IOException 
	{
        InputStream is = url.openStream();
        try 
        {
            PNGDecoder decoder = new PNGDecoder(is);
            ByteBuffer bb = ByteBuffer.allocateDirect(decoder.getWidth()*decoder.getHeight()*4);
            decoder.decode(bb, decoder.getWidth()*4, PNGDecoder.Format.RGBA);
            bb.flip();
            return bb;
        } 
        finally 
        {
            is.close();
        }
    }
}
