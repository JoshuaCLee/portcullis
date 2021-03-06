package main;

import java.util.ArrayList;
import java.util.Stack;

//import org.lwjgl.util.Rectangle;
import org.newdawn.slick.geom.GeomUtil;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
/**
 * An object with an arbitrary position and various physics properties as well as dynamic collision response
 */
public class Entity 
{
	protected Vector2d pos;       // r
	protected Vector2d vel;       // v
	protected Vector2d accel;     // a
	protected Vector2d netForce;  // F
	protected double mass;        // M
	protected Stack<Force> grossForce; // we'll stack all acting forces and work out how they affect the sprite
	
	protected double thrust;
	protected double friction;
	
	protected double angle;           // Ω or θ
	protected double z;
	protected double angleVel;        // ω
	protected double angleAccel;      // α
	protected double torque;          // τ pretty much just angular force
	protected double momentOfInertia; // I
	protected double elasticity;      // e
	
	protected Rectangle hitBox; // our geometry object we use to detect collisions
	protected int physMode;
	
	protected String name;
	
	protected double timeResolution;
	
	public Entity()
	{
		pos = new Vector2d(0, 0);
		z = 1;
		vel = new Vector2d(0, 0);
		accel = new Vector2d(0, 0);
		netForce = new Vector2d(0, 0);
		mass = 2;
		grossForce = new Stack<Force>();
		thrust = 800;
		friction = 3;
		
		timeResolution = ONE_SECOND;
		
		angle = 0;
		angleVel = 0.0;
		angleAccel = 0;
		torque = 0;
		momentOfInertia = 0;
		elasticity = 1;
		
		hitBox = new Rectangle(0, 0, 0, 0);
		physMode = PHYSMODE_STATIC;
		//hitBox.
		
		name = "generic";
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
				torque += f.position.cross(f.energy);
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
		vel.x += (accel.x * deltaTime);
		vel.y += (accel.y * deltaTime);
		angleVel += (angleAccel * deltaTime);
		
		// resolve collisions
		if(physMode == PHYSMODE_DYNAMIC)
			resolveCollisions();
		
		// update position
		pos.x += (vel.x * deltaTime);
		pos.y += (vel.y * deltaTime);
		angle += (angleVel * deltaTime);
		
		if(angle >= 360)
			angle -= 360;
		else if(angle < 0)
			angle += 360;
		
		// update hitbox
		if(hitBox != null)
			hitBox.setLocation((int)pos.x - hitBox.getWidth()/2, (int)pos.y - hitBox.getHeight()/2);
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
	
	private void resolveCollisions()
	{
		ArrayList<Entity> collisions = SpriteManager.getInstance().checkCollisions(this);
		for(int i = 0; i < collisions.size(); i++)
		{
			// brute force check for the point of collision because whatever
			Entity b = collisions.get(i);
			System.out.print(toString() + " collides with ");
			System.out.println(b.toString());
			if(b.physMode == PHYSMODE_DYNAMIC)
			{
				Vector2d p = collisionPoint(hitBox, collisions.get(i).hitBox);
			
				Vector2d relDisA = pos.vectorDistance(p).getRightNormal();
				Vector2d relVelA = new Vector2d(vel.x + relDisA.x * angleVel, vel.y + relDisA.y * angleVel);
				
				Vector2d relDisB = b.pos.vectorDistance(p).getRightNormal();
				Vector2d relVelB = new Vector2d(b.vel.x + relDisB.x * b.angleVel, b.vel.y + relDisB.y * b.angleVel);
				
				Vector2d relVel = new Vector2d(relVelA.x - relVelB.x, relVelA.y - relVelB.y);
				Vector2d normal = collisionNormal(hitBox, b.hitBox);
				normal = new Vector2d(normal.x * b.angle, normal.y * b.angle);
				normal = normal.getRightNormal().normalize();
				System.out.println(normal.toString());
				
				double j = (-(1 + elasticity) * relVel.dot(normal))
						/ ((1/mass + 1/b.mass)
								+ (Math.pow(relDisA.cross(normal), 2) / momentOfInertia)
								+ (Math.pow(relDisB.cross(normal), 2) / b.momentOfInertia));
				//System.out.println(j);
				// j is positive for body A (this sprite) and negative for body B
				vel.x += (j * normal.x) / mass;
				vel.y += (j * normal.y) / mass;
				angleVel += relDisA.dot(new Vector2d(normal.x * j, normal.y * j)) / momentOfInertia;
				
				//b.vel.x += (-j * normal.x) / b.mass;
				//b.vel.y += (-j * normal.y) / b.mass;
				//b.angleVel += relDisB.dot(new Vector2d(normal.x * -j, normal.y * -j)) / b.momentOfInertia;
			}
		}
	}
	
	private Vector2d collisionPoint(Rectangle r1, Rectangle r2) // FIXME: doesn't return any point
	{
		for(int i = 0; i < r2.getPointCount(); i++)
		{
			Line l2;
			Vector2f v;
			if(i == r2.getPointCount() - 1)
				l2 = new Line(r2.getPoint(i)[0], r2.getPoint(i)[1], r2.getPoint(0)[0], r2.getPoint(0)[1]);
			else
				l2 = new Line(r2.getPoint(i)[0], r2.getPoint(i)[1], r2.getPoint(i+1)[0], r2.getPoint(i+1)[1]);
			
			for(int k = 0; k < r1.getPointCount(); k++)
			{
				Line l1;
				if(k == r1.getPointCount() - 1)
					l1 = new Line(r1.getPoint(k)[0], r1.getPoint(k)[1], r1.getPoint(0)[0], r1.getPoint(0)[1]);
				else
					l1 = new Line(r1.getPoint(k)[0], r1.getPoint(k)[1], r1.getPoint(k+1)[0], r1.getPoint(k+1)[1]);
				
				if(l1.intersects(l2))
				{
					System.out.println(l1.toString());
					v = l1.intersect(l2);
					return new Vector2d(v.x, v.y);
				}
			}
		}
		return new Vector2d(0, 0);
	}
	
	private Vector2d collisionNormal(Rectangle r1, Rectangle r2)
	{
		for(int i = 0; i < r2.getPointCount(); i++)
		{
			Line l2;
			Vector2f v;
			if(i == r2.getPointCount() - 1)
				l2 = new Line(r2.getPoint(i)[0], r2.getPoint(i)[1], r2.getPoint(0)[0], r2.getPoint(0)[1]);
			else
				l2 = new Line(r2.getPoint(i)[0], r2.getPoint(i)[1], r2.getPoint(i+1)[0], r2.getPoint(i+1)[1]);
			
			for(int k = 0; k < r1.getPointCount(); k++)
			{
				Line l1;
				if(k == r1.getPointCount() - 1)
					l1 = new Line(r1.getPoint(k)[0], r1.getPoint(k)[1], r1.getPoint(0)[0], r1.getPoint(0)[1]);
				else
					l1 = new Line(r1.getPoint(k)[0], r1.getPoint(k)[1], r1.getPoint(k+1)[0], r1.getPoint(k+1)[1]);
				
				if(l1.intersects(l2))
				{
					return new Vector2d(l2.getX2() - l2.getX1(), l2.getY2() - l2.getY1());
				}
			}
		}
		return new Vector2d(0, 0);
	}
	
	
	public void setPhysicsMode(int physMode)
	{
		if(physMode != 1 && physMode != 2 && physMode != 3)
			physMode = 1;
		this.physMode = physMode;
	}
	
	public String toString()
	{
		return name;
	}
	
	// times represented in nanoseconds
	protected final long ONE_SECOND =      (long)1e9;
	protected final long ONE_MILLISECOND = (long)1e6;
	
	public static final int PHYSMODE_STATIC =    1;
	public static final int PHYSMODE_KINEMATIC = 2;
	public static final int PHYSMODE_DYNAMIC =   3;
}
