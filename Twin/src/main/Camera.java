package main;

/**
 * A glorified container for offsets. These are used to calculate where on the screen sprites should draw themselves.
 */
public class Camera extends Entity 
{
	public Camera(int width, int height) 
	{
		//super(50, 50, width, height);
		super();
		pos = new Vector2d(50, 50);
		//setBounds(new Rectangle(width, height));
	}
}

// TODO: elaborate behaviors that a camera might perform
