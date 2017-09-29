
package net;

import root.Queue;
import root.WorldSize;
import root.Factory;
import root.Command;
import statistics.SampleProvider;
public class VMConnector extends Connector {

	private Hub hub;
	private long initSid = -1L;

	public VMConnector(long initSid) {
		//setInitSid(initSid);
		this.initSid = initSid;
		hub = new Hub();
	}


	public void registerCommands(Factory f, String prefix) {
		f.add(prefix + "status", new Status() );
		hub.registerCommands(f,prefix);
	}

	public SampleProvider getHub() {
		return hub;
	}

	public void setTick(long c) {
		hub.setTick(c);
	}

	public MessageIO scan() throws Exception {
		MessageIO net = scan(initSid);

		if (net == null) throw new Exception("Couldn't connect");
		return net;

	}

	public MessageIO connect() throws Exception {

		throw new Exception("not implemented");
	}


	private MessageIO scan(long sid) {

		int max = 100000;

		int count = 0;

		Queue q = null;
		do {
			q = hub.createQueue(sid);
			sid++;
			count++;
		} while (q == null && (count < max) );

		if (q == null) return null; //Failed

		return new VMSocket(q,sid-1,hub);


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
			setResult("VMConnector.Status responding");
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

	}


	public static void main(String[] args) {

		Connector c = new VMConnector(50);
		try {
			MessageIO m1 = c.scan();

			System.out.println("connected to:" + m1.getLocalSID() );
			System.out.println("previous sid:" + m1.getPrevious() );


		} catch (Exception e) {
			System.out.println("Couldnt get MessageIO");
		}



	}

}