
package dispatch;

import root.Command;
import root.Factory;
import root.ParseUtil;
import java.text.DecimalFormat;
import net.NetMessage;

import root.WorldSize;


public class Dispatcher  { //implements Counter {

	protected final int MAX_PORT = Port.MAX_PORT;
	protected InHandler[] handler = new InHandler[MAX_PORT];


	private int[] count;
	private int total;
	private int userCount;
	private int sysCount;

	private int fullTotal = 0;	//Not to be reset
	private int msgLength;

	private int notHandled = 0;
	private int handled = 0;


	private DecimalFormat percentFormatter = new DecimalFormat();

	private UnPacker unpacker = new UnPacker();

	private String prefix;
	private Factory f;
	private int RESET_COUNT = 500;	//Number of messages


	public Dispatcher(String prefix, Factory f) {

		for (int i=0;i<MAX_PORT;i++) {
			handler[i] = null;
		}

		count = new int[MAX_PORT];

		resetCount();
		percentFormatter.applyPattern("##0.0");

		this.prefix = prefix;
		this.f = f;
	}

	protected InHandler get(int port) {
		if (port >= 0 && port < MAX_PORT) {
			return handler[port];	//may still be null
		} else {
			System.out.println("Dispatcher.get: "
				+ "internal port out of range: " + port);
			return null;
		}
	}

	public void add(int port, InHandler h) {
		if (port >= 0 && port < MAX_PORT) {
			handler[port] = h;
		} else {
			System.out.println("Dispatcher.add: "
				+ "internal port out of range: " + port);
		}
	}

	public void remove(int port) {
		if (port >= 0 && port < MAX_PORT) {
				handler[port] = null;
		}
	}

	public InHandler[] getHandlers() {
		return handler;
	}


	private void resetCount() {
		for (int i=0;i<MAX_PORT;i++) {
			count[i] = 0;
		}
		total = 0;
		msgLength = 0;
		handled = 0;
		notHandled = 0;
		sysCount = 0;
		userCount = 0;
	}

	public void registerCommands() {
		if (f == null) return;
		f.add(prefix + "status", 	new Status() );
		unpacker.registerCommands(f,prefix);
	}

	public void deregisterCommands() {
		if (f == null) return;
		f.remove(prefix + "status");
	}

	public void dispatch(NetMessage m) {

		if (m == null) return;	//RECHECK: Null is really error?

		int unpacked = unpacker.parse(m);

		if (unpacked == -1) {
			System.out.println("Dispatcher:" + unpacker.getError() );
		} else {

			for (int i = 0; i < unpacked;i++) {
				NetMessage n = NetMessage.getInstance();
				unpacker.copy(n,i);
				n.setSocket(m.getSocket() );
				_dispatch(n);
			}
		}

		NetMessage.freeInstance(m);

	}


	private void _dispatch(NetMessage m) {

		if (m == null) 	return;	//RECHECK: Null is error?

		int port = (int) m.getByte(0);

		InHandler h = get(port);

		if(total >= RESET_COUNT) {
			resetCount();
		}

		count[port]++;
		total++;

		if (h.isSystem()) {
			sysCount++;
		} else {
			userCount++;
		}


		fullTotal++;
		msgLength += m.getSize();

		if (h == null) {
			NetMessage.freeInstance(m);
			m = null;
			notHandled++;
		} else {
			handled++;
			h.input(m);
		}

    }

	public long getCount() {
		return (long) fullTotal;
	}

	public StringBuffer overview() {
		StringBuffer b = new StringBuffer(6);

		b.append(sysCount);
		b.append('-');
		b.append(userCount);

		if (notHandled > 0) {
			b.append("WARNING: NOT HANDLED: " + (100* notHandled/total) + "%");
		}
		return b;
	}

	public int sysCount() { return sysCount; }
	public int userCount() { return userCount; }

	public StringBuffer stats() {

		StringBuffer b = new StringBuffer(100);
		b.append("DISPATCH");
		if (total == 0) {
			b.append("no input received");
		} else {
			b.append(" [" + total + "/" + RESET_COUNT + "]");

			if (total > 0) {
				b.append("\n[msg-size average:" + (msgLength/total) + "]");
			}

			b.append('\n');
			//int userCount = 0;
			//int sysCount = 0;

			for (int i=0;i<MAX_PORT;i++) {
				if (count[i] != 0) {
					InHandler h = get(i);

					if (h == null)
						throw new IllegalStateException("Unknown inHandler,port=" + i);


					/*
					if (h.isSystem()) {
						sysCount += count[i];
					} else {
						userCount += count[i];
					}
					*/
					b.append("[");
					b.append(h.desc());

					b.append(": ");

					double ratio = (count[i] * 100.0) / total;
					String pcent =
							percentFormatter.format(new Double(ratio) );
					b.append(pcent);
					b.append('%');

					b.append("] ");

				}
			}
			b.append("\nuserCount = " + userCount);
			b.append("\nsysCount  = " + sysCount);

			double userP = userCount*100.0/total;
			b.append("\nuser% = " + percentFormatter.format(new Double(userP) ) );

			double sysP = sysCount*100.0/total;
			b.append("\nsys% = " +  percentFormatter.format(new Double(sysP) ) );


		}
		return b;

	}

	class Status extends Command {

		public Status() {
			setUsage("stats");
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
			if (isValid ) {
				setResult(stats().toString() + unpacker.status() );
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}

}