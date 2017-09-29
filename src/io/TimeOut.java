package io;

public class TimeOut {

	private long timeout;
	private long constructTime;
	private long lastTime = 0;

	public TimeOut(long timeout) {
		constructTime = System.currentTimeMillis();
		this.timeout = timeout;

	}

	public void set() {
		lastTime = System.currentTimeMillis();

	}


	public boolean hasTimedOut() {
		long now = System.currentTimeMillis();

		if (now - constructTime < timeout) {
			return false;
		} else if (now - lastTime < timeout) {
			return false;
		} else {
			return true;
		}

	}


}