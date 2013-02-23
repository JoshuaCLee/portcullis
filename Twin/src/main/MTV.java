package main;

public class MTV 
{
	private Vector2d min;
	private double overlap;
	
	public MTV(Vector2d min, double overlap)
	{
		this.min = min;
		this.overlap = overlap;
	}
	
	public Vector2d getMinimumVector()
	{
		return min;
	}
	
	public double getOverlap()
	{
		return overlap;
	}
}
