package net;

import io.handler.Message;


import java.net.DatagramPacket;


public class NetTransmitter {

	protected Transmitter transmitter = null;

	public NetTransmitter(Transmitter t) {
		transmitter = t;
	}


    public void send(AddressedData aD) {

		SocketAddress sa = aD.getAddress();

		Message m = aD.getMessage();

		//XXX: Reuse DatagramPacket instead

		DatagramPacket packet = new DatagramPacket(m.getData(),
		                                    m.getSize(),
		                                    sa.getAddress(),
	                                    sa.getPort() );

		//System.out.println("NetTransmitter.send. size = " + m.getSize() );

		boolean ok = transmitter.send(packet);
		if (!ok) {
			System.out.println("NetTransmitter.send failed");
		}

		Message.freeInstance(m);

    }


	public AddressedData receive() {

		Message m = Message.getInstance(Transmitter.MAX_RECEIVE_SIZE);

		DatagramPacket packet = new DatagramPacket(m.getData(), m.getSize() );

		boolean recvOK = transmitter.receive(packet);
		//System.out.println("NetTransmitter.receive size = " + packet.getLength() );

		if (recvOK) {
			//System.out.println("NetTransmitter.receive: Got message");
			if (packet.getLength() >= Transmitter.MAX_RECEIVE_SIZE) {
                    System.out.println("UDP.receive : length >= MAX size: " + packet.getLength() );
					return null;
            } else {

				m.setSize(packet.getLength() );
				//XXXDANGEROUS?. getAddress gives ref not value?
				return new AddressedData(new SocketAddress(packet.getAddress(), packet.getPort()), m );
			}


		} else {
			//System.out.println("NetTransmitter.receive: Got null: freeing message");
			Message.freeInstance(m);
			m = null;
			return null;
		}

	}

   	public String transferRate() {
		return transmitter.transferRate();
	}

	public void resetCount() {
		transmitter.resetCount();
	}


    public void close() {
        System.out.println("NetTransmitter.close()");
        transmitter.close();

    }

}

