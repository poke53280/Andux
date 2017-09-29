package world;

import java.awt.Point;
import java.util.Observer;
import java.util.Observable;


/*
	INIT
		- Goes to SEARCH.
		  When no assigned item, LOCK is never achieved.

	SEARCH
		- Looks for assigned item in focus. If found,
		  then LOCK.

	LOCK:
		- Verifies that assigned item  within TRACK_X, TRACK_Y range.
          If outside, then release as target, and go to SEARCH.
*/


public class SingleTargetTracker extends Tracker {

	protected Item assignedItem = null;

	public SingleTargetTracker(Item i, Engine e, Item a) {
		super(i,e);

		if (a == null) {
			//System.out.println("SingleTargetTracker: Assiged null");
		}
		assign(a);
	}

	public void setData(int d) {
		//System.out.println("SingleTargetTracker.setData=" + d);
		assign(d);
	}

	public int getData() {
		if (assignedItem == null) {
			//System.out.println("SingleTargetTracker.getData= (none)/-1");
			return -1;
		} else {
			int id = assignedItem.ID;
			//System.out.println("SingleTargetTracker.getData=" + id);
			return id;
		}

	}

	public void assign(int id) {
		if (id == -1) {
			//
		} else {
			Item a = e.getItem(id);
			if (a == null) {
				//System.out.println("assign - item not found; " + id);
			} else {
				//System.out.println("assign - item found " + id);
			}
			assign(a);
		}
	}


	public void assign(Item a) {

		//Drop out on identical existing item.
		if (a != null && assignedItem == a) {
			System.out.println("Assign: Reassigned,id = " + a.ID);
			return;
		}

		//Deregister
		if (assignedItem != null) {
			//System.out.println("Assign:Dropping observation of " + assignedItem.ID);
			assignedItem.deleteObserver(this);
			assignedItem = null;
		}

		//Assign
		assignedItem = a;

		//Register
		if (assignedItem != null) {
			assignedItem.addObserver(this);
			//System.out.println("Assign:Starting observation of " + assignedItem.ID);
		}

		target = null;

	}


	public void update(Observable obs, Object obj) {

		if (obs == assignedItem) {
			//System.out.println("SingleTargetTracker for item "
			//	+ local.ID + " updated about losing my target:" + assignedItem.ID);
			clearTarget();

		} else if (assignedItem == null) {
			System.out.println("SingleTargetTracker: My assigned target is null");
		} else {
			System.out.println("SingleTargetTracker: Got odd update:" + obs.toString() );
		}

	}




	public void clearTarget() {
		//System.out.println("SingleTargetTracker.clearTarget ");
		assign(null);
	}


	public void scan() {

		if (assignedItem == null) {
				target = null;
				return;
		}

		Point p = assignedItem.getDiff(local);

		if (p.x >= TRACK_X || p.y >= TRACK_Y) {
			target = null;
		} else {
			target = assignedItem;
		}

	}


	public String status() {
		if (hasTarget() ) 	  {
			return "S: LOCK:" + target.ID;
		}	else  if (assignedItem != null) {
			return "S:SRCH:" + assignedItem.ID;
		}	else {
			return "S:SRCH:N/A";
		}

	}

	public Tracker cloneFor(Item i) {
		//XXXWhat about the assignedItem?
		//Just set to null here.
		System.out.println("SingleTargetTracker.cloneFor - this is bogus!");
		Tracker t = new SingleTargetTracker(i,e, null);
		t.target = target;
		return t;
	}



}