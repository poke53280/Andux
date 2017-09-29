package io.handler;

public interface PortListener {
	public void add(int port, InHandler h);
	public void remove(int port);

}


