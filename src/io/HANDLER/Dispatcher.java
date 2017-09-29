package io.handler;

import io.Wire;
import root.Command;
import root.Factory;
import root.ParseUtil;
import java.text.DecimalFormat;

public class Dispatcher implements PortListener{

	private InHandler[] handler;
	private final int MAX_PORT = Port.MAX_PORT;

	private long[] count;
	private long total;
	private DecimalFormat percentFormatter = new DecimalFormat();

	public Dispatcher() {

		count = new long[MAX_PORT];
		handler = new InHandler[MAX_PORT];
		for (int i=0;i<MAX_PORT;i++) {
			handler[i] = null;
			count[i] = 0L;
		}
		total = 0L;
		percentFormatter.applyPattern("##0.0");

	}


	public void registerCommands(Factory f, String prefix) {
		f.add(prefix + "stats", 	new Status() );
	}


	public void deregisterCommands(Factory f, String prefix) {
			f.remove(prefix + "stats");
	}

	public void dispatch(Message m) {

		if (m== null) {
			return;
		}

		int port = (int) m.getByte(0);

		InHandler h = get(port);
		count[port]++;
		total++;

		if (h == null) {
			System.out.println("No handler found");
		} else {
			h.input(m);
		}

		//Message.freeInstance(m);

    }


	public StringBuffer stats() {

		StringBuffer b = new StringBuffer(100);
		b.append('\n');
		if (total == 0) {
			b.append("no input received");
		} else {
			b.append("total:");
			b.append(total);
			b.append('\n');
			for (int i=0;i<MAX_PORT;i++) {
				if (count[i] != 0) {
					InHandler h = get(i);
					if (h != null) {
						b.append(h.desc());
					} else {
						b.append(i);
						b.append(":????");
					}
					b.append(": ");
					b.append(count[i]);
					b.append(": ");

					double ratio = (count[i] * 100.0) / total;
					String pcent =
							percentFormatter.format(new Double(ratio) );
					b.append(pcent);
					b.append('%');

					b.append('\n');
				}
			}
		}
		return b;

	}



	private InHandler get(int port) {
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
				setResult("Dispatcher - #node inputs\n"
						+ stats().toString() );
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