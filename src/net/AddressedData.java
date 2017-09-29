package net;

import io.handler.Message;


public class AddressedData {

	private Message m = null;
    private SocketAddress socketAddress = null;

	public AddressedData(SocketAddress a, Message m) {
	        socketAddress = a;
			this.m = m;
    }

    public SocketAddress getAddress() {
        return socketAddress;
    }

	public Message getMessage() {
		return m;
	}


}
