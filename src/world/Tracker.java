package world;

import java.util.Observer;
import java.util.Observable;

public abstract class Tracker implements Observer {

	protected Engine e;
	protected static final int TRACK_X = 200;
	protected static final int TRACK_Y = 200;

	protected Item local = null;
	protected Item target = null;

	public Tracker(Item i, Engine e) {
		if (i == null) throw new IllegalArgumentException("Got null item");
		if (e == null) throw new IllegalArgumentException("Got null engine");
		this.e = e;
		this.local = i;

	}

	public void update(Observable obs, Object obj) {

		//Target sometimes is null, should use assignedItem for SingleTargetTracker?

		if (obs == target) {
			//System.out.println("Tracker for item "
			//	+ local.ID + " updated about losing my target:" + target.ID);
			clearTarget();

		} else if (target == null) {
			System.out.println("Tracker: My target is null");
		} else {
			System.out.println("Tracker: Got odd update:" + obs.toString() );
		}

	}

	protected abstract void clearTarget();

	public void detach() {
		clearTarget();
	}


	public abstract Tracker cloneFor(Item i);

	public final Item getTarget() {
		return target;
	}

	public final boolean hasTarget() {
		return target != null;
	}

	public abstract void scan();
	public abstract String status();


	public int getData() {
		return -1;
	}

	public void setData(int d) {
		//
	}

}