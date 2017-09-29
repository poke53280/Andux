package net;

import java.util.StringTokenizer;
import root.ParseUtil;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class IPString {

	public static final int PORT_MIN = 1025;
	public static final int PORT_MAX = 8000;



	public static long getSID(String hostname, int port) throws IllegalArgumentException {

		if (!portIsValid(port) || ! isValid(hostname) ) {
			System.out.println("IPString:Bad hostname, port:" + hostname + ":" + port);
			return -1L;
		}

		long sid;

		try {
			sid = getLong(hostname, port);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("MT:Can't create sid:" + hostname + ":" + port);
		}

		return sid;

	}



	public static boolean portIsValid(int port) {
		 if (port >= PORT_MIN && port <= PORT_MAX) {
			 return true;
		 } else {
			 return false;
		 }
	 }

	public static boolean isValid(String host) {
		if (host == null || host.equals("")) return false;

		boolean isValid = true;
		try {
			InetAddress i = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			isValid = false;
		}
		return isValid;
	}


	/*
	 Input: ip as byte[4]
	 Output: ip as integer
	*/

	public static int getInt(byte[] ip) {
		//a.b.c.d
		return  ((ip[0] & 0xFF) << 24)
			  + ((ip[1] & 0xFF) << 16)
			  + ((ip[2] & 0xFF) << 8)
			  + (ip[3] & 0xFF);


	}


	/*
	 Input: ip as byte[4], port as integer
	 Output: Socket as long.

	*/

	public static long getLong(byte[] b, int port) {
		int ip  = getInt(b);
		return getLong(ip, port);
	}




	/*
	 Input: ip as string "a.b.c.d"
	 Output: ip as integer
	*/

	public static int getInt(String s) throws IllegalArgumentException {
		StringTokenizer t = new StringTokenizer(s, ".");
		if (t.countTokens() != 4) {
			throw new IllegalArgumentException();
		}
		int ip = 0;

		while (t.hasMoreTokens() ) {
			String q = t.nextToken();
			int aByte = ParseUtil.parseInt(q);
			if (aByte < 0 || aByte > 255) {
				throw new IllegalArgumentException();
			} else {
				ip = ip << 8;
				ip += aByte;
			}
		}
		return ip;
	}


	/*
	 Input: ip as integer
	 Output: ip as string "a.b.c.d"
	*/

	public static String getString(int ip) {
		StringBuffer b = new StringBuffer(15);
		char c = '.';

		b.append((ip & 0xff000000) >>>24);
		b.append(c);

		b.append((ip & 0xff0000) >>>16);
		b.append(c);

		b.append((ip & 0xff00) >>>8);
		b.append(c);

		b.append(ip & 0xff);

		return b.toString();
	}


	public static void Bmain(String[] args) {

		String ipString = args[0];

		int port = ParseUtil.parseInt(args[1]);

		System.out.println("ip   =" + ipString );
		System.out.println("port =" + port );

		int ip = 0;
		try {
			ip = getInt(ipString);
		} catch (IllegalArgumentException ie) {
			System.out.println("error");
		} finally {
			//
		}

		long t = getLong(ip, port);
		System.out.println("----------");
		//ip = getIP(t);
		//port = getPort(t);

		//System.out.println(getString(ip) + ":" + port);
		System.out.println(getAddressString(t) );

	}



	/*
	*  	Input: socket as "a.b.c.d:port"
	*   Outout: socket as long
	*/

	public static long getLong(String s) throws IllegalArgumentException {

		StringTokenizer t = new StringTokenizer(s, ":");
		if (t.countTokens() != 2) {
			throw new IllegalArgumentException();
		}

		String ip = t.nextToken();
		String p  = t.nextToken();

		int port = ParseUtil.parseInt(p);
		if (port < 0) {
			throw new IllegalArgumentException();
		}

		System.out.println("found ip='" + ip + "'");
		System.out.println("    port='" + port + "'");

		return getLong(ip, port);

	}


	/*
	 Input: host as ip "a.b.c.d", port as integer
	 Output: socket as long
	*/
	public static long getLong(String host, int port)
							throws IllegalArgumentException {
		int ip = 0;
		try {
			ip = getInt(host);
		} catch (IllegalArgumentException ie) {
			//System.out.println("error: " + host + " no ip-string");
			throw new IllegalArgumentException();
		}
		return getLong(ip, port);

	}


	/*
	 Input: socket as long
	 Output: ip, port as String: "a.b.c.d:port"
	*/
	public static String getAddressString(long id) {
		int port = getPort(id);
		return getIPString(id) + ":" + port;
	}


	/*
	 Input : socket as long
	 Output: ip as string "a.b.c.d" (no port specified)

	*/
	public static String getIPString(long id) {
		int ip = getIP(id);
		return getString(ip);
	}


	/*
	 Input: ip and port as integers.
	 Output: socket as long
	*/

	public static long getLong(int ip, int port) {
		long l = (long) ip;
		l <<= 16;
		l += port;
		return l;
	}


   /*
	Input : socket as long
	Output: The IP-address as integer
   */
	public static int getIP(long l) {
		return (int) (l >>>16) & (0xffffffff);
	}


	/*
	 Input:    socket as long
	 Output:   the port as integer
	*/
	public static int getPort(long l) {
		return (int) l & (0xffff);
	}


	public static void main(String[] s) {

		try {
			getLong("12.121.43.23:2");
			getLong("12.11.3.23:12");

		} catch (IllegalArgumentException e) {
			System.out.println("bad string");
		}

	}


}