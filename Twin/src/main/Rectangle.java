package main;

import java.util.ArrayList;
/**
 * A geometric container that also handles collision detection
 */
public class Rectangle 
{
	private double x, y;
	private ArrayList<Vector2d> vertices; // clockwise elements
	private double width, height;
	private double angle;
	
	public Rectangle()
	{
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
		angle = 0;
		vertices = new ArrayList<Vector2d>();
	}
	
	public Rectangle(double width, double height)
	{
		this.x = 0;
		this.y = 0;
		this.width = width;
		this.height = height;
		angle = 0;
		vertices = new ArrayList<Vector2d>();
		createVertices();
	}
	
	public Rectangle(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		angle = 0;
		vertices = new ArrayList<Vector2d>();
		createVertices();
	}
	
	// implementation of Seperating Axis Theorem
	public boolean intersects(Rectangle b) 
	{
		ArrayList<Vector2d> aAxes = getEdgeNormals();
		ArrayList<Vector2d> bAxes = b.getEdgeNormals();
		
		for(int i = 0; i < aAxes.size(); i++)
		{
			Vector2d p1 = project(aAxes.get(i));
			Vector2d p2 = b.project(aAxes.get(i));
			if((p1.x > p2.y || p2.x > p1.y)) // if they overlap they are intersecting on this axis
				return false;
		}
		
		for(int i = 0; i < bAxes.size(); i++)
		{
			Vector2d p1 = project(bAxes.get(i));
			Vector2d p2 = b.project(bAxes.get(i));
			if((p1.x > p2.y || p2.x > p1.y)) // if they overlap they are intersecting on this axis
				return false;
		}
		return true;
	}
	
	public Vector2d intersect(Rectangle b) 
	{
		double overlap = Double.MAX_VALUE; // depth
		Vector2d collisionNormal = null; // minimum penetration vector or collision normal
		ArrayList<Vector2d> aAxes = getEdgeNormals();
		ArrayList<Vector2d> bAxes = b.getEdgeNormals();
		
		for(int i = 0; i < aAxes.size(); i++)
		{
			Vector2d p1 = project(aAxes.get(i));
			Vector2d p2 = b.project(aAxes.get(i));
			if((p1.x > p2.y || p2.x > p1.y)) // if they overlap they are intersecting on this axis
				return null;
			else
			{
				double o = Math.min(p1.y, p2.y) - Math.max(p1.x, p2.x);
				if(o < overlap)
				{
					overlap = o;
					collisionNormal = aAxes.get(i);
				}
			}
		}
		
		for(int i = 0; i < bAxes.size(); i++)
		{
			Vector2d p1 = project(bAxes.get(i));
			Vector2d p2 = b.project(bAxes.get(i));
			if((p1.x > p2.y || p2.x > p1.y)) // if they overlap they are intersecting on this axis
				return null;
			else
			{
				double o = Math.min(p1.y, p2.y) - Math.max(p1.x, p2.x);
				if(o < overlap)
				{
					overlap = o;
					collisionNormal = bAxes.get(i);
				}
			}
		}
		// collision normal and penetration depth has been found
		// now we find the most perpendicular edges to the collision normal
		Line aEdge = bestEdge(collisionNormal);
		Line bEdge = b.bestEdge(new Vector2d(-collisionNormal.x, -collisionNormal.y));
		// determine reference and incident edges
		Line reference, incident;
		Vector2d ref;
		boolean flipped = false;
		if(aEdge.toVector2d().dot(collisionNormal) <= bEdge.toVector2d().dot(collisionNormal))
		{
			reference = aEdge;
			incident = bEdge;
		}
		else
		{
			reference = bEdge;
			incident = aEdge;
			flipped = true;
		}
		ref = reference.toVector2d().normalize();
		
		double offset1 = ref.dot(reference.getPoint1());
		ArrayList<Vector2d> clipPoints = clip(incident.getPoint1(), incident.getPoint2(), new Vector2d(-ref.x, -ref.y), -offset1);
		if(clipPoints.size() < 2)
			return null;
		System.out.println("clip1 success");
		
		double offset2 = ref.dot(reference.getPoint2());
		clipPoints = clip(clipPoints.get(0), clipPoints.get(1), ref, offset2);
		if(clipPoints.size() < 2)
			return null;
		System.out.println("clip2 success");
		
		Vector2d refNormal = ref.getLeftNormal();
		if(flipped)
			refNormal = new Vector2d(refNormal.x * -1, refNormal.y * -1);
		double max = refNormal.dot(reference.getPoint1());
		if (refNormal.dot(clipPoints.get(0)) - max < 0.0)
		{	
			clipPoints.remove(clipPoints.get(0));
			if(!clipPoints.isEmpty())
				if (refNormal.dot(clipPoints.get(0)) - max < 0.0) 
					clipPoints.remove(clipPoints.get(0));
		}
		if(!clipPoints.isEmpty())
		{
			Vector2d collision = new Vector2d(0, 0);
			for(int i = 0; i < clipPoints.size(); i++)
			{
				collision = new Vector2d(collision.x + clipPoints.get(i).x, collision.y + clipPoints.get(i).y);
			}
			collision = new Vector2d(collision.x / clipPoints.size(), collision.y / clipPoints.size());
			return collision;
		}
		else
			return null;
	}
	
