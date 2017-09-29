package net;


import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.net.Socket;
import java.text.DecimalFormat;

import root.Factory;
import root.Command;

/*
	-- Responsible for sending and receiving DatagramPackets.
	-- Configurable time-out, so may both block for ever and time-out on
	   receival.
*/

public class PacketTransmitter  {

	public static final int MAX_RECEIVE_SIZE = 800;


	public static final long RESET_COUNT = 500L;	//Reset when received this many packets


	long inCount;
	long outCount;

	long inPackets;
	long timeoutPackets;
	long outPackets;

	long resetTime;

	long ioErrorCount = 0L;
	String lastIoError = "none";

	long localSID;



	DecimalFormat format = new DecimalFormat();

    protected DatagramSocket socket = null;


	public PacketTransmitter() { ; } //For sim mode

    public PacketTransmitter(int p, String hostname, int to) throws IOException {

		//System.out.println("PacketTransmitter constructor, hostname=" + hostname + ", port=" + p);


		 resetCount();
		 format.applyPattern("#####0.0");

		InetAddress ip = null;

		if (hostname != null) {

			try {
			 	ip = InetAddress.getByName(hostname);
			} catch (UnknownHostException e) {
				System.out.println("No address found");
				throw new IOException();
			}

		}

		 if (!IPString.portIsValid(p) ) {
			System.out.println("PacketTransmitter: Port not valid " + p);
			throw new IOException();
		 }

         try {
            if (ip == null) {
            	socket = new DatagramSocket(p);
			} else {
				socket = new DatagramSocket(p,ip);
			}

         } catch (SocketException e) {
			 socket = null;
         } catch (IOException ie) {
			  socket = null;
         } finally {

              setTimeOut(to);
              if (socket == null) {
					throw new IOException("PacketTransmitter: Couldn't construct socket");
			  } else {
					//InetAddress i = socket.getLocalAddress();
					int port = socket.getLocalPort();

					//Here, we see the local name change making it impossible to
					//reference a socket with a string (ip or name):
					//System.out.println("Datagramsocket created on "
					//	+ i.getHostAddress() + ":" + port + ", hostname was:" + hostname);


					//byte[] b = i.getAddress();
					//localSID = IPString.getLong(b,port);

					if (hostname == null) hostname = "127.0.0.1";

					try {
						localSID = IPString.getLong(hostname, port);
					} catch (IllegalArgumentException e) {
						throw new IllegalArgumentException("Couldnt construct");
					}

			  }
        }


    }

	public long getLocalSID() {
		return localSID;
	}

	public int getLocalPort() {
		return IPString.getPort(localSID);

	}





	public void registerCommands(String prefix, Factory f) {
		f.add(prefix + "netreset", new Reset() );
	}


	//XXX Blocks when accessed 'from outside' Unresolved problem.
	//Perhaps when socket is blocking.

	private int getTimeout() {
		int t = -1;
		try {
			t = socket.getSoTimeout();
		} catch (SocketException se) {
			//
		}
		return t;

	}

	//XXX Blocks when accessed 'from outside' Unresolved problem.
	public void setTimeOut(int to) {
		if (socket == null) {
			return;
		}

		if (to < 0) {
			throw new IllegalArgumentException("timeout < 0");
		}


		try {
			socket.setSoTimeout(to);
			//System.out.println("timeout set to " + to);
		} catch (SocketException e) {
			System.out.println("PacketTransmitter.setNoTimeOut: " + e.getMessage() );
			socket = null;
		} catch (Exception _e) {
			System.out.println("PacketTransmitter.setNoTimeOut: " + _e.getMessage() );
			socket = null;
		} finally {
			/*

			if (socket != null) {


				int t = getTimeout();
				if (t != -1) {
					System.out.println("PacketTransmitter: Timeout set to " + t + " ms");
				} else {
					System.out.println("PacketTransmitter: Unable ro read timeout setting");
				}

			}
			*/
		}


	}


