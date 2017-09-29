package gfx;

class SpaceLocation {

	private float x = 0f;	//location in space
	private float y = 0f;
	private float z = 0f;

	private double xrot = 0f;
	private double yrot = 0f;
	private double zrot = 0.0;

	public float x() { return x; }
	public float y() { return y; }
	public float z() { return z; }

	private boolean autoRotate = false;

	public void unit() {
		x = 0f;
		y = 0f;
		z = 0f;
		xrot = 0.0;
		yrot = 0.0;
		zrot = 0.0;

	}

	public void setAutoRotate(boolean on) {
		autoRotate = on;
	}

	public boolean autoRotate() {
		return autoRotate;
	}


	public void setPos(float x, float y, float z) {
		this.x = x; this.y = y; this.z = z;
	}

	public void setRot(double xrot, double yrot, double zrot) {
		this.xrot = xrot;
		this.yrot = yrot;
		this.zrot = zrot;
	}

	public double xrot() { return xrot; }
	public double yrot() { return yrot; }
	public double zrot() { return zrot; }

	public void move(float dx, float dy, float dz) {
		x += dx;	y += dy;	z += dz;
	}

	public void screenMove(float sx, float sy) {
		//Input: wanted move in screen. Find move in
		//model
		float f = 512f;
		float mx = sx*(z+500f)/f;
		float my = sy*(z+500f)/f;

		move(mx,my,0f);

	}

	public void transform(Matrix m) {
		translate(m);
		rotate(m);
	}

	public void translate(Matrix m) {
		m.translate(x,y,z);
	}

	public void rotate(Matrix m) {
		m.xrot(xrot);
		m.yrot(yrot);
		m.zrot(zrot);
	}

}