package main;
import java.awt.geom.Point2D;


public class Point extends Point2D 
{
	protected double x;
	protected double y;
	
	public Point(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Point(double x, double y)
	{
		this.x = (float) x;
		this.y = (float) y;
	}

	@Override
	public double getX() 
	{
		return x;
	}

	@Override
	public double getY() 
	{
		return y;
	}

	public void setLocation(float x, float y) 
	{
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void setLocation(double x, double y)
	{
		this.x = (float) x;
		this.y = (float) y;
	}

}
