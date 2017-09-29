
package root;

import area.WarpArea;
import area.Area;

public class SpaceMax {

	private static final float HMAX = 1000f*1000f*1000f;


	//Agent factor used to deterimine proximity or not
	//public static final float HIT_DISTANCE = 0.12f;

	//H must never be changed from the outside.
	//create getH method on it for safer play.

	public static float H = 2048f;
	public static final float EXILE = HMAX + 10f;

	public static final int HORIZON = 200;

	public static final float NEAR = 2f*1.4f;  //number of horizons
//	public static float FAR  = 25f;	   			//number of horizons
//	private static float WARP_FACTOR = 23f;

	public static float FAR  = 10f;	   			//number of horizons
	private static float WARP_FACTOR = 9f;


	private static WarpArea[] all;
	private static WarpArea[] quarter;
	private static WarpArea[] warps;
	private static int[] shuffled;		//all numbers from 0..3l-1 in random order

	private static int shuffleIndex = 0;

	private static int[] route;

	static {
		if (H > HMAX)
			throw new IllegalStateException("World too big");

		setSize((int) H);
	}


	//far interest of local node. Beyond far*horizon, nodes are not
	//locally interesting.
	public static void setFar(float f) {
		if (f <= NEAR)
			throw new IllegalArgumentException("far too small");

		FAR = f;


	}



	public static void setSize(int h) {
		int oldL = levels();

		if (!sizeOK(h) )
			throw new IllegalArgumentException("Bad size");

		//System.out.println("SpaceMax.size set to " + h);
		H = h;


		int l = levels();
		//resize and refill warp arrays
		if (l == 0) {
			//System.out.println("warp disabled");
			all = null;
			quarter = null;
			warps = null;
			route = null;
			shuffled = null;
			shuffleIndex = 0;
		} else {
			//System.out.println("warp enabled");

			all = new WarpArea[4*l];
			quarter = new WarpArea[l];
			route = new int[l];
			warps = new WarpArea[3*l];
			shuffled = new int[3*l];

			for(int i = 0; i < all.length; i++) {
				all[i] = new WarpArea(0f,0f,0f);
			}

			for(int i = 0; i < 3*l; i++) {
				shuffled[i] = i;	//all the indices to shuffle
			}
			shuffleIndex = 0;

		}

		if (oldL != l) System.out.println("#Number of warp levels changed");

	}

	public static boolean sizeOK(int h) {
		if (h < 1) return false;
		if (h > HMAX) return false;
		return true;
	}

	//returns number of levels paramter route and local route share
	public static int commonLevels(int[] _route) {
		if (route == null || _route == null)
			throw new IllegalStateException("route array(s) null");

		if (route.length != _route.length)
			throw new IllegalStateException("route arrays of different size");


		for (int i = 0; i < route.length; i++) {
			if (route[i] != _route[i]) {
				return i;
			}
		}
		return route.length;	//all alike

	}

	public static int[] getRoute() {
		return route;
	}


	//returns 0 when warp is disabled
	public static int levels() {
		float cell_min = 2f*WARP_FACTOR * HORIZON;

		if (H <= cell_min) {
			return 0;	//warp not enabled
		}

		double d = H/cell_min;
		d = Math.log(d)/ Math.log(2.0);
		return 1 + (int) d;
	}

	public static float any() {
		float r = (float) Math.random();
		float y = -H + r*( 2f* H -1f);
		return y;
	}

	public static void shuffle() {
		int l = levels();
		if (l == 0) {
			return;
		}

		int maxIndex = 3*l-1;
		for (int i = 0; i < maxIndex; i++) {
			int index = i + (int) (.5 + Math.random()*(maxIndex-i));
			if (i != index) {
				int tmp = shuffled[i];
				shuffled[i] = shuffled[index];
				shuffled[index] = tmp;
			}
		}
		shuffleIndex = 0;		//ready to display random order
	}

	public static WarpArea nextWarp() {
		int l = levels();

		if (l == 0) {
			throw new IllegalStateException("warp not running");
		}

		if (shuffleIndex >= 3*l) {
			throw new IllegalStateException("Out of warps, shuffleIndex=" + shuffleIndex);
		}

		int index = shuffled[shuffleIndex];
		shuffleIndex++;
		return warps[index];

	}

	public static boolean shuffleEmpty() {
		return shuffleIndex >= 3*levels();
	}

	public static boolean isWarp(WarpArea wa) {
		int l = levels();
		if (l == 0) {
			throw new IllegalStateException("warp not running");
		}
		for(int i = 0; i < 3*l; i++) {
			WarpArea _wa = warps[i];
			if (_wa.equals(wa)) return true;	//found
		}
		return false;		//was never found -> unknown -> not a warp

	}




