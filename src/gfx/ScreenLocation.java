
package gfx;

class ScreenLocation {

	private int xmin;
	private int xmax;
	private int ymin;
	private int ymax;
	private int lines;

	public void setBounds(Rasterizer r) {
		lines = r.lines();
		xmin = r.xmin();
		xmax = r.xmax();
		ymin = r.ymin();
		ymax = r.ymax();
	}

	public boolean intersects(int x, int y) {
		if (x < xmin) return false;
		if (x > xmax) return false;
		if (y < ymin) return false;
		if (y > ymax) return false;
		return true;
	}


}