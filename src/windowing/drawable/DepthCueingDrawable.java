package windowing.drawable;

import windowing.graphics.Color;


public class DepthCueingDrawable extends DrawableDecorator {

	private int row;
	private int col;
	private double [][] zbuffer;
	private double nc;
	private double fc;
	private Color color;

	public DepthCueingDrawable(Drawable delegate, int nearclip, int farclip, Color color) 
	{
		super(delegate);
		this.color = color;
		this.row = delegate.getHeight();
		this.col = delegate.getWidth();
		this.zbuffer = new double[row][col];

		nc = nearclip;
		fc = farclip;
		reset_z();		
	}

	@Override
	public void clear() 
	{
		fill(ARGB_BLACK, Double.MAX_VALUE);
		reset_z();
	}
	
	@Override
	public void setPixel(int x, int y, double z, int argbColor) 
	{
		double scaleFactor = (z - fc)/Math.abs(fc); 
		
		if ((x > 0 && x < col) && (y > 0 && y < row))
		{
			if (z < nc && z > zbuffer[y][x])
			{
				delegate.setPixel(x, y, z, color.scale(scaleFactor).asARGB());
				zbuffer[y][x] = z;
			}
		}
	}
	
	private void reset_z()
	{
		for (int y = 0; y < row; y++) {
			for (int x = 0; x < col; x++) {
				zbuffer[y][x] = fc;
			}
		}
	}
}