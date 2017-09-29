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

public class UDPTransmitter extends Transmitter {

	long inCount;
	long outCount;
	long resetTime;

	long ioErrorCount = 0L;
	String lastIoError = "none";


	DecimalFormat format = new DecimalFormat();

    protected DatagramSocket socket = null;


    public UDPTransmitter(int p, String hostname) throws IOException {

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

		 if (!SocketAddress.portIsValid(p) ) {
			System.out.println("UDPTransmitter: Port not valid " + p);
			throw new IOException();
		 }

         try {
            if (ip == null) {
            	socket = new DatagramSocket(p);
			} else {
				socket = new DatagramSocket(p,ip);
			}

         } catch (SocketException e) {

			 //XXX close, et.c.
			 socket = null;
             //System.out.println("UDPTransmitter: "
             //	+ " Failed to create socket: " + e.getMessage() );

         } catch (IOException ie) {
			  socket = null;
              //System.out.println("UDPTransmitter: " + ie.getMessage() );

         } finally {

              setTimeOut(0);

              if (socket == null) {
				//System.out.println("UDPTransmitter: Couldn't construct");
				throw new IOException();
			  } else {
				InetAddress i = socket.getLocalAddress();
				int port = socket.getLocalPort();
				//System.out.println("Datagramsocket created on "
				//				+ i.getHostAddress() + ":" + port);

			  }
        }


    }


	public void setTimeOut(int to) {
		if (socket == null) {
			return;
		}

		if (to < 0) {
			throw new IllegalArgumentException("timeout < 0");
		}


		try {
			socket.setSoTimeout(to);
		} catch (SocketException e) {
			System.out.println("UDPTransmitter.setNoTimeOut: " + e.getMessage() );
			socket = null;
		} finally {
			if (socket != null) {
				int t = -1;
				try {
					t = socket.getSoTimeout();
				} catch (SocketException se) {
					//
				}
				if (t != -1) {
					//System.out.println("UDPTransmitter: Timeout set to " + t + " ms");
				} else {
					System.out.println("UDPTransmitter: Unable ro read timeout setting");
				}

			}

		}


	}


	public void resetCount() {
		resetTime = System.currentTimeMillis();
		inCount = 0L;
		outCount = 0L;
		ioErrorCount = 0L;
		lastIoError = "none";
	}


    public SocketAddress getLocalSocket() {
		return new SocketAddress(getLocalIP() , socket.getLocalPort() );
    }


	public boolean send(DatagramPacket p) {

		 boolean isOK = false;
		 try {
            socket.send(p);
			outCount += 8*p.getLength();
			isOK = true;

         } catch (IOException ie) {
            System.out.println("UDPTransmitter.send(): " + ie.getMessage() );
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
            	System.out.println("UDPTransmitter.receive: "
            			+ "InterruptedIOException:" + ir.getMessage() );


          	} catch (IOException ie) {
				ioErrorCount++;
				lastIoError = ie.getMessage();
                p = null;
            }

			if (p != null && p.getLength() > 0) {
				inCount += 8*p.getLength();
				return true;
			} else {
				return false;
			}
	}


   public void close() {
            socket.close();
   }

	public String transferRate() {

		StringBuffer b = new StringBuffer(200);

		b.append("\n#bits in:  " + inCount);
		b.append("\n#bits out: " + outCount);

		long dTime = System.currentTimeMillis() - resetTime;

		double dSec = dTime/1000.0;

		double bps_in  =  inCount/ dSec;
		double bps_out = outCount/ dSec;

		b.append("\nbps in:  " + format.format(new Double(bps_in) ));
		b.append("\nbps out: " + format.format(new Double(bps_out) ));
		b.append("\n#error in: " + ioErrorCount);
		b.append("\nlast: " + lastIoError);

		return b.toString();

	}


	private static String getLocalIP() {

		 String h = null;
		 InetAddress i;

         try {
               i = InetAddress.getLocalHost();
        } catch (UnknownHostException e ) {
             System.out.println("Unknown local host");
              i = null;
        }

        if (i != null) {
            h = i.getHostAddress();
        } else {
            System.out.println("UDPTransmitter.getLocalIP:"
            				+ " Couldnt find local IP address");
        }

		return h;
	}


	public static void main (String[] args) {

		System.out.println("UDPTransmitter.main");

		UDPTransmitter u = null;

		try {
			u = new UDPTransmitter(3010,"192.168.0.9");
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

		u.setTimeOut(10*1000);

	/*
		boolean sendOK = u.send(sP);

		if (sendOK) {
			System.out.println("Send OK");
		} else {
			System.out.println("Send FAILED");
		}
	*/

		//receive exits when sending?

		boolean recv = u.receive(rP);

		if (recv) {
			System.out.println("Recv OK");
		} else {
			System.out.println("Recv FAILED");
		}

		u.close();

	}
}

