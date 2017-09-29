
package gfx;

public class Transformer {

	int sp = 0;
	int MAX = 12;

	float[] xxs = new float[MAX];
	float[] xys = new float[MAX];
	float[] xzs = new float[MAX];
	float[] xos = new float[MAX];

	float[] yxs = new float[MAX];
	float[] yys = new float[MAX];
	float[] yzs = new float[MAX];
	float[] yos = new float[MAX];

	float[] zxs = new float[MAX];
	float[] zys = new float[MAX];
	float[] zzs = new float[MAX];
	float[] zos = new float[MAX];


	private Matrix m = new Matrix();

	private Matrix t = new Matrix();

	public Matrix matrix() { return m; }

	public void pushMatrix() {
		if (sp >= MAX)
				throw new IllegalStateException("Stack is full");

		xxs[sp] = m.xx;	xys[sp] = m.xy;	xzs[sp] = m.xz;	xos[sp] = m.xo;
		yxs[sp] = m.yx;	yys[sp] = m.yy;	yzs[sp] = m.yz;	yos[sp] = m.yo;
		zxs[sp] = m.zx;	zys[sp] = m.zy;	zzs[sp] = m.zz;	zos[sp] = m.zo;
		sp++;
	}

	public void popMatrix() {
		if (sp <= 0)
				throw new IllegalStateException("Stack is empty");

		sp--;
		m.xx = xxs[sp];	m.xy = xys[sp];	m.xz = xzs[sp];	m.xo = xos[sp];
		m.yx = yxs[sp];	m.yy = yys[sp];	m.yz = yzs[sp];	m.yo = yos[sp];
		m.zx = zxs[sp];	m.zy = zys[sp];	m.zz = zzs[sp];	m.zo = zos[sp];

	}

	private void popTemp() {
		if (sp <= 0)
			throw new IllegalStateException("Stack is empty");

		sp--;
		t.xx = xxs[sp];	t.xy = xys[sp];	t.xz = xzs[sp];	t.xo = xos[sp];
		t.yx = yxs[sp];	t.yy = yys[sp];	t.yz = yzs[sp];	t.yo = yos[sp];
		t.zx = zxs[sp];	t.zy = zys[sp];	t.zz = zzs[sp];	t.zo = zos[sp];

	}


	//mul current with first on stack, result in m
	private void mul() {
		popTemp();		//pop into the t matrix
		m.mult(t);
	}

	public void begin() {
		pushMatrix();
		m.unit();
	}

	public void end() {
		mul();
	}

}