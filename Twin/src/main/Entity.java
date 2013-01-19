package main;

import java.util.Stack;

//import org.lwjgl.util.Rectangle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public class Entity 
{
	protected Point worldPos;  // r
	protected Point vel;       // v
	protected Point accel;     // a
	protected Point netForce;  // F
	protected float mass;     // M
	protected Stack<Force> grossForce;
	
	protected double thrust;
	protected double friction;
	
	protected double worldAngle;  // Big Omega or Theta
	protected double z;
	protected double angleVel;    // Little Omega
	protected double angleAccel;  // Little Alpha
	protected double torque;
	protected double momentOfInertia;
	protected double elasticity;
	
	protected Rectangle hitBox;
	protected Shape shape;
	
	protected double timeResolution;
	
	public Entity()
	{
		worldPos = new Point(0, 0);
		z = 1;
		vel = new Point(0, 0);
		accel = new Point(0, 0);
		netForce = new Point(0, 0);
		mass = 2;
		grossForce = new Stack<Force>();
		thrust = 800;
		friction = 3;
		
		timeResolution = ONE_SECOND;
		
		worldAngle = 0;
		angleVel = 0.0;
		angleAccel = 0;
		torque = 0;
		momentOfInertia = 0;
		elasticity = 0.5;
		
		hitBox = new Rectangle(0, 0, 0, 0);
		//hitBox.
	}
	
	public void update(double deltaTime)
	{
		deltaTime /= timeResolution;
		
		// safeguard values
		if(friction < 0)
			friction = 0;
		if(mass <= 0)
			mass = (float) 0.1;
		if(thrust < 100)
			thrust = 100;
		
		// update force
		while(!grossForce.isEmpty())
		{
			Force f = grossForce.peek();
			if(!(f.position.x < 0) && !(f.position.y < 0))
				torque += f.energy.y * f.position.x - f.energy.x * f.energy.y;
			netForce.x += f.energy.x;
			netForce.y += f.energy.y;
			grossForce.pop();
		}
		
		netForce.x -= (vel.x * friction);
		netForce.y -= (vel.y * friction);
		momentOfInertia = mass * ((32 * 32 + 32 * 32) / 12); // width * width + height * height
		torque -= angleVel * friction * 200;
		
		// update acceleration
		accel.x = (netForce.x / mass); // acceleration is force divided by mass
		accel.y = (netForce.y / mass);
		angleAccel = (torque / momentOfInertia);
		
		// reset force
		netForce.x = 0;
		netForce.y = 0;
		torque = 0;
		
		// update velocity
		vel.x = (accel.x * deltaTime) + vel.x;
		vel.y = (accel.y * deltaTime) + vel.y;
		angleVel = (angleAccel * deltaTime) + angleVel;
		
		// resolve collisions
		
		
		// update position
		worldPos.x = (deltaTime * vel.x) + worldPos.x;
		worldPos.y = (deltaTime * vel.y) + worldPos.y;
		worldAngle = (angleVel * deltaTime) + worldAngle;
		
		if(worldAngle >= 360)
			worldAngle -= 360;
		else if(worldAngle < 0)
			worldAngle += 360;
		
		// update hitbox
		if(hitBox != null)
			hitBox.setLocation((int)worldPos.x - hitBox.getWidth()/2, (int)worldPos.y - hitBox.getHeight()/2);
		/*
		if(!screenLocked)
		{	
			screenPos.x = worldPos.x - camera.worldPos.x;
			screenPos.y = worldPos.y - camera.worldPos.y;
		}
		else
		{
			screenPos.x = worldPos.x;
			screenPos.y = worldPos.y;
		}
		
		if(screenWrapped)
		{
			if(screenPos.x < 0 - frameWidth)
				worldPos.x = frame.getWidth();
			else if(screenPos.x > frame.getWidth())
				worldPos.x = 0 - frameWidth;
			
			if(screenPos.y < 0 - frameHeight)
				worldPos.y = frame.getHeight();	
			else if(screenPos.y > frame.getHeight())
				worldPos.y = 0 - frameHeight;	
		}
		*/
	}
	
	protected final long ONE_SECOND =      (long)1e9;
	protected final long ONE_MILLISECOND = (long)1e6;
}