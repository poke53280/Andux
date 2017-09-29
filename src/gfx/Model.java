
package gfx;

public class Model {


	private int MAX = 1500;
	private float[] v = new float[MAX];

	private int LMAX = 1500;
	private int[] l = new int[LMAX];

	private int vIndex = 0;
	private int lIndex = 0;

	private int color = 0xFFFFFF;



	Rasterizer r;
	Transformer t;

	public Model(Rasterizer r) {
		this.r = r;
		this.t = new Transformer();
	}

	public Transformer transformer() {
		return t;
	}

	public int addVertex(float x, float y) {
		return addVertex(x,y,0);
	}

	public int addVertex(float x, float y, float z) {

		if (vIndex + 2 >= MAX) {
			System.out.println("Vertex array is full. Trying auto commit");
			commit();
		}
		int i = vIndex;

		v[i+0] = x;
		v[i+1] = y;
		v[i+2] = z;
		vIndex += 3;
		return i/3;
	}

	public void addLine(int n0, int n1) {

		if (lIndex +2 >= LMAX) {
			System.out.println("Line array is full. Trying auto commit");
			commit();
		}

		int j = lIndex;
		l[j+0] = n0;
		l[j+1] = n1;
		l[j+2] = color;
		lIndex +=3;
	}

	public void setColor(int c) {
		color = c;
	}

	//handles empty model, too
	public void commit() {
		transform();
		toRaster();
		clear();
	}


	public void transform() {
		if (vIndex == 0) return; 	//empty model

		Matrix m = t.matrix();
		m.transform(v,vIndex/3);
	}

	public void toRaster() {
		for(int j =0; j < lIndex-1; j+=3) {
			int i0 = l[j+0]*3;
			int i1 = l[j+1]*3;
			r.push(
				v[i0  ],v[++i0],v[++i0],		//P:x,y,z
				v[i1  ],v[++i1],v[++i1],		//Q:x,y,z
				l[j+2]							//color
			);
		}
	}

	public void clear() {
		vIndex = 0;
		lIndex = 0;
	}

}