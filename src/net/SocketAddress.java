package net;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Maintainer of a socket (IP, port)<P>
 *
 * @author Anders E. Topper
 * @since 060999
 * @see InetAddress
 */

public class SocketAddress {
	public static final int PORT_MIN = 1025;
	public static final int PORT_MAX = 8000;
    private InetAddress address = null;
    private int port = 0;



    /**
     * Constructs a SocketAddress with hostname and IP
     *
     * @param hostname Hostname in plain text or IP as "%d.%d.%d.%d"
     * @param port Port number
     */


     public static boolean portIsValid(int port) {
		 if (port >= PORT_MIN && port <= PORT_MAX) {
			 return true;
		 } else {
			 return false;
		 }
	 }

    public SocketAddress(String hostname, int port) {
        this.port = port;
        this.address = setAddress(hostname);

    }

    /**
     * Constructs a SocketAddress from an existing IP-address
     *
     * @param address Existing IP-address
     * @param port Port number
     */

    public SocketAddress(InetAddress address, int port) {
        this.port = port;
        this.address = address;



    }

    /**
     * Changes this SocketAddress' values.
     *
     * @param hostname
     * @param port
     */


    public void setSocket(String hostname, int port) {
        this.port = port;
        this.address = setAddress(hostname);


    }

    /**
     * Changes the socket contained in this SocketAddress.
     *
     * @param address
     * @param port
     */


    public void setSocket(InetAddress address, int port) {
        this.address = address;
        this.port = port;

    }

    /**
     * Checks if this SocketAddress contains
     * an IP, port - address.<P>
     * A failed initializatin or change will give a false return.
     */


    public boolean isSet() {
        //System.out.println("SocketAddress.isSet()");

        if (port == 0 || address == null) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * @return port number. 0 if not set.
     */


    public int getPort() {
        return port;

    }

    /**
     * @return Current InetAddress. null if not set.
     */

    public InetAddress getAddress() {
        return address;

    }

    /**
     * SocketAddress looks up the IP ("%d.%d.%d.%d")
     * of the set InetAddress.
     */


    public String getHostname() {
        return address.getHostAddress();
    }


	public static boolean isValid(String host) {
		boolean isValid = true;
		try {
			InetAddress i = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			isValid = false;
		}
		return isValid;
	}

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

    /**
     * @return "%d.%d.%d.%d" + ":" + port, e.g.:
     * "193.213.36.20:80"
     */
    public String description() {
        if (isSet() ) {
            return getHostname() + ":" + port;

        } else {
            return "[not set]";
        }




    }



}
