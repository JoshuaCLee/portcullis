package main;

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