package main;

import java.util.ArrayList;

//import org.lwjgl.util.Rectangle;
import org.newdawn.slick.geom.Rectangle;

public class Square extends Sprite
{
	protected long telegraphTimer, telegraphDelay;
	protected long shotTimer, shotDelay;
	protected double deltaAngle;
	
	//protected ArrayList<Sprite> bullets;
	
	public Square(Camera camera, TextureLoader loader)
	{
		super(camera);
		friction = 0;
		telegraphTimer = 0;
		telegraphDelay = (long)2e9;
		shotTimer = 0;
		shotDelay = 0;
		deltaAngle = 90;
		
		name = "enemy";
		score = 100;
		damageTrigger.add("player_bullet");
		//damageTrigger.add("enemy");
		hitBox = new Rectangle((int)pos.x, (int)pos.y, 32, 32);
		//hitBox.setBounds((int)worldPos.x, (int)worldPos.y, 32, 32);
		load("images/square.png", loader);
	}
	
	public void draw(double deltaTime)
	{
		update(deltaTime);
		super.draw(deltaTime);
	}
	
	public void update(double deltaTime)
	{
		//telegraph(deltaTime);
		super.update(deltaTime);
	}
	
	public void telegraph(double deltaTime)
	{	
		if(!(telegraphTimer >= telegraphDelay))
		{
			telegraphTimer += deltaTime;
		}
		else
		{
			if(deltaAngle == 0)
			{
				if(angle > 0 && angle < 10)
				{
					deltaAngle = 90;
					angle = 0;
					telegraphTimer = 0;
					angleVel = 0;
					//shoot();
				}
	
				else
					angleVel = 200;
			}
			else if(angle < deltaAngle)
			{
				angleVel = 200;
			}
			else
			{
				angleVel = 0;
				angle = deltaAngle;
				deltaAngle += 90;
				if(deltaAngle >= 360)
					deltaAngle = 0;
				telegraphTimer = 0;
				//shoot();
			}
		}
	}
	
	public void shoot()
	{
		Sprite s1 = new Sprite(camera);
		s1.name = "enemy_bullet";
		s1.pos = new Vector2d(pos.x, pos.y);
		s1.hitBox = new Rectangle(0, 0, 16, 16);
		//s1.hitBox.setSize(16, 16);
		s1.vel = new Vector2d(0, 300);
		s1.friction = -1;
		s1.damageTrigger.add("player_bullet");
		s1.load("images/square_bullet.png", SpriteManager.getInstance().getLoader());
		SpriteManager.getInstance().addSprite(s1);
		
		Sprite s2 = new Sprite(camera);
		s2.name = "enemy_bullet";
		s2.pos = new Vector2d(pos.x, pos.y);
		s2.hitBox = new Rectangle(0, 0, 16, 16);
		//s2.hitBox.setSize(16, 16);
		s2.vel = new Vector2d(0, -300);
		s2.friction = 0;
		s2.damageTrigger.add("player_bullet");
		s2.load("images/square_bullet.png", SpriteManager.getInstance().getLoader());
		SpriteManager.getInstance().addSprite(s2);
		
		Sprite s3 = new Sprite(camera);
		s3.name = "enemy_bullet";
		s3.pos = new Vector2d(pos.x, pos.y);
		s3.hitBox = new Rectangle(0, 0, 16, 16);
		//s3.hitBox.setSize(16, 16);
		s3.vel = new Vector2d(300, 0);
		s3.friction = 0;
		s3.damageTrigger.add("player_bullet");
		s3.load("images/square_bullet.png", SpriteManager.getInstance().getLoader());
		SpriteManager.getInstance().addSprite(s3);
		
		Sprite s4 = new Sprite(camera);
		s4.name = "enemy_bullet";
		s4.pos = new Vector2d(pos.x, pos.y);
		s4.hitBox = new Rectangle(0, 0, 16, 16);
		//s4.hitBox.setSize(16, 16);
		s4.vel = new Vector2d(-300, 0);
		s4.friction = 0;
		s4.damageTrigger.add("player_bullet");
		s4.load("images/square_bullet.png", SpriteManager.getInstance().getLoader());
		SpriteManager.getInstance().addSprite(s4);
	}
}
