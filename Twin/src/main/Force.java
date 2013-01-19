package main;

public class Force 
{
	// contains vectors that represent a particular force being applied to a specific point on a sprite's body
	
	protected Point energy; // describes the amount of force to be applied
	protected Point position; // describes where on the body the force will be applied. position of (-1, -1) represents force applied at the center of mass.
	protected String type;
	
	public Force(Point energy, Point position, String type)
	{
		this.energy = energy;
		this.position = position;
		this.type = type;
	}
	
	public Force(Point energy, String type)
	{
		this.energy = energy;
		this.position = new Point(-1, -1);
		this.type = type;
	}
	
	public Point getEnergy()
	{
		return energy;
	}
	
	public Point getPosition()
	{
		return position;
	}
	
	public String getType()
	{
		return type;
	}
}
