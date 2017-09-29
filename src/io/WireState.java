package io;


public class WireState {

	private TimeOut receiveTimer;

	private boolean isSending = true;
	private boolean isReceiving = true;

	private long numberOfSent = 0;
	private long numberOfReceived = 0;

	private long lastSent = 0;

	private int outCountMax = 0;
	private int outCountNow = 0;

	private long constructTime;

	public WireState(long timeout) {
		constructTime = System.currentTimeMillis();
		receiveTimer = new TimeOut(timeout);

	}


	public void registerSend() {
		lastSent = System.currentTimeMillis();
		numberOfSent++;
	}

	public void registerReceive() {
		numberOfReceived++;
		receiveTimer.set();

	}

	public void registerLength(int l) {
		outCountNow = l;
		if (outCountNow > outCountMax) {
			outCountMax = outCountNow;
		}

	}


	public boolean isSending() {
		return isSending;
	}

	public boolean isReceiving() {
		return isReceiving;
	}

	public void doSend(boolean isSending) {
		this.isSending = isSending;
	}

	public void doReceive(boolean isReceiving) {
		this.isReceiving = isReceiving;
	}

	public String state() {
		String out = "";
		if (remoteIsDead() ) {
			out = "[ SILENT ] ";
		} else {
			out = "[INCOMING] ";
		}

		String s1 = " Stot" + numberOfSent + " Slast" + outCountNow
			+ " Smax" + outCountMax +" R" + numberOfReceived;
		return out + " | " + s1;
	}

	public long getReceived() {
		return numberOfReceived;
	}

	public long getSent() {
		return numberOfSent;
	}

	public boolean remoteIsDead() {
		return receiveTimer.hasTimedOut();
	}


}