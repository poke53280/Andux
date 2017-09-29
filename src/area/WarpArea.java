
package area;

import root.SpaceMax;

public class WarpArea {

	//Area at center location (x,y) extending
	//2h in width and height,
	//so that it extends h in
	//each four directions from (x,y)

	float x;
	float y;
	float h;

	public WarpArea(float x, float y, float h) {
		set(x,y,h);
	}

	public void set(float x, float y, float h) {
		this.x = x;
		this.y = y;
		this.h = h;
	}

	public float getX() { return x; }
	public float getY() { return y; }
	public float getH() { return h; }

	public boolean equals(WarpArea wa) {

		return x == wa.getX() &&
				y == wa.getY() &&
				h == wa.getH();


	}


	public String state() {
		return "(" + x + "," + y + "),h=" + h;
	}

	public boolean intersects(Area a) {
		float north = y - h;
		float south = y + h;
		float west  = x - h;
		float east  = x + h;

		int _north = a.getY() - SpaceMax.HORIZON;
		int _south = a.getY() + SpaceMax.HORIZON;
		int _west  = a.getX() - SpaceMax.HORIZON;
		int _east  = a.getX() + SpaceMax.HORIZON;

		//floats and ints
		if (north >= _south) return false;
		if (south <  _north) return false;
		if (west  >  _east)  return false;
		if (east <=  _west)  return false;

		return true;
	}


	//on border returns false
	public boolean containsCenterOf(Area a) {
		float north = y - h;
		float south = y + h;
		float west  = x - h;
		float east  = x + h;

		int _cx = a.getX();
		int _cy = a.getY();

		if (_cx >= east)  return false;
		if (_cx <= west)  return false;
		if (_cy >= south) return false;
		if (_cy <= north) return false;

		return true;

	}

	public static void main(String[] args) {

		WarpArea wa = new WarpArea(0f,0f,101f);

		Area a1 = new Area(99,130);
		Area a2 = new Area(100,100);

		System.out.println("a1=" + a1.state() );
		if (wa.containsCenterOf(a1)) {
			System.out.println("inside");
		} else {
			System.out.println("outside");
		}

		System.out.println("a2=" + a2.state() );
		if (wa.containsCenterOf(a2)) {
			System.out.println("inside");
		} else {
			System.out.println("outside");
		}





	}



}