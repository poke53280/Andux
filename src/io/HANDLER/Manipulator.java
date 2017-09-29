package io.handler;


public abstract class Manipulator {

	protected Message m;

	public Manipulator() {
		m = null;
	}

	public abstract void create();
	public abstract void setMessage(Message m);


	public final void forget() {
			m = null;
	}

	public final void release() {
		if (m != null) {
			Message.freeInstance(m);
			m = null;
		} else {
			throw new IllegalStateException("Manipulator.release:"
											+ " No message to release");
		}
	}

	public final Message getMessage() {
		return m;
	}

}