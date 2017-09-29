
package area;

import root.SpaceMax;

public class Area {

	private int x;
	private int y;

	public Area() {
		this.x = (int) (.5 + SpaceMax.any() );
		this.y = (int) (.5 + SpaceMax.any() );
	}

	public Area(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Area(Area a) {
		setTo(a);
	}

	public void setTo(Area a) {
		x = a.getX();
		y = a.getY();
	}

	public int getX() { return x; }
	public int getY() { return y; }

	public Area copy() {
		return new Area(x,y);
	}

	public static int pixelArea() {
		return 4*SpaceMax.HORIZON*SpaceMax.HORIZON;
	}

	//both areas shifted SpaceMax.HORIZON to south and east
	public boolean intersects(Area a) {
		int north = y;
		int south = y+2*SpaceMax.HORIZON;
		int west  = x;
		int east  = x+2*SpaceMax.HORIZON;

		int _north = a.getY();
		int _south = _north + 2*SpaceMax.HORIZON;
		int _west  = a.getX();
		int _east  = _west + 2*SpaceMax.HORIZON;

		if (north >= _south) return false;
		if (south <  _north) return false;
		if (west  >  _east)  return false;
		if (east <=  _west)  return false;

		return true;
	}

	public void move(float _x, float _y) {
		move(
			(int) (.5 + _x),
			(int) (.5 + _y)
			);
	}

	//Reposition in case world size has changed
	public void move() {
		move(x,y);
	}

	public void move(int _x, int _y) {

		x = _x;
		y = _y;

		if (isExiled() ) return;

		int max = (int) (.5 + SpaceMax.H);

		if (x < -max ) {
			x = -max;
		} else if (x > max) {
			x = max;
		}

		if (y < -max ) {
			y = -max;
		} else if (y > max) {
			y = max;
		}

	}

	public void setExiledPos() {
		x = (int) SpaceMax.EXILE;
		y = (int) SpaceMax.EXILE;
	}

	public String getOverview() {	return isExiled()?"EXILED":"";	}

	public boolean isExiled() {
		return x == (int) (SpaceMax.EXILE) &&
			y == (int) (SpaceMax.EXILE);

	}

	public String state() {
		if (isExiled()) return "exiled";
		else return "(" + x + "," + y + "), h=" + SpaceMax.HORIZON;

	}


	public boolean equals(Area a) {
		int _x = a.getX();
		int _y = a.getY();
		return x==_x&&y==_y;
	}

	public String status() {
		StringBuffer b = new StringBuffer(100);
		if (isExiled() ) {
			b.append("exiled");
		} else {
			String cell = SpaceMax.desc(this);
			b.append("pos=(" + x + "," + y + "),cell:" + cell);
		}
		return b.toString();
	}


	//Distance between this area and Area a in pixels
	public int centerDistance(Area a) {

		float dx = (float) (x - a.getX() );
		float dy = (float) (y - a.getY() );

		float d2 = dx*dx + dy*dy;

		return (int) (.5 + Math.sqrt(d2) );
	}

	//Number of SpaceMax.HORIZON s between area centers
	public float horizons(Area a) {
		float dx = (float) (x - a.getX() );
		float dy = (float) (y - a.getY() );

		float d2 = dx*dx + dy*dy;

		d2 = (float) Math.sqrt(d2);
		d2 = d2/SpaceMax.HORIZON;

		return d2;

	}

	public double centerAngle(Area a) {

		int dx = a.getX() - x;
		int dy = a.getY() - y;

		double rad = Math.atan2(dy,-dx);
		return rad;
	}

	//How deep down these two areas share warp level (0...max)

	//Costly operation, can be done smarter. (dx and dy between areas,
	//integer divides, look at rest, et.c.)

	public int commonLevel(Area a) {
		if (a == null) throw new IllegalStateException("area is null");

		if (SpaceMax.levels() == 0) {
			//System.out.println("Area.commonLevel:warp system not running");
			return 0;
		}

		if (this == a) {
			System.out.println("Area.commonLevel:Testing against self");
			return SpaceMax.levels();
		}


		SpaceMax.recalc(this);
		//String s = SpaceMax.desc(this);
		//System.out.println("this =" + s);

		int[] r = SpaceMax.getRoute();	//this calced route
		int[] _r = new int[SpaceMax.levels()];

		for(int i = 0; i < _r.length; i++) {
			_r[i] = r[i];
		}

		SpaceMax.recalc(a);
		//s = SpaceMax.desc(a);
		//System.out.println("param=" + s);

		r = SpaceMax.getRoute();
		int common = SpaceMax.commonLevels(_r);

		return common;
	}


	public static void main(String[] args)  {

		Area a = new Area(1258394,21253118);
		Area b = new Area(-19858,233200);

		SpaceMax.setSize(10000000);

		int common = a.commonLevel(b);

		System.out.println("got #common levels: " + common);

	}

}