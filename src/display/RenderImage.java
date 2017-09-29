package display;

public final class RenderImage {

	public int x;
	public int y;
	public int imageRef;
	public int id = 0;
	public int state = 0;
	public int energy = 0;
	public int score = 0;
	public String name = null;

	public long expireTime = 0;

	public boolean isMissile = false;


    public RenderImage(int x,
    				   int y,
		    		   int imageRef) {

		this.x = x;
		this.y = y;
		this.imageRef = imageRef;


	}

	public RenderImage() {
		this.x = -1;
		this.y = -1;
	}


	public static synchronized RenderImage[] createBuffer(int size) {
		RenderImage[] r = new RenderImage[size];

		for (int i = 0; i < size; i++) {
			r[i] = new RenderImage();
		}
		return r;

	}


}