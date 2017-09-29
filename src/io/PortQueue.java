package io;
import java.util.Vector;
import net.SocketAddress;

/**
 * Adorns a Queue with information about a specific host name and port number.
 *
 * @author Anders E. Topper
 * @since Reviewed 250899
 */

public class PortQueue extends Queue {

    protected String    host = null;
    protected int       port = 0;

	protected boolean	hasMaintainer = false;

    /**
     * Initiates a PortQueue without host information
     *
     * @param s SocketAddress
     */

    public PortQueue() {
        super();

    }



    /**
     * Initiates a PortQueue with specified SocketAddress attached.
     *
     * @param s SocketAddress
     */

    public PortQueue(SocketAddress a) {
        super();
        this.host = a.getHostname();
        this.port = a.getPort();
    }


    /**
     * Returns a string signature for the stored host, port as e.g.:<BR>
     * "192.168.0.1:2224," where "192.168.0.1" is host name and 2224 is port number.
     */

    public synchronized String getSocketInfo() {
        return host + ":" + port;

    }

    /**
     * @return The host name
     */

    public synchronized String getHost() {
        return host;
    }

    /**
     * @return The port number
     */

    public synchronized int getPort() {
        return port;

    }

	public void attach() {
		if (!hasMaintainer) {
			hasMaintainer = true;
			System.out.println("PortQueue: attached OK");

		} else {
			System.out.println("PortQueue.attach: Error: Already is attached");

		}


	}

	public void detach() {
		if (hasMaintainer) {

			System.out.println("PortQueue: Detached OK");
			hasMaintainer = false;
		} else {

			System.out.println("PortQueue: Noone to detach from");

		}




	}

	public boolean isMaintained() {
		return hasMaintainer;

	}
}
