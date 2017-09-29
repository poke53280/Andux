
package root;


public class ClusterConfig {

	public int period = 0;

	public boolean cm = true;
	public boolean autoEval = false;


	public int n = 0;
	//public int speed = 300;
	public int speed = 20;	//10*pixels/tick
	public boolean contactAll = false;

	//public int agentFreq = 13;
	public int agentFreq = 3;

	public float density = .5f;		//nodes pr. Mpixel
	//public float density = .1f;		//nodes pr. Mpixel

	public String state() {
		StringBuffer b = new StringBuffer(300);
		b.append("\n#T = " + period);
		b.append("\n#cm  " + cm);
		b.append("\n#autoeval " + autoEval);
		b.append("\n#n init   " + n);
		b.append("\n#speed    " + speed);
		b.append("\n#agent run" + agentFreq);
		b.append("\n#contact all " + contactAll);
		b.append("\n#density " + density);


		return b.toString();
	}


}