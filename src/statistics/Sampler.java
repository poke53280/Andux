
package statistics;

public class Sampler {

	/*
		Reads data from a Provider, adds to Buffer
	*/
	private final SampleProvider p;
	private final Buffer b;

	private int count = 0;


	public Sampler(SampleProvider p, int length, String name) {
		this.p = p;
		this.b = new Buffer(length, 100, name);
	}

	public void sample() {
		float value = p.sampleValue();
		b.add(value);
	}

	public void sample(int mod) {
		if(++count%mod == 0) sample();
	}


	public Buffer getBuffer() {
		return b;
	}

}