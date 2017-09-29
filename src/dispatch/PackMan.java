
package dispatch;

import net.NetMessage;
import net.PacketTransmitter;

public class PackMan {

	NetMessage m = null;

	public void startBuild(NetMessage m, NetMessage n) {
		if (this.m != null)
			throw new IllegalStateException("Message exists");


		if (n.getSize() +4 > PacketTransmitter.MAX_RECEIVE_SIZE)
			throw new IllegalStateException("Message > max size");

		this.m = m;

		m.initPacked(n.getSize());
		m.addMessage(n);

	}

	public void add(NetMessage n) {

		if (m.getSize() < 2)	//2: minimum size + message
			throw new IllegalStateException("Message empty");


		if (n.getSize() +4 > PacketTransmitter.MAX_RECEIVE_SIZE)
			throw new IllegalStateException("Message > max size");


		m.append(n.getSize() );
		m.addMessage(n);

	}

	public boolean withinLimit(NetMessage n) {
		//Current size + message in question + 4 for length indicator
		return m.getSize() + n.getSize() +4 < PacketTransmitter.MAX_RECEIVE_SIZE;
	}


	public NetMessage take() {
		try {
			return m;
		} finally {
			m = null;
		}
	}


	public static void main(String[] args) { //For testing
		PackMan p = new PackMan();

		NetMessage m1 = createM1();
		NetMessage m2 = createM2();

		p.startBuild(NetMessage.getInstance(), m1 );
		p.add(m2);
		p.add(m1);
		p.add(m1);
		p.add(m1);
		p.add(m1);

		NetMessage m = p.take();
		System.out.println(m.list() );

		System.out.println("-----INVESTIGATE");

		UnPacker u = new UnPacker();
		int count = u.parse(m);
		if (count > -1) {
			for (int i = 0; i < count; i++) {
				System.out.println("-----MESSAGE--");
				NetMessage n = NetMessage.getInstance();
				u.copy(n,i);
				System.out.println(n.list() );
			}
		} else {
			System.out.println("Bad packet");
		}



	}


	public static NetMessage createM1() {	//For testing
		NetMessage m1 = NetMessage.getInstance(4);
		m1.setByte(0,(byte) 190);
		m1.setByte(1, (byte) 191);
		m1.setByte(2, (byte) 192);
		m1.setByte(3, (byte) 193);
		return m1;
	}

	public static NetMessage createM2() { //For testing
		NetMessage m2 = NetMessage.getInstance(7);
		m2.setByte(0,(byte) 18);
		m2.setByte(1,(byte) 19);
		m2.setByte(2,(byte) 20);
		m2.setByte(3,(byte) 21);
		m2.setByte(4,(byte) 22);
		m2.setByte(5,(byte) 23);
		m2.setByte(6,(byte) 24);
		return m2;
	}
}