	public Line bestEdge(Vector2d collisionNormal) // FIXME
	{
		double max = -Double.MAX_VALUE;
		int index = -1;
		System.out.println(collisionNormal);
		for(int i = 0; i < vertices.size(); i++)
		{
			double projection = collisionNormal.dot(vertices.get(i));
			if(projection > max)
			{
				max = projection;
				index = i;
			}
		}
		// now we find the most perpendicular edge
		Vector2d v = vertices.get(index);
		Vector2d v1, v0;
		
		if(index + 1 == vertices.size())
			v1 = vertices.get(0);
		else
			v1 = vertices.get(index + 1);
		if(index == 0)
			v0 = vertices.get(vertices.size() - 1);
		else
			v0 = vertices.get(index - 1);
		
		Vector2d left = new Vector2d(v.x - v1.x, v.y - v1.y);
		Vector2d right = new Vector2d(v.x - v0.x, v.y - v0.y);
		System.out.println("right: " + right + " left: " + left);
		if(Math.abs(right.dot(collisionNormal)) <= Math.abs(left.dot(collisionNormal)))
		{
			//System.out.println("return right");
			v0.draw();
			v.draw();
			return new Line(v0, v);
		}
		else
		{
			//System.out.println("return left");
			v.draw();
			v1.draw();
			return new Line(v, v1);
		}
	}
	
	public ArrayList<Vector2d> clip(Vector2d v1, Vector2d v2, Vector2d reference, double o)
	{
		ArrayList<Vector2d> clipPoints = new ArrayList<Vector2d>();
		double d1 = reference.dot(v1) - o;
		double d2 = reference.dot(v2) - o;
		//System.out.println("d1: " + d1 + " d2: " + d2 + " v1: " + v1 + " v2: " + v2 + " ref: " + reference + " offset: " + o);
		
		if(d1 >= 0)
			clipPoints.add(v1);
		if(d2 >= 0)
			clipPoints.add(v2);
		
		if(d1 * d2 < 0)
		{
			Vector2d e = new Vector2d(v2.x - v1.x, v2.y - v1.y);
			double u = d1 / (d1 - d2);
			e = new Vector2d((e.x * u) + v1.x, (e.y * u) + v1.y);
			clipPoints.add(e);
		}
		return clipPoints;
	}
	
	public ArrayList<Vector2d> getEdgeNormals()
	{
		ArrayList<Line> edges = getEdges();
		ArrayList<Vector2d> normals = new ArrayList<Vector2d>();
		for(int i = 0; i < edges.size(); i++)
		{
			double x = edges.get(i).getPoint1().x - edges.get(i).getPoint2().x;
			double y = edges.get(i).getPoint1().y - edges.get(i).getPoint2().y;
			normals.add(new Vector2d(x, y).normalize().getLeftNormal());
		}
		return normals;
	}
	
	public Vector2d project(Vector2d axis)
	{
		double min = axis.dot(vertices.get(0));
		double max = min;
		for(int i = 0; i < vertices.size(); i++)
		{
			double p = axis.dot(vertices.get(i));
			if(p < min)
				min = p;
			else if(p > max)
				max = p;
		}
		return new Vector2d(min, max);
	}
	
	public void createVertices()
	{
		vertices.clear();
		if(angle >= 360)
			angle %= 360;
		else if(angle < 0)
			angle = (angle % 360) + 360;
		double cos = Math.cos(Math.toRadians(angle));
		double sin = Math.sin(Math.toRadians(angle));
		
		Vector2d v1 = new Vector2d(((-width/2) * cos + (-height/2) * sin) + x, 
				                   ((width/2)  * sin + (-height/2) * cos) + y); // -width, -height
		Vector2d v2 = new Vector2d(((width/2)  * cos + (-height/2) * sin) + x, 
				                   ((-width/2) * sin + (-height/2) * cos) + y); // width, -height
		Vector2d v3 = new Vector2d(((width/2)  * cos + (height/2)  * sin) + x, 
				                   ((-width/2) * sin + (height/2)  * cos) + y); // width, height
		Vector2d v4 = new Vector2d(((-width/2) * cos + (height/2)  * sin) + x, 
				                   ((width/2)  * sin + (height/2)  * cos) + y); // -width, height
		vertices.add(v1);
		vertices.add(v2);
		vertices.add(v3);
		vertices.add(v4);
	}
	
	public ArrayList<Vector2d> getVertices()
	{
		createVertices();
		return vertices;
	}
	
	public ArrayList<Line> getEdges()
	{
		createVertices();
		ArrayList<Line> lines = new ArrayList<Line>();
		for(int i = 0; i < vertices.size(); i++)
		{
			if(i == vertices.size() - 1)
				lines.add(new Line(vertices.get(i), vertices.get(0)));
			else
				lines.add(new Line(vertices.get(i), vertices.get(i+1)));
		}
		return lines;
	}
	
	public double getAngle()
	{
		return angle;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public double getWidth()
	{
		return width;
	}
	
	public double getHeight()
	{
		return height;
	}
	
	public void setLocation(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void setAngle(double angle)
	{
		this.angle = angle;
	}
	
	public String toString()
	{
		String str = "Pos: (" + x + ", " + y + ") ";
		for(int i = 0; i < vertices.size(); i++)
			str += "V" + i + ": " + vertices.get(i).toString() + " ";
		return str;
	}
}
