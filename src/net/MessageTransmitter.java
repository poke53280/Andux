package net;

import java.util.Hashtable;
import java.net.DatagramPacket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class MessageTransmitter extends PacketTransmitter {

	protected InetCache cache = new InetCache();

	public MessageTransmitter(int port, String hostname, int to) throws IOException {
		super(port, hostname, to);
	}

	public MessageTransmitter() { ; }	//When running in sim mode


	protected SocketAddress get(long sid) {

		SocketAddress sa = cache.get(sid);

		if (sa == null) {

			String hostname = IPString.getIPString(sid);
			int port = IPString.getPort(sid);

			try {
				sa = new SocketAddress(hostname, port);
			} catch (UnknownHostException e) {
				System.out.println("Could not construct address");
				System.exit(1);
			}

			cache.put(sid,sa);
			//System.out.println("new socket from system: " + sa.description() );
		}

		return sa;

	}

	  public void send(NetMessage m) {

		long sid = m.getSocket();

		SocketAddress sa = get(sid);

		DatagramPacket packet = new DatagramPacket(m.getData(),
		                                	    m.getSize(),
		                                	    sa.getAddress(),
	                                    		sa.getPort() );


		super.send(packet);
		NetMessage.freeInstance(m);

    }

	  public NetMessage receive() {

		NetMessage m = NetMessage.getInstance(MAX_RECEIVE_SIZE);

		DatagramPacket packet = new DatagramPacket(m.getData(), m.getSize() );

		boolean recvOK = super.receive(packet);

		if (recvOK) {

			if (packet.getLength() >= MAX_RECEIVE_SIZE)
				throw new IllegalStateException("l>=MAX,l=" + packet.getLength() );


			InetAddress ia = packet.getAddress();
			byte[] b = ia.getAddress();
			int port = packet.getPort();
			long socket = IPString.getLong(b,port);

			m.setSocket(socket);
			m.setSize(packet.getLength() );


			SocketAddress sa = cache.get(socket);

			if (sa == null) {


				sa = new SocketAddress(packet.getAddress(), packet.getPort());


				cache.put(socket, sa);

			}
			return m;



		} else {
			NetMessage.freeInstance(m);
			m = null;
			return null;
		}

	}

	protected String cacheStatus() {
		return cache.status();
	}


    public void close() {
        System.out.println("MessageTransmitter.close()");
		super.close();
    }



	private class SocketAddress {

		//public static final int PORT_MIN = 1025;
		//public static final int PORT_MAX = 8000;
	    private InetAddress address = null;
	    private int port = 0;

	    public SocketAddress(String hostname, int port) throws UnknownHostException {
	        this.port = port;
//	        this.address = null;//setAddress(hostname);

			this.address = InetAddress.getByName(hostname);
/*

	        try {
	            address = InetAddress.getByName(hostname);
	        }

	        catch (UnknownHostException e) {
	            port = 0;
	            System.out.println("SocketAddress.setAddress(): Host unknown");
	        } finally {
	            return a;
	        }

*/

	    }

	    public SocketAddress(InetAddress address, int port) {
	        this.port = port;
	        this.address = address;
	    }

	    public int getPort() { return port; }
	    public InetAddress getAddress() { return address; }
	    public String getHostname() { return address.getHostAddress(); }
		public String description() { return getHostname() + ":" + port; }

	/*
	    private InetAddress setAddress (String host) {

	        InetAddress a = null;

	        try {
	            a = InetAddress.getByName(host);
	        } catch (UnknownHostException e) {
	            port = 0;
	            System.out.println("SocketAddress.setAddress(): Host unknown");
	        } finally {
	            return a;
	        }
	    }
	*/

	}


	private class InetCache {

		Hashtable l = new Hashtable();

		private int getSuccess = 0;
		private int getFailure = 0;

		public void put(long lSock, SocketAddress sa) {

			if(l.containsKey(new Long(lSock) ) )
				throw new IllegalStateException("Socket exists,lSock=" + lSock);

			l.put(new Long(lSock), sa);
			//System.out.println("InetCache: New socket added. Holding " + l.size() + " sockets");

		}

		public SocketAddress get(long lSock) {

			SocketAddress sa = (SocketAddress) l.get(new Long(lSock) );
			if (sa == null) {
				getFailure++;
				return null;
			} else {
				getSuccess++;
				return sa;
			}
		}

		public String status() {
			if (getSuccess + getFailure == 0) return "N/A";
			int hitRatio = (getSuccess*100) / (getSuccess + getFailure);

			//return "size = " + l.size() + ", hit = " + getSuccess +
			//", miss = " + getFailure + ", hit% > " + hitRatio;
			return "hit% >" + hitRatio;

		}

	}


}

