
package world;

import display.RenderImage;
import java.awt.Point;


/*
	INIT
		- Locks on given target, if any. Goes to state LOCK,
		  otherwise SEARCH.

	SEARCH
		- Looks for any target in focus.

	LOCK:
		- Maintains same target as long as item is
		  within TRACK_X, TRACK_Y range.
*/




public class CloseTracker extends Tracker {

	protected Focus f;
	protected RenderImage[] buffer =
				RenderImage.createBuffer(5);		//was:2

	public CloseTracker(Item i, Engine e) {
		super(i,e);

		this.f = e.createItemFocus(i.ID);
		f.getOffset(); 	//Initate it
		if (f == null) throw new IllegalStateException("No item found");

	}

	public String status() {
		if (hasTarget() ) 	  { 	return "C: LOCK:" + target.ID;	}
								else {		return "C:SRCH";					}

	}


	public Tracker cloneFor(Item i) {
		Tracker t = new CloseTracker(i,e);

		t.target = target;

		if (hasTarget() ) {

			//XXX ADD OBSERVER FOR THE NEW TRACKER

			//System.out.println("Tracker.clone: target transfered");
		} else {
			//System.out.println("Tracker.clone: no target transfered");
		}

		return t;
	}


	public void scan() {
		if (target == null) {
			acquire();
		} else {
			verify();
			if (target == null) {
				acquire();
			}

		}

	}

	public void clearTarget() {
		//System.out.println("CloseTracker.clearTarget");

		if (target != null)  target.deleteObserver(this);
		target = null;


	}



	protected void acquire() {
		Item i = null;
		int count = e.getA(buffer,f);
		if (count > 1) {

			for (int j = 0; j <count; j++) {
				if (buffer[j].id != local.ID && !buffer[j].isMissile) {
					i = e.getItem(buffer[j].id);		//Item other than self and not a missile
					break;
				}
			}
/*
			if (buffer[0].id == local.ID) {
				i = e.getItem(buffer[1].id );
			} else {
				i = e.getItem(buffer[0].id );
			}
*/
			if (i == null) {
				//throw new IllegalStateException("acquire failed");
				//System.out.println("Neighbours(" + (count -1)
				//													+ "), but missiles only");
				return;
			}


			target = i;
			target.addObserver(this);
		}
	}


	protected void verify() {
		//recheck: xxx - if item repooled, target item may be gone?
		if (target == null) throw new IllegalStateException("No target");

		Point p = target.getDiff(local);
		if (p.x >= TRACK_X || p.y >= TRACK_Y) {
			//System.out.println("Tracker:Target lost");
			target.deleteObserver(this);
			target = null;
		}

	}


}