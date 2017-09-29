package world;


public class ItemState {

	public static final int OWN = 1;
	public static final int SPEC = 2;
	public static final int TRANSFER = 3;
	public static final int DROP = 4;
	public static final int NONE = 5;			//If instance unused.

  	public int state;

	public ItemState() {
		this.state = NONE;
	}


	public void init(int state) throws IllegalArgumentException {
		if (this.state != NONE) {
			throw new IllegalStateException("Cannot init an item in state " + desc() );
		}

		if (state == OWN || state == SPEC) {
			this.state = state;
		} else {
			this.state = state;
			throw new IllegalArgumentException("Illegal init state" + desc() );
		}

	}

	public void destroy() {
		state = NONE;
	}


	public void setInside() {
		if (state == OWN || state == TRANSFER) {
			state = OWN;
		}

		if (state == SPEC || state == DROP) {
			state = SPEC;
		}

	}


	public void setOutside() {
		if (state == OWN || state == TRANSFER) {
			state = TRANSFER;
		}

		if (state == SPEC || state == DROP) {
			state = DROP;
		}

	}


	public int getState() {
		return state;
	}

	public void take() {
		if (state == SPEC) {
			state = OWN;
		} else {
			throw new IllegalStateException("ItemState.takeControl():"
									+ " Cannot change " + desc() + " to OWN");
		}

	}


	public void release() {
		if (state == TRANSFER) {
			state = SPEC;
		} else {
			throw new IllegalStateException("ItemState.releaseControl():"
									+ " Cannot change " + desc() + " to SPEC");
		}

	}


	public boolean isControlled() {
		return state == OWN || state == TRANSFER;

	}

	public boolean inState(int s) {
		return s == state;
	}


	public static boolean isValid(int state) {
		return (state==OWN || state == SPEC
							|| state == TRANSFER || state == DROP);
	}

	public String desc() {
			String desc = null;
			switch(state) {
				case OWN:
						desc = "OWN";
						break;
				case SPEC:
						desc = "SPEC";
						break;
				case TRANSFER:
						desc = "TRANSFER";
						break;
			   case DROP:
			   			desc = "DROP";
						break;
				case NONE:
						desc = "WARN: NONE";
						break;
				default:
						throw new
							IllegalStateException("Itemstate="
										+ state);
			}
			return desc;

	}

}
