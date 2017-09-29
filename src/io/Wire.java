
package io;

import net.MessageIO;
import net.IPString;
import net.PacketTransmitter;
import root.Queue;
import net.NetMessage;

import java.util.Random;
import java.text.DecimalFormat;
import root.Command;
import root.Factory;
import root.ParseUtil;
import root.WorldSize;

import dispatch.PackMan;


public class Wire  {

	private MessageIO plexer = null;
	private long sid;
	private Queue destination = null;
	private PackMan packer = new PackMan();

	private long numberOfSent = 0;
	private int outCountMax = 0;
	private int outCountNow = 0;

	private double loseRate;

	private final Random random =
  				new Random(System.currentTimeMillis() );


	public Wire(MessageIO p, long sid) {
		plexer = p;
		destination = new Queue();	//Queue is synch'ed, but this not
									//necessary
		this.sid = sid;
		setLoseRate(WorldSize.DROP_PACKETS);
	}

	public void setLoseRate(int i) {
		if (i < 0 || i > 1000)
			throw new IllegalArgumentException("Not a part of 1000: " + i);

		double d = i/1000.0;
		loseRate = d;
	}


	public long getSID() {
		return sid;
	}

	public void push(NetMessage m) {
		if (m == null) return;
		destination.push(m);
	}

	public void send() {
		//perhaps send(int size, int number) - max number of bytes,
		//max number of packets

		outCountNow = destination.size();
		if (outCountNow > outCountMax) {
			outCountMax = outCountNow;
		}

		while (!destination.isEmpty() ) {

			//In loop, one packet of messages will be prepared and sent.
			//The dropper alters no state, dropping or not: It is invisible
			//to the system.

			NetMessage first =  (NetMessage) destination.poll();

			if (first.getSize()  +4 > PacketTransmitter.MAX_RECEIVE_SIZE)
				throw new IllegalStateException("Message too big to be sent");


			if (first == null) throw new IllegalStateException("queue couldn't be empty");

			packer.startBuild(NetMessage.getInstance(), first);
			numberOfSent++;
			NetMessage.freeInstance(first);

			while (!destination.isEmpty() ) {

				NetMessage n = (NetMessage) destination.poll();
				if (n == null) throw new IllegalStateException("queue couldn't be empty");

				if (n.getSize()  +4 > PacketTransmitter.MAX_RECEIVE_SIZE)
					throw new IllegalStateException("Message too big to be sent");


				if (packer.withinLimit(n) ) {
					packer.add(n);
					numberOfSent++;
					NetMessage.freeInstance(n);
				} else {
					//Exceeding packet size, pulling back.
					//Note: queue reordered.
					destination.push(n);		//LOG THIS
					break;
				}
			}

			NetMessage m = packer.take();

			if (loseRate > 0.0 && random.nextDouble() < loseRate ) {
				NetMessage.freeInstance(m);
			} else {
				m.setSocket(sid);
				plexer.send(m);
			}

		}

	}

	public String state() {
		return shortState();
	}

	public String shortState() {
		return " OUT #" + numberOfSent;
	}

	public long getCount() {
		return numberOfSent;
	}

}