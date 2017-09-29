
package net;

import java.util.Hashtable;
import java.text.DecimalFormat;
import root.Queue;

import root.Factory;
import root.Command;

import statistics.SampleProvider;

class Hub extends Hashtable implements SampleProvider {

	private int messagesOut = 0;	//Includes lost ones.
	private int bitsOut = 0;
	long counter;

	long resetCount = 0L;
	private float lastValue = -1f;

	private long resetPeriod = 155L;

	public Hub() {
		super();
		reset();
	}


	public void registerCommands(Factory f, String prefix) {
		f.add(prefix + "hubstat", new Status() );
		f.add(prefix + "hubreset", new Reset() );
	}

	public void setTick(long c) {
		counter = c;
	}


	public float sampleValue() {
		if (size() == 0) return -1f;

		long ticks = counter - resetCount;

		if (ticks > resetPeriod) {


			double bitsPerTick = bitsOut/(1.0*ticks);
			double prNode = bitsPerTick/size();

			lastValue = (float) prNode;
			reset();
		}
		return lastValue;
	}


	private void reset() {
		bitsOut = 0;
		messagesOut = 0;
		resetCount = counter;

	}

	/*
		All net messages being sent enter here
	*/

	public String state() {
			if (size() == 0) return "no nodes/ no readings";

			StringBuffer b = new StringBuffer(100);
			b.append("\nreset period = " + resetPeriod);
			b.append("\nlast calc'ed = " + resetCount);
			b.append("\ntick now     = " + counter);
			b.append("\nbits out     = " + bitsOut);
			b.append("\nmsg out      = " + messagesOut);
			b.append("\nlast value   = " + lastValue);

			return b.toString();
	}

	public void send(long localSid, NetMessage m) {

		bitsOut += 8*m.getSize();
		messagesOut++;


		long sid = m.getSocket();

		Queue q = (Queue) get(new Long(sid) );
		if (q == null) {
			//just lose info, like UDP/IP
		} else {
			m.setSocket(localSid);	//Set the from address
			q.push(m);
		}

	}

	public Queue createQueue(long sid) {
		Queue q = (Queue) get(new Long(sid) );
		if (q != null) {
			//Found - returns null
			return null;
		} else {
			q = new Queue();
			put(new Long(sid), q);
			return q;
		}

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

			setResult(state() );
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

	}

	class Reset extends Command {

		public Reset() {
			setUsage("netreset");
		}

		public Command create(String[] args) {
			Command c = new Reset();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			reset();
			setResult("stats reset");
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

	}

}