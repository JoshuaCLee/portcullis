package main;

public class Force 
{
	// contains vectors that represent a particular force being applied to a specific point on a sprite's body
	
	protected Vector2d energy; // describes the amount of force to be applied
	protected Vector2d position; // describes where on the body the force will be applied. position of (-1, -1) represents force applied at the center of mass.
	protected String type;
	
	public Force(Vector2d energy, Vector2d position, String type)
	{
		this.energy = energy;
		this.position = position;
		this.type = type;
	}
	
	public Force(Vector2d energy, String type)
	{
		this.energy = energy;
		this.position = new Vector2d(-1, -1);
		this.type = type;
	}
	
	public Vector2d getEnergy()
	{
		return energy;
	}
	
	public Vector2d getPosition()
	{
		return position;
	}
	
	public String getType()
	{
		return type;
	}
}
