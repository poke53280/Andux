
package gfx;


//Applies the keyboard counters movement to the
//component given and resets the counters.
class Mover {

	Listener l;
	float step;

	Mover(Listener l, float step) {
		this.l = l;
		this.step = step;
	}

	void move(SpaceLocation sl) {
		if (l.allZero() ) return;

		if (l.resetRequested() ) {
			//System.out.println("Mover: Reset requested");
			sl.unit();
		} else {
			float dx = (float) (l.kx() * step + l.dx());
			float dy = (float) (l.ky() * step - l.dy());
			float dz = (float) (l.kz() * step);

			sl.move(0f,0f,dz);
			sl.screenMove(dx,dy);
		}

		l.reset();

	}


	void move(AppComponent ac) {
		if (l.allZero() ) return;

		SpaceLocation sl = ac.spaceLoc();
		move(sl);

	}

}