
package root;


import gfx.Primitives;
import gfx.Model;

import area.Area;
import area.WarpArea;
import root.SpaceMax;

class DuxPrimitives extends Primitives {
	protected final int L = SpaceMax.HORIZON;

	public DuxPrimitives(Model m) {
		super(m);
	}


	public void addArea(Area a) {
		int x = a.getX();
		int y = a.getY();
		addRect(x-L,y-L,2*L,2*L);
	}

	public void addCross(Area a) {
		int x = a.getX();
		int y = a.getY();
		addCross(x,y,2*L/3);

	}

	public void addCross(WarpArea w) {
		int x = (int) w.getX();
		int y = (int) w.getY();
		int h = (int) w.getH();
		addCross(x,y,h);

	}

	//A smaller X
	public void addMark(WarpArea w) {
		int x = (int) w.getX();
		int y = (int) w.getY();
		int h = (int) (w.getH()/3f);
		addX(x,y,h);

	}


	public void addX(Area a) {
		//A   B
 		//  X
		//C   D
		int x = a.getX();
		int y = a.getY();
		addX(x, y, 2*L/2);

	}

}