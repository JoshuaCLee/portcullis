package main;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
//import org.lwjgl.util.Rectangle;
import org.newdawn.slick.geom.Rectangle;

public class SpriteManager 
{
	private static SpriteManager manager;
	private int score;
	private ArrayList<Sprite> sprites;
	private Quadtree quad;
	private Camera camera;
	private Sprite arena;
	private TextureLoader loader;
	private boolean toggleMesh = false;
	private boolean oneShot = false;;
	
	private SpriteManager()
	{
		camera = new Camera(Display.getWidth(), Display.getHeight());
		arena = new Sprite(camera);
		arena.hitBox = new Rectangle(0 ,0, 2000, 2000);
		//arena.hitBox.setBounds(0, 0, 2000, 2000);
		arena.worldPos = new Point(arena.hitBox.getWidth()/2, arena.hitBox.getHeight()/2);
		arena.isMeshDrawn = true;
		sprites = new ArrayList<Sprite>();
		quad = new Quadtree(1, camera.hitBox);
		loader = new TextureLoader();
	}
	
	public static SpriteManager getInstance()
	{
		if(manager == null)
			manager = new SpriteManager();
		return manager;
			
	}
	
	public void newSprite()
	{
		
	}
	
	public void addSprite(Sprite s)
	{
		sprites.add(s);
	}
	
	public void updateSprites(double deltaTime)
	{
		if(!oneShot && !Keyboard.isKeyDown(Keyboard.KEY_F3))
		{
			oneShot = true;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_F3) && oneShot)
		{	
			toggleMesh = !toggleMesh;
			oneShot = false;
		}
		
		quad.clear();
		for (int i = 0; i < sprites.size(); i++) 
		{
			sprites.get(i).isMeshDrawn = toggleMesh;
			quad.insert(sprites.get(i));
		}
		
		for (int i = 0; i < sprites.size(); i++)
		{
			checkCollisions(sprites.get(i));
			
			if(sprites.get(i).alive == false)
			{
				score += sprites.get(i).score;
				sprites.remove(i);
				i--;
			}
			else if(sprites.get(i).worldPos.x < 0 - sprites.get(i).width())
			{
				if(sprites.get(i).name.equals("player"))
				{	
					sprites.get(i).draw(deltaTime);
					sprites.get(i).worldPos.x = 0 - sprites.get(i).width();
				}	
				else
				{	
					sprites.remove(i);
					i++;
				}
			}
			else if(sprites.get(i).worldPos.x > arena.hitBox.getWidth() + sprites.get(i).width())
			{
				if(sprites.get(i).name.equals("player"))
				{	
					sprites.get(i).draw(deltaTime);
					sprites.get(i).worldPos.x = arena.hitBox.getWidth() + sprites.get(i).width();
				}
				else
				{
					sprites.remove(i);
					i--;
				}
			}
			else if(sprites.get(i).worldPos.y < 0 - sprites.get(i).height())
			{
				if(sprites.get(i).name.equals("player"))
				{
					sprites.get(i).draw(deltaTime);
					sprites.get(i).worldPos.y = 0 - sprites.get(i).height();
				}
				else
				{
					sprites.remove(i);
					i--;
				}
			}
			else if(sprites.get(i).worldPos.y > arena.hitBox.getHeight() + sprites.get(i).height())
			{
				if(sprites.get(i).name.equals("player"))
				{
					sprites.get(i).draw(deltaTime);
					sprites.get(i).worldPos.y = arena.hitBox.getHeight() + sprites.get(i).height();
				}
				else
				{
					sprites.remove(i);
					i--;
				}
			}
			else
			{
				sprites.get(i).draw(deltaTime);
			}
		}
		arena.draw(deltaTime);
	}
	
	private void checkCollisions(Sprite sprite)
	{
		ArrayList<Sprite> returnObjects = new ArrayList<Sprite>();

		returnObjects.clear();
		quad.retrieve(returnObjects, sprite);

		for (int k = 0; k < returnObjects.size(); k++) 
		{
			// Run collision detection algorithm between objects
			if(sprite.hitBox.intersects(returnObjects.get(k).hitBox))
			{
				
				sprite.collision(returnObjects.get(k).name); // exchange info
				returnObjects.get(k).collision(sprite.name);
			}
		}

	}
	
	public TextureLoader getLoader()
	{
		return loader;
	}
	
	public void setCamera(Camera c)
	{
		this.camera = c;
		arena.camera = c;
	}
	
	public Sprite getArena()
	{
		return arena;
	}
	
	public int getScore()
	{
		return score;
	}
}