	//x big to right
	//y big down
	// II   I
	// III  IV
	public static void main(String[] args)  {

			try {
				float x = (float) Integer.parseInt(args[0]);
				float y = (float) Integer.parseInt(args[1]);
				int h = Integer.parseInt(args[2]);

				setSize(h);

				if (levels() == 0) {
					System.out.println("warp disabled");
					return;
				}

				recalc(x,y);
				String newD = desc(x,y);


				System.out.println("# levels: " + levels() );
			//	System.out.println("\nlocation:\n" + show(quarter) );
				System.out.println("\nwarplocs:\n" + show(warps) );

			//	WarpArea w = any(quarter);
			//	System.out.println("\nany warp loc area(new): " + w.state() );


				//System.out.println("\nnew desc: " + newD);
				//System.out.println("\nold desc: " + oldD);
			//	shuffle();

			//	System.out.println("\nshowShuffle1:" + showShuffle() );

			//	shuffle();
			//	System.out.println("\nshowShuffle2:" + showShuffle() );


			shuffle();

			System.out.println("-----------------------random order---");
			for (int i = 0; i < 3*levels(); i++) {

				WarpArea wa = nextWarp();
				System.out.println(wa.state() );


			}


			} catch (NumberFormatException e) {
				System.out.println("bad numbers");
			}

	}

	//null if warp is not enabled.
	public static WarpArea getWarp(Area a) {
		if (levels() == 0) return null;

		float x = (float) a.getX();
		float y = (float) a.getY();

		recalc(x,y);
		return any(warps);
	}

	public static WarpArea[] getQuarter(Area a) {
		if (levels() == 0) throw new IllegalStateException("0 levels");

		float x = (float) a.getX();
		float y = (float) a.getY();

		recalc(x,y);
		return quarter;

	}


	public static void recalc(Area a) {


		float x = (float) a.getX();
		float y = (float) a.getY();

		recalc(x,y);
		shuffle();

	}
	//
	//x big to right
	//y big down
	// II   I
	// III  IV


	public static String desc(Area a) {
		if (levels() == 0) return "disabled";

		float x = (float) a.getX();
		float y = (float) a.getY();
		return desc(x,y);
	}

	private static String desc(float x, float y) {
		int l = levels();
		if (l == 0)
			throw new IllegalStateException("Warp disabled");

		recalc(x,y);

		StringBuffer b = new StringBuffer(2*l);
		for(int i = 0; i < l; i++) {
			if (i > 0)	b.append(':');
			b.append(route[i]);
		}
		return b.toString();

	}

	//2 1
	//3 4
	private static void recalc(float x, float y) {
		if (levels() == 0)
			throw new IllegalStateException("Warp disabled");


		float cx = 0f;
		float cy = 0f;
		float h = H;

		float cell_min = 2f*HORIZON * WARP_FACTOR;

		int allIndex = 0;
		int quarterIndex = 0;
		int warpIndex = 0;

		WarpArea wa;
		while ( h >= cell_min) {
			h = h/2f;

			int q;
			if (x < cx) {
				if ( y < cy) {
					q = 2;
				} else {
					q = 3;
				}
			} else {
				if ( y < cy) {
					q = 1;
				} else {
					q = 4;
				}
			}

			//3 outside
			if (q != 1) {
				wa = all[allIndex++];
				wa.set(cx+h,cy-h,h);
				warps[warpIndex++] = wa;
			}
			if (q != 2) {
				wa = all[allIndex++];
				wa.set(cx-h,cy-h,h);
				warps[warpIndex++] = wa;
			}
			if (q != 3) {
				wa = all[allIndex++];
				wa.set(cx-h,cy+h,h);
				warps[warpIndex++] = wa;
			}
			if (q != 4) {
				wa = all[allIndex++];
				wa.set(cx+h,cy+h,h);
				warps[warpIndex++] = wa;
			}

			//1 inside
			if (q == 2) { cx -= h; cy -= h; }
			if (q == 3) { cx -= h; cy += h; }
			if (q == 1) { cx += h; cy -= h; }
			if (q == 4) { cx += h; cy += h; }
			wa = all[allIndex++];
			wa.set(cx,cy,h);
			route[quarterIndex] = q;
			quarter[quarterIndex++] = wa;



		}
	}


	private static String show(WarpArea[] w) {
		if (w == null || w.length==0) return "no data";

		StringBuffer b = new StringBuffer(w.length*10);
		for (int i = 0; i < w.length; i++) {
			WarpArea _w = w[i];
			b.append('\n');
			b.append(_w.state() );

		}
		return b.toString();

	}

	private static WarpArea any(WarpArea[] w) {
		if (w == null || w.length==0) return null;
		int index = (int) Math.round(Math.random()*(w.length-1 ) );
		return w[index];
	}

}