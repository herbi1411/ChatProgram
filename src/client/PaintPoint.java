package client;

import java.awt.Color;

public class PaintPoint {
	public int x;
	public int y;
	public int px,py;
	public int mw;
	public Color cr;
	
	public PaintPoint(int px, int py, int x, int y, Color cr, int mw) 
	{
		this.px = px;
		this.py = py;
		this.x = x;
		this.y = y;
		this.cr = cr;
		this.mw = mw;
	}
}
