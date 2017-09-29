
package agent;

import net.IPString;

public class Stay {

	private final long localhome;

	private long home = -1L;
	private int id = -1;
	private int nr = -1;
	private long target = -1L;
	private int type = -1;	//not a valid type
	private State state = null;

	Stay(long localhome) {
		if (localhome == -1L)
			throw new IllegalArgumentException("bad localhome");
		this.localhome = localhome;
	}

	void register(Agent a, State s) {
		if(s == null)
			throw new IllegalStateException("bad state");


		this.home = a.getHomeKey();
		this.id = a.getID();
		this.nr = a.hops() + 1;

		this.target = a.destination();

		this.type = a.getType();
		this.state = s;

		if (!Type.isValid(type))
			throw new IllegalStateException("bad type=" + type);

		if (a.verbose()) {
			System.out.println("Registered guest: " + show() );
		}

	}

	public String show() {
		if (id == -1) return "UNUSED";

		String d = null;
		if (target == -1L) {
			d = "NONE";
	 	} else {
			d = IPString.getAddressString(target);
		}

		String h = null;
		if (home == localhome) {
			h = "LOCAL         ";
		} else {
			h = IPString.getAddressString(home);
		}

		return h + "  " + Type.desc(type) + "  " + id
				+ "  " + nr + "  " + d + "  " + state.desc();
	}


	public String overview() {
		if (id == -1) return "UNUSED";

		if (state == State.BACK0 || state == State.BACK1) {
			return Type.abbrev(type) + " " + state.desc() + " " + nr;
		} else {
			return Type.abbrev(type) + " " + state.desc();
		}
	}


	public State state() {
		return state;
	}

	public long target() {
		return target;
	}


	boolean search(long s, int i, int n) {
		return s == home && i == id && n == nr;
	}

}