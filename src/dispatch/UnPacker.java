package dispatch;

import java.text.DecimalFormat;

import net.NetMessage;

import root.WorldSize;
import root.Command;
import root.Factory;


public class UnPacker {

	private int max = WorldSize.PACKET_SPLIT;

	private int[] starts = new int[max];
	private int[] stops  = new int[max];

	private String error = null;

	private int msgCounter = 0;
	private int pacCounter = 0;

	private int pacLength = 0;

	private final int MAX = 10;
	private final int RESET_COUNT = 400;	//Reset counters when
										    //received this many packets



	private int[] density = new int[MAX];
	private DecimalFormat percentFormatter = new DecimalFormat();
	private NetMessage m = null;




	public UnPacker() {
		percentFormatter.applyPattern("##0.0");
	}


	public void registerCommands(Factory f, String prefix) {
		if (f == null) return;
		f.add(prefix + "packing", 	new Status() );
	}


	public int parse(NetMessage m) {
		this.m = null;
		error = null;

		if (m == null || m.getSize() < 2) {
			error = "no message";
			return -1;
		}


		int index = 0;
		int count = 0;

		while (index < m.getSize() ) {
			//int length = m.getByteAsInt(index);
			int length = m.getInteger(index);

			try {
				starts[count] = index + 4;
				stops[count] = index + 4 + length;
			} catch (IndexOutOfBoundsException e) {
				error = "Too many message slices";
				return -1;
			}

			count++;
			index += length + 4;
		}

		if (index == m.getSize() ) {
			this.m = m;
			doStats(count, m.getSize() );
			//System.out.println("Packet OK. Index ="
			//			+ index + ", m.getSize() = " + m.getSize() );
			return count;
		} else {
			error = "Packet format error. Index ="
						+ index + ", m.getSize() = " + m.getSize();
			return -1;
		}

	}

	private void doStats(int msg, int length) {
		if(pacCounter >= RESET_COUNT) {
			resetStats();
		}

		msgCounter += msg;
		pacCounter++;
		pacLength += length;

		if (msg >= MAX) {
			density[MAX-1]++;	//For upper and more.
		} else {
			density[msg]++;
		}

	}

	private void resetStats() {
		msgCounter = 0;
		pacCounter = 0;
		pacLength = 0;
		for (int i = 0; i < MAX; i++) {
			density[i] = 0;
		}

	}



	private StringBuffer printStats() {
		if (pacCounter == 0) return new StringBuffer("N/A");


		StringBuffer b = new StringBuffer(20*MAX);


		for (int i = 0; i < MAX -1; i++) {

			if (density[i] > 0) {
				b.append("[");
				b.append(i);
				b.append(':');
				//b.append(density[i]);
				//b.append(':');
				double ratio = (density[i] * 100.0) / pacCounter;
				String pcent =
						percentFormatter.format(new Double(ratio) );
				b.append(pcent);
				b.append("%]");

			}
		}
		if (density[MAX-1] > 0) {
			b.append('\n');
			b.append(MAX-1);
			b.append("+: #");
			b.append(density[MAX-1]);	//ex.: '9+: #32' 32 units with 9 and more.
		}

		return b;
	}

	public String status() {

		if (pacCounter == 0) return "N/A";

		StringBuffer b = new StringBuffer(500);

		b.append("\nUNPACKER ");

		b.append("[" + pacCounter + "/" + RESET_COUNT + "]");
		b.append("\n[#pac:" + pacCounter + "]");
		b.append(" [#msg:" + msgCounter + "]");



		double ratio = (msgCounter*1.0) / pacCounter;
		String dense =	percentFormatter.format(new Double(ratio) );


		b.append(" [msg/pac:" + dense + "]");


		b.append(" [p-size average:" + pacLength/pacCounter + "]");

		b.append("\n");
		b.append(printStats() );
		return b.toString();

	}


	public String getError() {
		return error;
	}

	public void copy(NetMessage n, int i) {
		byte[] d = m.getData();
		n.copy(d, starts[i], stops[i]);
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
			setResult(status() );
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}


}