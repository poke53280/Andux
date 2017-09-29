package io.handler;

import io.WireState;

import root.Command;
import root.Factory;

import java.text.DecimalFormat;

public class PLoss implements InHandler {

	private MessageSender sender;
	private LossMessage l = null;
	private WireState state = null;


	private static final long MINIMUM_SEND = 110;

	private long outCounter = -1L;
	private long inCounter = -1L;

	private long countID = 0;
	private long remoteID = -1;


	private long inRec  = 0L;
	private long inSent = 0L;

	private long inRecAll = 0L;
	private long inSentAll = 0L;

	private double inLoss = -1.0;
	private double inLossAll = -1.0;

	private DecimalFormat percentFormatter = new DecimalFormat();


	public PLoss(MessageSender s) {
		sender = s;
		l = new LossMessage();

		percentFormatter.applyPattern("##0.00");

	}

	public String desc() {
		return "ploss";
	}

	private void reset() {
		outCounter = -1L;
		inCounter = -1L;
		inRec  = 0L;
		inSent = 0L;

		inRecAll = 0L;
		inSentAll = 0L;

		inLoss = -1.0;
		inLossAll = -1.0;
	}


	private void startCount() {
		outCounter = state.getSent();

		l.create();

		countID++;

		l.setReceived(countID);
		l.setSent(0);

		Message m = l.getMessage();
		l.forget();
		sender.push(m);
	}


	private void stopCount() {

		if (state.getSent() - outCounter < MINIMUM_SEND) {
			//Hasnt sent enough. Wait.
			return;
		}

		l.create();

		l.setReceived(countID);
		l.setSent(state.getSent() - outCounter);

		Message m = l.getMessage();
		l.forget();
		sender.push(m);
		outCounter = -1L;
	}

	public void input(Message m) {
		if (m== null) {
			System.out.println("message null");
			return;
		}

		l.setMessage(m);

		long remoteOut = l.getSent();

		if (remoteOut == 0 && inCounter == -1) {
			remoteID = l.getReceived();
			inCounter = state.getReceived();
		} else if (remoteOut > 0 && inCounter != -1) {

			if (remoteID == l.getReceived() ) {
				calcLoss(state.getReceived() - inCounter, remoteOut);
				inCounter = -1;
			} else {
				//System.out.println("Count session broken:");
				inCounter = -1;
			}


		} else {
			//System.out.println("Ploss.input: PLoss control packet probably lost. Resetting.");
			inCounter = -1;
		}

		l.release();

	}


	public void setState(WireState s) {
		state = s;
	}



	public String state() {
		if (inLoss == -1.0) {
			return "IN: N/A";
		} else {
			String last = percentFormatter.format(new Double(inLoss) );
			String accu = percentFormatter.format(new Double(inLossAll) );
			return "INlast: " + last + "% " + ", INacc: " + accu + "%";
		}
	}


	public void send() {
		if (state == null) {
			return;
		}
		if (outCounter == -1L) {
			startCount();
		} else {
			stopCount();
		}
	}

	private void calcLoss(long received, long sent) {
		if (state == null) {
			return;
		}

		inRec  = received;
		inSent = sent;

		if (inRec > inSent) {
			System.out.println("Ploss: Warning: More received than sent");
			//System.out.println("Last sent: " + inSent);
			//System.out.println("Last received: " + inRec);
			//System.out.println("Using data still");
		}
		//else {

			inLoss = 100.0 - (inRec * 100.0) / inSent;

			inRecAll += inRec;
			inSentAll += inSent;
			inLossAll = 100.0 - (inRecAll * 100.0) / inSentAll;
		//}
	}

	public void registerCommands(Factory f, String prefix) {
			f.add(prefix + "reset"  , new Reset() );
	}

	public void deregisterCommands(Factory f, String prefix) {
			f.remove(prefix + "reset");
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
			if (isValid ) {
				reset();
				setResult("packetloss statistics reset");
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}

public class LossMessage extends Manipulator {

   protected static final int RECEIVED = 1;
   protected static final int SENT = 9;

   protected static final int SIZE = 17;


   public LossMessage() {
		super();
   }

	public void create() {
		if (m == null) {
			m = Message.getInstance();
			m.setByte(Port.PORT, (byte) Port.PLOSS);
			m.setSize(SIZE);
		} else {
			throw new IllegalStateException("Message already set");

		}
	}

	public void setMessage(Message me) {
		if (this.m == null) {
			this.m = me;
			if (me.getSize() != SIZE) {
				throw new IllegalStateException("wrong size:" + me.getSize() );
			}
		}
	}

	public void setSent(long sent) {
		m.setLong(SENT, sent);
	}

	public long getSent() {
		return m.getLong(SENT);
	}

	public void setReceived(long received) {
		m.setLong(RECEIVED, received);
	}

	public long getReceived() {
		return m.getLong(RECEIVED);
	}

}

}