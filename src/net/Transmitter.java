package net;

import java.net.DatagramPacket;


public abstract class Transmitter  {


    public static final int MAX_RECEIVE_SIZE = 800;

    public abstract SocketAddress getLocalSocket();


	public abstract boolean send(DatagramPacket p);
	public abstract boolean receive(DatagramPacket p);


    public abstract String transferRate();

	public abstract void resetCount();

    public abstract void close();

}

