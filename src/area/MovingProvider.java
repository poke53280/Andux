
package area;

import root.SpaceMax;
import root.Command;
import root.Factory;
import root.ParseUtil;



public class MovingProvider implements AreaProvider {

	private float vx;
	private float vy;

	private float x;
	private float y;


	private float step = 1f;

	private Area a = null;

	private float max;

	public MovingProvider(float max) {

		a = new Area();

		x = SpaceMax.any();
		y = SpaceMax.any();

		if (max < 0f)
			throw new IllegalArgumentException("max speed <0:" + max);


		if (max == 0f) {	//ensure 0 velocity
			vx = 0f;
			vy = 0f;
		} else {
			float fx = 2f * max * (float) Math.random();
			float fy = 2f * max * (float) Math.random();
			vx = 0.1f*(fx - max);
			vy = 0.1f*(fy - max);
		}
		this.max = max;
		tick();
	}


	public void east() {
		vx += step;
		if (vx > max) vx = max;
	}

	public void west() {
		vx -= step;
		if (vx < -max) vx = -max;
	}

	public void north() {
		vy -= step;
		if (vy < -max) vy = -max;
	}

	public void south() {
		vy += step;
		if (vy > max) vy = max;
	}



	public void registerCommands(String prefix, Factory f) {
		f.add(prefix + "loc", new Loc() );
	}


	public Area getArea() { return a; }

	public void shuffle() {
		//Move to any place

		x = SpaceMax.any();
		y = SpaceMax.any();
		tick();
	}


	public void tick() {

		if (vx != 0f || vy != 0f) {

			x += vx;
			y += vy;

			if (x < -SpaceMax.H) {
				x = -SpaceMax.H;
				vx = -vx;
			}

			if (x > SpaceMax.H) {
				x = SpaceMax.H;
				vx = -vx;
			}

			if (y < -SpaceMax.H) {
				y = -SpaceMax.H;
				vy = -vy;
			}

			if (y > SpaceMax.H) {
				y = SpaceMax.H;
				vy = -vy;
			}
		}

		a.move(x,y);
	}

	class Loc extends Command {

		int _x = -1;
		int _y = -1;
		boolean hasArgs = false;

		public Loc() {
			setUsage("loc x y");
		}

		public Command create(String[] args) {
			Command c = new Loc();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			_x = -1;
			_y = -1;
		}

		public void execute() {
			if(hasArgs) {
				System.out.println("moving node to ("
									+ _x + "," + _y + ")");

				x = (float) _x;
				y = (float) _y;
				a.move(x,y);
			}
			setResult(a.status() );

		}

		public void setArgs(String[] args) {
			init();
			if (args.length == PAR2 +1) {
				_x = ParseUtil.parseInt(args[PAR1]);
				_y = ParseUtil.parseInt(args[PAR2]);
				if (_x == -1 || _y == -1) {
					hasArgs = false;
				} else {
					hasArgs = true;
				}
			} else {
				hasArgs = false;
			}
			isValid = true;
		}

	}

}