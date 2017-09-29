package cmd;

import root.Command;
import root.Factory;
import root.ParseUtil;
import net.PacketTransmitter;

import java.util.Hashtable;
import java.util.Enumeration;

public class ServerAccess {

	private Hashtable cons = new Hashtable();

	public ServerAccess() {
		//
	}

	private OneCom getCon(String name) {
		return (OneCom) cons.get(name);
	}

	public String exec(String name, String cmd) {

			OneCom c = getCon(name);

			if (c == null) {
				System.out.println("Not existing");
				return null;
			}

			String result = null;
			try {
				result = c.exec(cmd);
			} catch (Exception e) {
				System.out.println("ServerAccess.exec: "
																	+ e.getMessage() );
				result = null;
			}
			return result;

	}

	public boolean add(String ip, int port, String name) {

		if (name == null) {
			System.out.println("ServerAccess: Name is null");
			return false;
		}


		OneCom c = getCon(name);

		if ( c != null) {
			System.out.println("Already existing");
			return false;
		}

                // XXX Begin               
                /*
		if (!PacketTransmitter.portIsValid(port) || !PacketTransmitter.isValid(ip) ) {
				System.out.println("ServerAccess: Invalid port or address:");
				System.out.println("ip=[" + ip + "]");
				System.out.println("port=[" + port + "]");

				return false;
		}
                */
                // XXX End

		c = null;
		boolean ok = true;
		try {
			c = new OneCom(name, ip, port);
		} catch (Exception e) {
			//System.out.println("ServerAccess: Failed to create OneCom");
			System.out.println("nDux: I can't find any server. "
							+ "Now, please enter server hostname(IP) and port!");

			ok = false;
			c = null;
		}

		if (ok) {
			cons.put(name, c);
		}

		return ok;

	}

	public boolean close(String name) {

		OneCom c = getCon(name);
		if (c == null) {
			return false;
		}

		boolean ok = true;

		try {
			c.close();
		} catch (Exception e) {
			ok = false;
		}

		Object o = cons.remove(name);
		if (o == null) {
			throw new
					IllegalStateException("Nothing removed, name="
						+ name);
		}

		c = null;
		return ok;
	}

	private String status() {

		if (cons.isEmpty() ) {
			return "No tcp up";
		}

		StringBuffer b = new StringBuffer(100);
		Enumeration e = cons.elements();
		while (e.hasMoreElements() ) {
			OneCom c = (OneCom) e.nextElement();
			b.append(c.status() );
		}
		return b.toString();

	}


	public void registerCommands(Factory f, String prefix) {
			f.add(prefix + "add", new Add() );
			f.add(prefix + "cls", new Close() );
			f.add(prefix + "run", new Exec() );
			f.add(prefix + "l", new Status() );
	}


class Add extends Command {

		private String ip = null;
		private int port = -1;
		private String name;

		public Add() {
			setUsage("add ip port name");
		}

		public Command create(String[] args) {
			Command c = new Add();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			port = -1;
			ip = null;
			name = null;
		}

		public void execute() {
			if (isValid ) {
                            
                            add(ip, port, name);
                            // XXX Begin
                            /*
				if (PacketTransmitter.portIsValid(port) &&
						PacketTransmitter.isValid(ip) ) {
							boolean ok = add(ip, port, name);
							if (!ok) {
								setResult("Error connecting to host "
																				+ ip + ":" + port);
							} else {
								setResult("Connected OK");
							}

				} else {
					setResult("Invalid ip and/or port" );
				}
                            */    
                            // XXX End
                                
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length == PAR3 + 1 ) {
				ip = args[PAR1];
				port = ParseUtil.parseInt(args[PAR2]);
				name = args[PAR3];
				isValid = true;
			} else {
				isValid = false;
			}
		}
	}


class Exec extends Command {

		private String name;
		private String cmd;

		public Exec() {
			setUsage("run name cmd");
		}

		public Command create(String[] args) {
			Command c = new Exec();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			name = null;
			cmd = null;
		}

		public void execute() {
			if (isValid ) {
				if (env.getItemID() != -1) {
					//System.out.println("Env value added to command : " + env.getItemID() );
					cmd = cmd + " " + env.getItemID();
				} else {
					//System.out.println("No env, or env is -1");
				}

				String s = exec(name, cmd);
				if (s == null) {
					setResult("exe failed");
				} else {
					setResult(s);
					int id = ParseUtil.parseInt(s);
					env.setItemID(id);
				}
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length >= PAR2 + 1 ) {
				name = args[PAR1];
				StringBuffer b = new StringBuffer();
				b.append(args[PAR2]);
				for (int i = PAR2 +1; i < args.length;i++) {
					b.append(' ');
					b.append(args[i]);
				}

				cmd = b.toString();

				isValid = true;
			} else {
				isValid = false;
			}
	}
}
class Close extends Command {

		private String name;

		public Close() {
			setUsage("close name");
		}

		public Command create(String[] args) {
			Command c = new Close();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			name = null;
		}

		public void execute() {
			if (isValid ) {

				boolean ok = close(name);
				if (ok) {
					setResult("closed OK");
				} else {
					setResult("close Failed/was not open");
				}
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length == PAR1 + 1 ) {
				name = args[PAR1];
				isValid = true;
			} else {
				isValid = false;
			}
		}
	}

class Status extends Command {

		public Status() {
			setUsage("status");
		}

		public Command create(String[] args) {
			Command c = new Status();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				setResult(status() );
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}

}