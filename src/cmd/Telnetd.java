
package cmd;

import java.net.ServerSocket;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.Socket;

import root.Command;
import root.Factory;
import root.ParseUtil;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Observable;

import root.SynchedQueue;
import net.IPString;




//import ParseUtil;

public class Telnetd extends Thread implements StatusReader {

	private int port = 0;
	private String welcome;
	private int termID = 0;

	private Hashtable terminals;

	private ServerSocket listener = null;

	private SynchedQueue requests = new SynchedQueue();

	private Factory f = null;

	private String username = null;
	private String password = null;


	public Telnetd(String welcome) {

		super();
		this.welcome = welcome;
		terminals = new Hashtable();

	}

	public void setPort(int port) {
		this.port = port;
	}


	public void startConsole(String prompt) {
		ConsoleCom c = new ConsoleCom(termID, "console", requests, prompt);
		c.setIO();
		addCom(c);
	}

	public DisplayCom startDisplayCom() {
		DisplayCom c = new DisplayCom(termID, "display", requests);
		addCom(c);
		return c;

	}


	public void startFile(String name) throws Exception  {

		FileCom f = new FileCom(termID, welcome, requests);

		try {
			f.setIO(name);
		} catch (Exception e) {
			f = null;
			System.out.println("Telnetd.startFile: Error " + e.getMessage() );
		} finally {
			if (f != null) {
				addCom(f);
				//System.out.println("File opened OK: " + name);
			} else {
				System.out.println("Failed to open file: " + name);
				throw new Exception("Bad file name");
			}
		}

	}

	private void addCom(AbstractCom c) {
		terminals.put(new Integer(termID), c);
		termID++;
		c.start();
	}


	public DataHolder startLive(CommandSource b) {
		SingleCom c = new SingleCom(b,termID, "live-" + b.desc(), requests);
		c.setPeriod(1000);
		addCom(c);
		return c.getModel();
	}



	public void connect(int p) {
		setPort(p);
		start();
	}


	public void run() {


		if (port == -1) {
			throw new IllegalStateException("Port not defined, cannot start");
		}

		try {
			listener = new ServerSocket(port);
		} catch (IOException ie) {
			System.out.println("Telnetd: Couldn't create server socket");
		}

		while (listener != null) {
			try {
				final Socket s = listener.accept();

				ShellCom c = new ShellCom(termID, "shell", requests, username, password);

				try {
					c.setIO(s);
				} catch (IOException ie) {
					c.close();
					c = null;
				} finally {
					if (c != null) {
						addCom(c);
					} else {
						System.out.println("Failed to connect to telnet client");
					}

				}

			} catch (IOException ie) {
				System.out.println("Couldnt receive on socket");
			} catch (NullPointerException ne) {
				System.out.println("Telnetd.listener - abnormal exit?");
			}
		}
		System.out.println("Telnetd listen thread exiting");
	}


	public void close() {
		killShells();


		if (listener != null) {
			System.out.println("Telnetd.close(): Closing listener..:");
			try {
				listener.close();
			} catch (IOException ie) {
				//
			}
			listener = null;
			System.out.println("Telnetd.close(): Closed");

		}

		//System.out.println("telnetd closed and finished on port " + port);

	}


	public void putResponse(Request r) {

		int id = r.getShellID();

		AbstractCom c = (AbstractCom) terminals.get(new Integer(id) );

		if (c == null) {
			System.out.println("Terminal, id=" + id
					+ " not found, can't return result");
			return;
		} else {
			c.setResponse(r);
		}
	}



	private void killShells() {
		removeDead();
		//System.out.println("killShells() ");
		//System.out.println("Prekill: Number of shells in hash: "
		//								+ terminals.size() );

		Enumeration e = terminals.keys();
		while (e.hasMoreElements() ) {
			Integer i = (Integer) e.nextElement();
			killOne(i);
		}

		//System.out.println("Telnetd.killShells(): Killed shells completed");
		terminals.clear();

		//System.out.println("Telnetd.killShells() completed");

	}

	private void killOne(Integer i) {
		AbstractCom c = (AbstractCom) terminals.get(i);
		if (c == null) {
			System.out.println("killOne, com not found");
			return;
		}

		if (c.isRunning() ) {
			int id = i.intValue();
			System.out.println("closing shell " + id );
			c.close();
			System.out.println("closed: " + id);
		} else {
			System.out.println("removing dead shell " + i.intValue() );
		}

		terminals.remove(i);

	}

