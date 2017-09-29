package io.handler;

import io.Wire;
import net.SocketAddress;
import net.IPString;

import java.awt.Dimension;
import java.awt.Point;

import root.Command;
import root.Factory;
import root.ParseUtil;

public class RemoteNode implements InHandler {

	private String LOCALHOST = "127.0.0.1";

	private Wire w = null;
    private NodeMessage nm;
	private LocalNode l;
	private boolean onNet;


	public RemoteNode(LocalNode l, Wire w, boolean onNet) {
		this.w = w;
		this.l = l;
		this.onNet = onNet;

		nm = new NodeMessage();
	}

	public String desc() {
		return "r-node";
	}

	public void announce() {

		nm.create();
		boolean b = l.fillMessage(nm);
		//if (!b)
                if (true)
                {
System.out.println("RemoteNode.announce FAILED");
                    nm.release();
			//System.out.println("RemoteNode.announce FAILED");
			return;		//Something went wrong. Like local name only.
		}

              
		//Message m = nm.getMessage();
		//nm.forget();
		//w.push(m);

	}

	public void input(Message m) {

		if (m == null) {
			System.out.println("message null");
			return;
		}

		nm.setMessage(m);

		if (onNet && nm.getIPString().equals(LOCALHOST)) {
			System.out.println("RemoteNode.input.Warning:"
										+ " Received localaddress. Skipping because onNet");
			nm.release();
			return;

		}


		if (isRemote(nm.getIPString(), nm.getPort() ) ) {
		} else {
			l.contact(nm);
		}

		nm.release();

	}

	public Message getUpdate() {
System.out.println("RemoteNode.getUpdate: Bad IP string. Message dropped");
		return null;
		
	}


	/**
	* Announcing data about any known node to remote node.
	*/

	public void announce(RemoteNode r) {
System.out.println("RemoteNode.announce(RemoteNode) failed");
		

	}


	private boolean isRemote(String ip, int port) {
System.out.println("RemoteNode.aisRemote failed");
		return false;

	}

	//----------------------------------------

	public void registerCommands(Factory f, String prefix) {
		f.add(prefix + "announce", new Announce() );
	}

	public void deregisterCommands(Factory f, String prefix) {
		f.remove(prefix + "announce");
	}


	class Announce extends Command {

		public Announce() {
			setUsage("announce <pID>" );
		}

		public Command create(String[] args) {
			Command c = new Announce();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				announce();
				setResult("");
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

	}

public class NodeMessage extends Manipulator {

   //protected static final int NAME = 1;
//	protected static final int NAME_LENGTH = 8;

   //protected static final int PORT = 17;
   //protected static final int IP = 21;
   //protected static final int SIZE = 25;

 	protected static final int PORT = 1;
  	protected static final int IP = 5;
  	protected static final int SIZE = 9;


   public NodeMessage() {
		super();
   }

	public void create() {
		if (m == null) {
			m = Message.getInstance();
			m.setByte(Port.PORT, (byte) Port.NODE);
			m.setSize(SIZE);
		} else {
			throw new IllegalStateException("Message already set");

		}
	}

	public void setMessage(Message me) {
		if (this.m == null) {
			this.m = me;
			if (me.getSize() != SIZE) {
				throw new IllegalStateException("wrong size:" + me.getSize() );
			}
		}
	}

/*
	public void setName(String name) {
		m.setString(NAME, name, NAME_LENGTH);
	}

	public String getName() {
		return m.getString(NAME);
	}
*/

	public void setIP(int ip) {
		m.setInteger(IP, ip);
	}

	public void setIP(String s) throws IllegalArgumentException {
		int ip = IPString.getInt(s);
		setIP(ip);
	}

	public int getIP() {
		return m.getInteger(IP);
	}

	public String getIPString() {
		int ip = getIP();
		return IPString.getString(ip);
	}

	public void setPort(int p) {
		m.setInteger(PORT, p);
	}

	public int getPort() {
		return m.getInteger(PORT);
	}


}


}