
package net;

import root.WorldSize;
import java.io.IOException;
import root.Factory;
import root.Command;
import statistics.SampleProvider;
import java.net.UnknownHostException;

public class NetConnector extends Connector {

	private long initSid = -1L;

	//"a.b.c.d:port"
	public NetConnector(String s) throws UnknownHostException {
		try {
			initSid =  IPString.getLong(s);
		} catch (IllegalArgumentException e) {
			System.out.println("Couldn't parse local and/or remote address");
			throw new UnknownHostException("could not find host");
		}
	}

	public NetConnector(long initSid) {
		this.initSid = initSid;
	}

	public void registerCommands(Factory f, String prefix) {
		f.add(prefix + "status", new Status() );
	}

	public MessageIO scan() throws Exception {
		MessageIO net = scan(initSid);

		if (net == null) throw new Exception("Couldn't connect");
		return net;
	}

	private MessageIO scan(long sid) {

		int max = WorldSize.PORT_COUNT;
		int count = 0;

		MessageIO net = null;
		int port = IPString.getPort(sid);

		do {
			net = connect(port);
			port++;
			count++;
		} while (net == null && (count < max));

		return net;
	}

	public MessageIO connect() throws Exception {
		MessageIO net = connect(IPString.getPort(initSid) );
		if (net == null) throw new Exception("Couldn't connect");
		return net;
	}

	private MessageIO connect(int port) {

		String hostname = null;
		MessageIO net = null;
		try {
			net = new MessageSocket(port, hostname, WorldSize.TIMEOUT);
		} catch (IOException ie) {
			net = null;
		}
		return net;
	}

	public SampleProvider getSampleProvider() {
		return null;
	}


	class Status extends Command {

		public Status() {
			setUsage("status");
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
			setResult("NetConnector.Status responding");
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}

}