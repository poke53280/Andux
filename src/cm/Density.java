
package cm;
import root.CycleVector;
import root.Node;
import io.ProxyManager;
import area.Area;
import root.Command;
import root.Factory;
import root.SpaceMax;

/*
	Find the density of nodes in the world
*/


public class Density {

	private CycleVector v;

	public Density(CycleVector v) {
		this.v = v;
	}

	public void registerCommands(Factory f, String prefix) {
		if (f == null) return;
		f.add(prefix + "density", new Status() );
	}

	//Nodes per Mpixel
	public float numberPerArea() {

		if (v == null || v.isEmpty() ) 	return 0f;
		float world = SpaceMax.H* SpaceMax.H*4f;
		System.out.println("Density: worldpixels=" + world);
		System.out.println("Density: #nodes= " + v.size() );
		float d = v.size()*1024f*1024f/world;
		return d;
	}

	public float getCoverage() {
		int area = getPixels();
		float world = SpaceMax.H* SpaceMax.H*4f;
		float f = 100f*area/world;
		return f;

	}

	private int getPixels() {

		if (v == null || v.isEmpty() ) {
			return 0;
		}

		int sumArea = 0;

		for(int i = 0; i < v.size(); i++) {
			Node n = (Node) v.elementAt(i);
			ProxyManager p = n.getProxyManager();
			Area a = p.getLocalArea();
			int pixelArea = a.pixelArea();
			sumArea += pixelArea;
		}

		return sumArea;
	}


	//To keep #nodes/Mpixel at d, a square given
	//here is of appropriate size
	//Returns half the size (extension from origo)

	//Returns 0 if the world is empty of known nodes.

	public float sizeAtDensity(float d) {

		if (v == null || v.isEmpty() ) {
			return 0;
		}

		float w = 512f * (float) Math.sqrt(v.size()/d);
		return w;
	}


	//Calculate density if there were extra more nodes
	//in the system
	public float sizeAtDensity(float d, int extra) {

		if (extra < 0)
			throw new IllegalArgumentException("extra < 0");

		int inSystem = 0;
		if (v != null) inSystem = v.size();

		int nodes = extra + inSystem;

		float w = 512f * (float) Math.sqrt(nodes/d);
		return w;


	}



	class Status extends Command {

			public Status() {
				setUsage("status");
			}

			public Command create(String[] args) {
				Command c = new Status();
				c.setArgs(args);
				return c;
			}

			public void init() {
				super.init();
			}

			public void execute() {
				if (isValid ) {
					float cover = getCoverage();
					float number = numberPerArea();

					float presetDensity = 0.5f; //nodes pr.mega pixels

					float sizeAtDensity = sizeAtDensity(presetDensity);

					StringBuffer b = new StringBuffer(150);
					b.append("world size = " + SpaceMax.H + ", "  + SpaceMax.H);
					b.append("coverage% = "	+ getCoverage() );
					b.append("\n#pr.Mpixel = "	+ number);
					b.append("\nSpaceMax.W,H=" + sizeAtDensity + ", @density=" + presetDensity);

					setResult(b.toString() );

				} else {
					setResult(showUsage() );
				}
			}

			public void setArgs(String[] args) {
				init();
				isValid = true;
			}
	}

}