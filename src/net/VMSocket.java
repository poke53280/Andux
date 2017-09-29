
package net;

import root.Factory;
import root.Queue;

class VMSocket implements MessageIO {

	private long sid;
	private Queue q;
	private Hub h;


	public VMSocket(Queue q, long sid, Hub h) {
		this.sid = sid;
		this.q = q;
		this.h = h;
	}


	public void registerCommands(String prefix, Factory f) { }

	public void send(NetMessage m) {
			h.send(sid, m);
	}

	public NetMessage poll() {
		return (NetMessage) q.poll();
	}

	public long getLocalSID() { return sid; }

	public void end() { }

	public void close() { }

	public long getPrevious() { return sid -1; }



}