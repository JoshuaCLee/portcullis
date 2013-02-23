package main;

import java.util.ArrayList;
import java.util.List;

//import org.lwjgl.util.Rectangle;
import org.newdawn.slick.geom.Rectangle;

public class Quadtree 
{
	private int MAX_OBJECTS = 10;
	private int MAX_LEVELS = 5;

	private int level;
	private ArrayList<Entity> entities;
	private Rectangle bounds;
	private Quadtree[] nodes;

	public Quadtree(int level, Rectangle bounds)
	{
		this.level = level;
		this.bounds = bounds;
		entities = new ArrayList<Entity>();
		nodes = new Quadtree[4];
	}

	public void clear()
	{
		entities.clear();

		for(int i = 0; i < nodes.length; i++)
		{
			if(nodes[i] != null)
			{
				nodes[i].clear();
				nodes[i] = null;
			}
		}
	}

	public void split()
	{
		int subWidth = (int)(bounds.getWidth() / 2);
		int subHeight = (int)(bounds.getHeight() / 2);
		int x = (int)bounds.getX();
		int y = (int)bounds.getY();

		nodes[0] = new Quadtree(level++, new Rectangle(x + subWidth, y, subWidth, subHeight));
		nodes[1] = new Quadtree(level++, new Rectangle(x, y, subWidth, subHeight));
		nodes[2] = new Quadtree(level++, new Rectangle(x, y + subHeight, subWidth, subHeight));
		nodes[3] = new Quadtree(level++, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
	}

	public int getIndex(Rectangle pRect)
	{
		int index = -1;
		double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
		double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

		// Object can completely fit within the top quadrants
		boolean topQuadrant = (pRect.getY() < horizontalMidpoint && pRect.getY() + pRect.getHeight() < horizontalMidpoint);
		// Object can completely fit within the bottom quadrants
		boolean bottomQuadrant = (pRect.getY() > horizontalMidpoint);

		// Object can completely fit within the left quadrants
		if (pRect.getX() < verticalMidpoint && pRect.getX() + pRect.getWidth() < verticalMidpoint) 
		{
			if (topQuadrant) 
			{
				index = 1;
			}
			else if (bottomQuadrant) 
			{
				index = 2;
			}
		}
		// Object can completely fit within the right quadrants
		else if (pRect.getX() > verticalMidpoint) 
		{
			if (topQuadrant) 
			{
				index = 0;
			}
			else if (bottomQuadrant) 
			{
				index = 3;
			}
		}

		return index;
	}

	public void insert(Entity e)
	{
		if (nodes[0] != null) 
		{
			int index = getIndex(e.hitBox);

			if (index != -1) 
			{
				nodes[index].insert(e);

				return;
			}
		}

		entities.add(e);

		if (entities.size() > MAX_OBJECTS && level < MAX_LEVELS) 
		{
			if (nodes[0] == null) 
			{
				split();
			}

			int i = 0;
			while (i < entities.size()) 
			{
				int index = getIndex(entities.get(i).hitBox);
				if (index != -1) 
				{
					nodes[index].insert(entities.remove(i));
				}
				else 
				{
					i++;
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List retrieve(List returnObjects, Entity e)
	{
		int index = getIndex(e.hitBox);
		if (index != -1 && nodes[0] != null) 
		{
			nodes[index].retrieve(returnObjects, e);
		}

		returnObjects.addAll(entities);

		return returnObjects;
	}
}
