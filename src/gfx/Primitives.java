
package gfx;

public class Primitives {

	protected Model m;
	public Primitives(Model m) {
		this.m = m;
	}

	public void addLine(int x0,int y0, int z0,
						int x1, int y1, int z1) {

		int A = m.addVertex(x0,y0,z0);
		int B = m.addVertex(x1,y1,z1);
		m.addLine(A,B);
	}

	//center point, extends w/2 in all four
	//so each side has length w
	public void addSquare(int w) {
		addSquare(0,0,w);
	}

	//center point, extends w/2 in all four
	public void addSquare(int x, int y, int w) {

		int A = m.addVertex(x+w/2,y+w/2);
		int B = m.addVertex(x-w/2,y+w/2);
		int C = m.addVertex(x-w/2,y-w/2);
		int D = m.addVertex(x+w/2,y-w/2);
		m.addLine(A,B);
		m.addLine(B,C);
		m.addLine(C,D);
		m.addLine(D,A);
	}

	//center point, extends w in all four directions
	public void addX(int x, int y, int w) {
		//A   B
 		//  X
		//C   D
		int A = m.addVertex(x-w,y-w);
		int B = m.addVertex(x+w,y-w);
		int C = m.addVertex(x-w,y+w);
		int D = m.addVertex(x+w,y+w);
		m.addLine(A,D);
		m.addLine(B,C);
	}

	//center point, extends w in all four directions
	public void addRotor(int x, int y, int w, long tick) {
		int frame = (int) (tick%8 );		//frames 0..7
		int A = -1;
		int B = -1;
		if (frame == 0) {		A = m.addVertex(x,y-w);	    B = m.addVertex(x,y+w); 	    } // |
	    if (frame == 1) {		A = m.addVertex(x+w/2,y-w);	B = m.addVertex(x-w/2,y+w); }	//22.5
		if (frame == 2) {		A = m.addVertex(x+w,y-w);	B = m.addVertex(x-w,y+w);	}// 45
		if (frame == 3) {		A = m.addVertex(x+w,y-w/2);	B = m.addVertex(x-w,y+w/2); };	//45+22.5
		if (frame == 4) { 		A = m.addVertex(x-w,y);		B = m.addVertex(x+w,y);  	}// -
		if (frame == 5) {		A = m.addVertex(x+w,y+w/2);	B = m.addVertex(x-w,y-w/2);} //	90+22.5
		if (frame == 6) { 		A = m.addVertex(x-w,y-w);  B = m.addVertex(x+w,y+w);	}// \
		if (frame ==7) {	A = m.addVertex(x+w/2,y+w);	B = m.addVertex(x-w/2,y-w); } //		135+22.5

		if (A == -1 && B == -1) throw new IllegalStateException("bad frame");
		m.addLine(A,B);
	}

	//center to left, w thick, extending l to the right
	public void addBar(int x, int y, int w, int l) {
		addRect(x,y-w/2,l,w);
	}

	//center point, extends w in all four directions
	public void addCross(int x, int y, int w) {
		//    C
		//  A   B
		//    D

		int A = m.addVertex(x-w,y);
		int B = m.addVertex(x+w,y);
		int C = m.addVertex(x,y-w);
		int D = m.addVertex(x,y+w);
		m.addLine(A,B);
		m.addLine(C,D);
	}

	//top left corner, extend w and h from there
	public void addRect(int x, int y, int w, int h) {

		int A = m.addVertex(x,y);
		int B = m.addVertex(x+w,y);
		int C = m.addVertex(x+w,y+h);
		int D = m.addVertex(x,y+h);
		m.addLine(A,B);
		m.addLine(B,C);
		m.addLine(C,D);
		m.addLine(D,A);
	}

	public int addRects(int x, int y, int n, int w) {
		for(int i = 0; i < n; i++) {
			addRect(x,y,w,w);
			x += (w+w/2);
		}
//		if (n > 0) flushTemp();

		return x;
	}

	public int addCrosses(int x, int y, int n, int w) {
		for(int i = 0; i < n; i++) {
			addCross(x,y,w);
			x += (2*w+w/2);
		}
//		if (n > 0) flushTemp();

		return x;
	}


}