	public void resetCount() {
		resetTime = System.currentTimeMillis();
		inCount = 0L;
		outCount = 0L;

		inPackets = 0L;
		timeoutPackets = 0L;
		outPackets = 0L;

		ioErrorCount = 0L;
		lastIoError = "none";
	}

	public boolean send(DatagramPacket p) {

		 boolean isOK = false;
		 try {
            socket.send(p);
			outCount += 8*p.getLength();
			outPackets++;
			isOK = true;

         } catch (IOException ie) {
            //With bad address 'host not reachable' errors
            //System.out.println("PacketTransmitter.send(): " + ie.getMessage() );
         }
		 return isOK;
	}

	public boolean receive(DatagramPacket p) {

           try {
               socket.receive(p);
               /*
               if (p.getLength() >= MAX_RECEIVE_SIZE) {
                    System.out.println("UDP.receive : length >= MAX size: "
                    	+ p.getLength() );
                    p = null;
               }
				*/
			} catch (InterruptedIOException ir) {
            	p = null;
				timeoutPackets++;
          	} catch (IOException ie) {
				ioErrorCount++;
				lastIoError = ie.getMessage();
                p = null;
            }

			if (p != null && p.getLength() > 0) {


				//Reset:
				if (inPackets >= RESET_COUNT) {
					resetCount();
				}


				inCount += 8*p.getLength();

				inPackets++;

				return true;
			} else {
				return false;
			}
	}


   public void close() {
            socket.close();
   }

	public String transferRate() {


		StringBuffer b = new StringBuffer(500);



		//b.append("\n#bits in:  " + inCount);
		//b.append("\n#bits out: " + outCount);

		//b.append("\n#packets in   :");
		//b.append(inPackets);

		//b.append("\n#packets out :" + outPackets);


		long dTime = System.currentTimeMillis() - resetTime;

		double dSec = dTime/1000.0;

		double bps_in  =  inCount/ dSec;
		double bps_out = outCount/ dSec;

		double pac_in   =  inPackets/ dSec;
		double pac_out  = outPackets/ dSec;

		String inDevice = NetDevice.getDevice(bps_in);
		b.append("[IN: " + format.format(new Double(bps_in/1024.0) ) + "kb/s");
		b.append(", " + format.format(new Double(pac_in) ) + "p/s]");
		b.append(", " + inDevice);

		String outDevice = NetDevice.getDevice(bps_out);
		b.append(" [OUT: " + format.format(new Double(bps_out/1024.0) ) + "kb/s");
		b.append(", " + format.format(new Double(pac_out) ) + "p/s]");
		b.append(", " + outDevice);

/*
		b.append("\nTime-out:");
		int timeOut = getTimeout();
		if (timeOut == 0) {
			b.append(" Blocking");
		} else {
			b.append(timeOut);
			b.append(" ms");
		}
*/
		b.append("\n[" + inPackets + "/" + RESET_COUNT + "]");

		String toprc = format.format(new Double(timeoutPackets*100.0/(timeoutPackets + inPackets) ) );
		b.append(" [time-out%:" + toprc + "]");

		if (ioErrorCount > 0) {
			b.append(" [last error: " + lastIoError + "]");
			b.append(" [error:" + ioErrorCount + "]");
		}

		return b.toString();


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
/*
	public static void main (String[] args) {

		System.out.println("PacketTransmitter.main");

		PacketTransmitter u = null;

		try {
			u = new PacketTransmitter(3010,"192.168.0.9",0);
		} catch (IOException ie) {
			System.out.println(ie.getMessage() );
		}

		DatagramPacket rP = new DatagramPacket(new byte[100],100);
		DatagramPacket sP = null;

		try {

			sP = new DatagramPacket(new byte[100],100,
									InetAddress.getByName("192.168.0.9"), 3090);

		} catch (UnknownHostException uh) {
			System.out.println(uh.getMessage() );
		}

		u.setTimeOut(5*1000);



		boolean recv = u.receive(rP);

		if (recv) {
			System.out.println("Recv OK");
		} else {
			System.out.println("Recv FAILED");
		}

		u.close();

	}
*/
}

