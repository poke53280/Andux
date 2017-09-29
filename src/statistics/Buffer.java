
package statistics;

public class Buffer {

	private float NO_VALUE = 0f;

	private final float[] data;
	private int next = 0;
	private boolean wrapped = false;

	private float sum = 0f;

	private float min = NO_VALUE;
	private float max = NO_VALUE;

	private int HEIGHT;

	private float lastValue = NO_VALUE;

	private String name = "NONAME";

	//part of HEIGHT used from min to max value
	//rest of HEIGHT is air above and below
	private float FILLED_RATIO = 0.8f;


	public Buffer(int capacity, int h, String name) {
		data = new float[capacity];
		HEIGHT = h;

		if (name != null && !name.equals("") ) this.name = name;
	}

	public String name() {
		return name;
	}


	//used slots. (0 is empty)
	public int size() {
		return wrapped?data.length:next;
	}

	//max size
	public int capacity() {
		return data.length;
	}

	public void clear() {
		next = 0;
		wrapped = false;
		sum = 0;
		min = NO_VALUE;
		max = NO_VALUE;
		lastValue = NO_VALUE;
	}

	public void add(int i) {
		float f = (float) i;
		add(f);
	}


	public void add(float i) {
		if (next >= data.length) { //overwrap
			wrapped = true;
			next = 0;
		}

		if (wrapped) {
			sum -= data[next];
		}
		sum += i;

		checkExtreme(i);
		data[next] = i;
		lastValue = i;

		next++;
	}

	//0...(max-1)
	public void addRandom(int max) {
		float value = (float) Math.random()*max;
		add(value);
	}

	private void checkExtreme(float i) {
		//Always extreme if only value
		if(size() == 0) {
			min = i;
			max = i;
			return;
		}
		if (i > max) max = i;
		if (i < min) min = i;

	}



	public void show() {
		//oldest first

		System.out.println("--------------");

		if(!wrapped && next == 0) {
			System.out.println("buffer empty");
			//can return
		}

		if (wrapped) {
			for (int i = next; i < data.length; i++) {
				System.out.println("" + i + ": " + data[i] );
			}
		}
		for (int i = 0; i < next; i++) {
			System.out.println("" + i + ": " + data[i] );
		}
		System.out.println("#elements: " + size() );
		System.out.println("capacity: " + capacity() );
		System.out.println("buffer sum= " + sum);
		System.out.println("value average= " + average() );
		System.out.println("value min = " + min);
		System.out.println("value max = " + max);

		System.out.println("scaled average = " + scaled(average() ) );
		System.out.println("scaled max = " + scaled(max) );
		System.out.println("scaled min = " + scaled(min) );
		System.out.println("scaled x   = " + scaled(0f) );


	}

	public boolean isWrapped() {
		return wrapped;
	}

	public int next() {
		return next;
	}

	public float[] data() {
		return data;
	}

	public float average() {
		//average, or default value if empty
		return size() == 0?	NO_VALUE:sum/size();
	}

	public float last() {
		return lastValue;
	}


	public int scaledAverage() {
		float f = average();
		return scaled(f);
	}

	public float min() {
		return min;
	}

	public float max() {
		return max;
	}


	//0...(max-1)
	public void fillRandom(int max) {
		for (int i = 0; i < data.length; i++) {
			addRandom(max);
		}
	}

	//Scales amplitude y to fit integer 0.HEIGHT -1
	//given min and max values.
	public int scaledAmp(int i) {
		if (i > data.length -1) {
			System.out.println("Warning: Buffer.scaledAmp");
			return (int) NO_VALUE;
		}

		float y = data[i];
		return scaled(y);
	}

	public int xAxis() {
		int i = scaled(0f);
		return i;
	}


	public int scaled(float y) {
		if (y < min || y > max) {
			return -1;	//outside range
		}

		y -= min;

		float delta = max-min;
		if (delta == 0) { //no variation in signal history
			return (HEIGHT-1)/2;

		}

		float active = (HEIGHT-1)*FILLED_RATIO;
		float air = (HEIGHT-1)*(1-FILLED_RATIO)/2f;

		float amp = active*y/delta;
		int i = (int) (0.5+ amp+ air);
		return i;

//		float amp = (HEIGHT-1)*y/delta;
//		return (int) (0.5 + amp);
	}

	public static void main(String[] fs) {
		/*
		Buffer s = new Buffer(50,100);
		s.fillRandom(10);
		for(float i = 0f; i < 30f; i++) {

			s.add(-i);
		}
		s.show();
		*/
		steps(1f,1.2f,5);
	}

	public static void steps(float min, float max, int steps) {

		System.out.println("PRODUCING " + (steps +1)
			+ "step(s) between " + min + " and" + max);

		float step = (max-min)/steps;

		for(float f = min; f <= max; f+= step) {
			System.out.println("y = " + f);
		}
	}
}