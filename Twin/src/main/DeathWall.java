package main;

import java.util.ArrayList;

import org.newdawn.slick.geom.Rectangle;

public class DeathWall extends Sprite 
{
	private ArrayList<SubWall> subWalls;
	private int health;
	public DeathWall(Camera c)
	{
		super(c);
		
		hitBox = new Rectangle(0, 0, 16, 64);
		health = 24;
		deathTrigger.add("player_bullet");
	}
	
	public void draw()
	{
		
	}
	
	public void update()
	{
		
	}
	
	public void collision(String s)
	{
		
	}
	
	private class SubWall extends Sprite
	{
		private SubWall(Camera c)
		{
			super(c);
			
			hitBox = new Rectangle(0, 0, 16, 64);
		}
		
		private void draw()
		{
			
		}
		
		private void update()
		{
			
		}
	}
}
