package main;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex2d;

public class Vector2d 
{
	protected double x;
	protected double y;
	
	public Vector2d(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	// the dot product of this vector and another vector
	public double dot(Vector2d other)
	{
		return (x * other.x) + (y * other.y);
	}
	
	// the cross product of this vector and another vector
	public double cross(Vector2d other)
	{
		return (x * other.y) - (y * other.x);
	}
	
	public double scalarDistance(Vector2d other)
	{
		return Math.hypot(x - other.x, y - other.y);
	}
	
	public Vector2d vectorDistance(Vector2d other)
	{
		return new Vector2d(x - other.x, y - other.y);
	}
	
	public double length()
	{
		return Math.hypot(x, y);
	}
	
	public Vector2d normalize()
	{
		if(length() == 0)
			return new Vector2d(0, 0);
		else
			return new Vector2d(x / length(), y / length());
	}
	
	// returns a vector perpendicular to this one
	public Vector2d getRightNormal()
	{
		return new Vector2d(-y, x);
	}
	
	public Vector2d getLeftNormal()
	{
		return new Vector2d(y, -x);
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public void draw()
	{
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glPushMatrix();
		glBindTexture(GL_TEXTURE_2D, 0);
		glPolygonMode(GL_FRONT, GL_LINE);
		glColor3f(0.1f, 0.1f, 30);
		glBegin(GL_QUADS);
			glVertex2d(x - 2, y - 2); // upper-left
			glVertex2d(x + 2, y - 2); // upper-right
			glVertex2d(x + 2, y + 2); // bottom-right
			glVertex2d(x - 2, y + 2); // bottom-left
		glEnd();
		glPolygonMode(GL_FRONT, GL_FILL);
		glPopMatrix();
	}
	
	public boolean equals(Vector2d other)
	{
		if(Math.round(other.x) == Math.round(x) && Math.round(other.y) == Math.round(y))
			return true;
		return false;
			
	}
	
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}
