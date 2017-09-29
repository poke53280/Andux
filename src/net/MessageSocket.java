
package net;

import root.Command;
import root.Factory;
import root.ParseUtil;

import java.io.IOException;
import java.util.Random;

import dispatch.PackMan;	//For round-testing only.


public class MessageSocket extends MessageTransmitter implements MessageIO {


	protected BlockReceiver current  = null;

	public MessageSocket(int port, String hostname, int to)
														throws IOException {
		super(port, hostname, to);

		//if (type == BLOCK) {
			current = new BlockReceiver(to);
		//} else {
		//	throw new IllegalArgumentException("Bad type:" + type);
		//}
		current.launch();
	}

	public long getPrevious() {
		long sid = -1L;
		try {
			sid = IPString.getSID("127.0.0.1", getLocalPort() -1);
		} catch (IllegalArgumentException e) {
			System.out.println("MessageSocket.getPrevious failed");
			System.exit(1);
		}

		return sid;
	}

	public void registerCommands(String prefix, Factory f) {
		if (f == null) return;

		f.add(prefix + "netstat", new Status() );
		super.registerCommands(prefix, f);
	}

	public void end() {
		current.end();
	}

	public NetMessage poll() {
		return current.poll();
	}

	protected String receiverDesc() {
		return current.desc();
	}

	class Status extends Command {

		public Status() {
			setUsage("netstatus");
		}

		public Command create(String[] args) {
			Command c = new Status();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
				setResult(transferRate() + "\n[cache: "
						+ cacheStatus() + "] "
						+ receiverDesc() );
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

	}

/*
	private abstract class Receiver {



		public Receiver(int to) {
			this.to = to;
		}

		public abstract NetMessage poll();
		public abstract String desc();
		public abstract void launch();
		public abstract void end();
		public abstract int getType();
	}
*/

	private class BlockReceiver  {

		private final int to;

		public BlockReceiver(int to) {
			this.to = to;
		}

		public NetMessage poll() {
			return receive();	//Blocks for time-out msecs. May return null.
		}

		public String desc() { return "[BLOCK] timeout=" + to + " ms"; }

		public void launch() { ; }
		public void end() { ; }
	}

}
