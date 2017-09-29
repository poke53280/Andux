
package area;


import root.SpaceMax;
import root.Factory;
import root.Command;

/*
	Desired warps - up to date
						on construction.
						when update()ed.
*/

/*
	Acquire and keep N_WARPS warp areas that really are
	in warp positions compared to local area.
*/

/*
	The warp system may be disabled
*/


public class WarpDesire {
	private AreaProvider ap;
	private float atSize;

	private WarpArea[] warps = null;


	private final float DESIRE_RATIO = .3f;

	private int N_WARPS = 0;	//number of desired warps kept

	public WarpDesire(AreaProvider ap) {
		this.ap = ap;
		init();
	}

	private void init() {
		//Rebuild links from scratch at init or
		//at full reset (world size changed)


		//System.out.println("WD:Init:Full reset of desired warps");

		if (warps != null) throw new IllegalStateException("warps not null");

		atSize = SpaceMax.H;
		if (SpaceMax.levels() == 0) {
			//System.out.println("WD:Init: no warps");
			return;
		}

		Area local = ap.getArea();
		SpaceMax.recalc(local);

		int allwarps = 3*SpaceMax.levels();

		N_WARPS = (int) (.5 + allwarps*DESIRE_RATIO);

		if (N_WARPS <= 0) {
			//System.out.println("N_WARPS = 0 not handled, setting to 1");
			N_WARPS = 1;

		}


		//System.out.println("init: allwarps=" + allwarps + " -> n=" + N_WARPS);
		//if (N_WARPS > allwarps)...


		warps = new WarpArea[N_WARPS];


		for(int i = 0; i < N_WARPS; i++) {
			WarpArea wa = SpaceMax.nextWarp();
			warps[i] = new WarpArea(wa.getX(),wa.getY(), wa.getH());
		}
		//System.out.println("WD:Init: created full set of warps");

	}

	public WarpArea[] getDesiredWarps() {
		return warps;
	}

	/*
		- before launching a new warp agent
		- before answering to list command
		- at update command
		- every 'some' ticks.

	*/


	public void update() {

		float currentSize = SpaceMax.H;

		if (currentSize != atSize) {

			warps = null;			//Get rid of old system.
			atSize = currentSize;
			init();

			return;

		}

		if (warps == null) return;


		//warps are running.

		Area local = ap.getArea();
		SpaceMax.recalc(local);			//recalced and shuffled


		int badCount = 0;
		for(int i = 0; i < N_WARPS; i++) {
			WarpArea wa = warps[i];

			//Check if this area is in the warps of SpaceMax.
			boolean isWarp = SpaceMax.isWarp(wa);

			if(!isWarp) {
				badCount++;
				//System.out.println("bad dw found: " + wa.state() );

				//replace with a good and unknown warp immediately

				WarpArea _wa = getUnknownWarp(i);

				wa.set(_wa.getX(), _wa.getY(), _wa.getH());

				//System.out.println("...replaced with: " + wa.state() );
			}
		}
		if (badCount == 0) {
			//System.out.println("all warps ok");
		} else {
			//System.out.println("replaced " + badCount + " dwarp(s)");
		}
		shuffle();
	}

	//randomize order in the warps array
	private void shuffle() {
		if (warps == null) return;
		WarpArea tmp;

		int maxIndex = N_WARPS-1;

		for(int i = 0; i < maxIndex; i++) {
			int index = i + (int) (.5 + Math.random()*(maxIndex-i));
			if (i != index) {
				tmp = warps[i];
				warps[i] = warps[index];
				warps[index] = tmp;
			}
		}

	}


	private WarpArea getUnknownWarp(int target) {
		if (target < 0 || target >= N_WARPS) throw new IllegalArgumentException("target="+target);

		//System.out.println("WM.getUnknownWarp called, i = " + target);


		WarpArea wa = null;


		do {

			if (SpaceMax.shuffleEmpty() ) {
				throw new
					IllegalStateException("out of random warps - should always be enough");
			}

			wa = SpaceMax.nextWarp();


		} while (!isUnknown(target,wa));


		return wa;


	}

	//Checks if WarpArea wa is represented('equals') in the warps array.
	private boolean isUnknown(int target, WarpArea wa) {
		for(int i = 0; i < N_WARPS; i++) {
			WarpArea _wa = warps[i];
			if (wa.equals(_wa)) return false;	//found, thus known.
		}
		return true;

	}


	private String list() {
		if (warps == null) return "no dwarps found";

		StringBuffer b = new StringBuffer(N_WARPS*10);
		for(int i = 0; i < N_WARPS; i++) {
			WarpArea wa = warps[i];
			b.append('\n');
			b.append(wa.state());
		}
		return b.toString();
	}



	public void registerCommands(String prefix, Factory f) {
		if (f == null) return;
		f.add(prefix + "list", new WList() );
		f.add(prefix + "update", new Update() );
	}


	class Update extends Command {


		public Update() {
			setUsage("update");
		}

		public Command create(String[] args) {
			Command c = new Update();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			update();
			setResult("dwarps updated");

		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

	}


	class WList extends Command {

		public WList() {
			setUsage("wlist");
		}

		public Command create(String[] args) {
			Command c = new WList();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			update();
			String s = list();
			setResult(s);
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}

}