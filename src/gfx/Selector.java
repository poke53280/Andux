
package gfx;

import root.CycleVector;

class Selector {

	CycleVector apps;
	Listener l;
	Screen screen;

	int mx = 0;
	int my = 0;	//last mouse click pos

	AppComponent selected = null;


	Selector(Listener l, CycleVector apps, Screen screen) {
		this.apps = apps;
		this.l = l;
		this.screen = screen;
	}

	//Returns the component, if any, set as selected during last run of focus.
	public AppComponent selected() { return selected; }

	public void focus() {

		if (apps.isEmpty() ) {
			//non to select
			//ensure none selected and return
			selected = null;
			return;
		}

		//Last clicked spot on screen
		int x = l.mx();
		int y = l.my();

		if (x != mx || y != my) {
			//last click is at new location
			mx = x;
			my = y;
			//System.out.println("Click at new location: (" + x + "," + y + ")");

			//If we are assigned, don't change if assigned (still) intersects.
			if(selected != null) {
				ScreenLocation sl = selected.screenLoc();
				if (sl.intersects(x,y)) {
					//System.out.println("No change: " + selected.name() );
					return;
				}
			}
			//Selected (if any) not intersecting, we deselect right away
			if (selected != null) {
				App a = selected.getApp();
				//System.out.println("SET INACTIVE: "+ selected.name());
				a.inActive(screen);
				selected = null;
			}

			//Now, we don't have any selected component. We know that
			//at least one component exists. Select any that intersects
			//the coordinates.
			select(x,y);
		}
	}

	//Policy: Select NONE if two or more are intersecting mouse click location
	private void select(int x, int y) {
		if (selected != null) throw new IllegalStateException("Selected != null");

		for(int i = 0; i < apps.size(); i++) {
			AppComponent ac = (AppComponent) apps.elementAt(i);
			ScreenLocation sl = ac.screenLoc();
			if (sl.intersects(x,y) ) {
				//Check if we have found one already
				if (selected != null) {
					//System.out.println("Several->none selected");
					selected = null;
					return;
				}
				selected = ac;
				//System.out.println("Found new selection: " + selected.name() );
			}
		}
		if (selected != null) {
			//System.out.println("SET ACTIVE:   " + selected.name() );
			App a = selected.getApp();
			a.active(screen);
			l.resetKeyCounters();

			SpaceLocation _sl = selected.spaceLoc();
			l.setAutoRotate(_sl.autoRotate() );
			//l.setAutoRotate(selected.

			//l.resetDrag();
		} else {
			//System.out.println("None found");
		}
	}

}