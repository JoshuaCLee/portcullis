package main;

public class Line 
{
	private Vector2d p1;
	private Vector2d p2;
	
	public Line()
	{
		p1 = new Vector2d(0, 0);
		p2 = new Vector2d(0, 0);
	}
	
	public Line(double x1, double y1, double x2, double y2)
	{
		p1 = new Vector2d(x1, y1);
		p2 = new Vector2d(x2, y2);
	}
	
	public Line(Vector2d p1, Vector2d p2)
	{
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public Vector2d getPoint1()
	{
		return p1;
	}
	
	public Vector2d getPoint2()
	{
		return p2;
	}
	
	public Vector2d toVector2d()
	{
		return new Vector2d(p1.x - p2.x, p1.y - p2.y);
	}
}