	private Request getRequest() {
		removeDead();

		if (!requests.isEmpty() ) {
			return (Request) requests.pop();
		} else {
			return null;
		}
	}


	public void registerCommands(Factory f, String prefix) {

		this.f = f;
		f.add("/finger", new Finger() );	//AT ABSOLUTE ROOT
		f.add(prefix + "connect", new Connect() );
		f.add(prefix + "open", new Open() );

	}

	public void execute() {

		if (f == null) {
			throw new IllegalStateException("Telnetd.execute: No factory");
		}

		Request r = getRequest();

		if (r != null) {

			Command c = f.get(r);
			if (c == null) {
				r.setResponse("command not found");
			} else {
				Environment e = r.getEnv();
				c.setEnv(e);
				c.execute();
				String s = c.getResult();
				r.setResponse(s);
			}

			putResponse(r);
			r = null;
		} else {
			//NORMAL - NOTHING QUEUED

		}

	}

	class Connect extends Command {

		private int port;

		public Connect() {
			setUsage("connect {port} {username} {password}");
		}

		public Command create(String[] args) {
			Command c = new Connect();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			port = -1;
		}

		public void execute() {
			if (isValid ) {
				if (!IPString.portIsValid(port) ) {
					setResult("Port out of legal range");
				} else {
					connect(port);
					setResult("Connected to port " + port + " OK");
				}

			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length != PAR3 + 1 ) {
				isValid = false;
			} else {
				port = ParseUtil.parseInt(args[PAR1]);
				if (port == -1) {
					isValid = false;
				} else {
					isValid = true;
				}
				username = args[PAR2];
				password = args[PAR3];

			}

		}
	}


	class Open extends Command {

		String filename = null;

		public Open() {
			setUsage("open {filename}");
		}

		public Command create(String[] args) {
			Command c = new Open();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			filename = null;
		}


		public void setArgs(String[] args) {

			init();

			if (args.length != PAR1 +1) {
				isValid = false;

			} else {
				filename = args[PAR1];
				if (filename == null
					|| filename.equals("")
					|| filename.startsWith(".")
					|| filename.startsWith("/") 	) {
					isValid = false;
				} else {
					isValid = true;
				}
			}
		}

		public void execute() {
			if (isValid) {

				try {
					//shell.startFile(filename);
			 		startFile(filename);
			 		setResult("File started OK");
			 	} catch (Exception e) {
					setResult("File not run: " + e.getMessage() );
				}
			} else {
				setResult(showUsage() );
			}

		}
	}




	class Finger extends Command {

		public Finger() {
			setUsage("finger");
		}

		public Command create(String[] args) {
			Command c = new Finger();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

		public void execute() {
			setResult(getStatus() );
		}
	}

	private String getStatus() {
		String out = "\nLogin	Tty		Office		Idle";
		Enumeration e = terminals.keys();
		while (e.hasMoreElements() ) {

			Integer i = (Integer) e.nextElement();

			AbstractCom c = (AbstractCom) terminals.get(i);

			if (c.isRunning() ) {
				out = out + "\n";
				String s = c.terminalStatus();
				out = out + s;
			}
		}
		return out;
	}

	private void removeDead() {
		Enumeration e = terminals.keys();
		while (e.hasMoreElements() ) {
			Integer i = (Integer) e.nextElement();

			AbstractCom c = (AbstractCom) terminals.get(i);

			if (!c.isRunning() ) {
				//System.out.println("Removing dead shell: id=" + c.getID() );
				terminals.remove(i);
			}
		}
	}



	public static void main (String argv []) {

		Telnetd server = new Telnetd("\nAndux v 0.2\n");
		server.setPort(5556);

		server.start();

		while (server != null) {

			Request r = server.getRequest();
		    String s = null;
			if (r != null) {
					s = "Command not found";
					r.setResponse(s);
					server.putResponse(r);
					r = null;
			}

			try {
				sleep(200);
			} catch (InterruptedException e) {
				System.out.println("Telnetd.main: Interrupted in sleep");
			}
		}
	}

}