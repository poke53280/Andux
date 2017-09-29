package net;

import root.Sleeper;
import io.Queue;
import root.Command;
import root.Factory;


import java.util.Hashtable;
import java.util.Enumeration;
import java.io.IOException;

import io.handler.Message;


public class DataPlexer implements Runnable {

    NetTransmitter net = null;

    static final protected int WAIT = 30;

	protected boolean goOn = true;

    protected Sleeper sleeper = new Sleeper(WAIT);

	private Queue incoming = new Queue();

	long inCount;
	long outCount;
	long resetTime;

	protected Thread plexThread = null;

	public DataPlexer(Transmitter u) {
		net = new NetTransmitter(u);
	}

	public void registerCommands(String prefix, Factory f) {
		f.add(prefix + "netstat", new Status() );
		f.add(prefix + "netreset", new Reset() );
	}

	public void start() {
		if (plexThread == null) {
			plexThread = new Thread(this, "DataPlexer");
			resetCount();
			plexThread.start();
		}
	}

	private void resetCount() {
		resetTime = System.currentTimeMillis();
		inCount = 0L;
		outCount = 0L;

		if (net != null) {
			net.resetCount();
		}


	}


	public void stop() {
		goOn = false;
		net.close();

		if (plexThread != null) {
			plexThread.interrupt();
		}
		plexThread = null;

	}



	public void run() {

	   AddressedData d = null;

   	   while (goOn) {

   	     //sleeper.sleep();
   	     Thread.yield();  //try this one instead of sleep?
   	     d = net.receive();

		 if (d != null) {
            incoming.push(d);
			inCount++;
		 }

      }
      net.close();
      System.out.println("Dataplexer finished");
    }


	public AddressedData pop() {
		if (incoming.isEmpty() ) {
			return null;
		} else {
			return (AddressedData) incoming.pop();
		}

	}

    public void send(AddressedData d) {
       net.send(d);
	   outCount++;
    }

	private String transferRate() {

		StringBuffer b = new StringBuffer(200);

		b.append("#packets in: " + inCount);
		b.append("\n#packets out:" + outCount);
		b.append(net.transferRate() );

		return b.toString();

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
			if (isValid ) {
				setResult(transferRate() );
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

	}


	class Reset extends Command {

		public Reset() {
			setUsage("reset");
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
			resetCount();
			setResult("Counters reset");

		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}

}
