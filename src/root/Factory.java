
package root;

import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Enumeration;

import cmd.Request;
import cmd.Environment;

public class Factory {

	protected Hashtable cmds = new Hashtable(10);
	protected String DEFAULT_COMMAND = "/uptime";

	public Factory() {
		add("/ls", new LS() );
		add("/monitor", new Monitor() );
		add("/pwd", new Pwd() );
		add("/cd", new Cd() );
	}

	public void add(String path, Command c) {
		cmds.put(path, c);
	}

	public void remove(String path) {
		Command c = (Command) cmds.remove(path);
		if (c == null) System.out.println("Factory.remove: Nothing removed: " + path);
	}


	public Command get(Request r) {
		if (r == null) throw new IllegalArgumentException("Request null");

		String[] req = r.getRequest();
		String s = null;

		if (req == null || req.length == 0) {
			return null;
		}

		if (!req[0].startsWith("/") ) {

			String r0 = req[0];
			//RELATIVE REFERENCE

			//1st: TRY '.'
			Environment e = r.getEnv();
			String absCommand = e.getPath() + r0;
			req[0] = absCommand;
			//System.out.println("added path:" + req[0] + "]");

			Command c = get(req);
			if (c != null) {
				//System.out.println("Found command at .");
				return c;
			}


			//2nd: TRY '/'
			//String binCommand = "/t/" + r0;

			String binCommand = "/" + r0;
			req[0] = binCommand;
			//System.out.println("added path:" + req[0] + "]");

			c = get(req);
			if (c != null) {
				//System.out.println("Found command at /t/");
				return c;
			} else {
				System.out.println("Didnt find command: '" + r0 + "'");
				return null;
			}


		} else {
			//ABSOLUTE REFERENCE
			return get(req);
		}


	}

	public Command get(String[] args) {

		if (args != null && args.length >= 1) {
			//System.out.println("Factory; Looking for command [" + args[Command.CMD] + "]");
			Command c = (Command) cmds.get(args[Command.CMD]);
			if (c != null) {
				return c.create(args);
			}
		}
		return null;
	}


	/*
		All-in-one solution, not used by main Andux system.
	*/
	public  String exec(String[] args) {

			Command c = get(args);

			if (c == null) {
				return "Command not found";
			} else {
				c.execute();
				String s = c.getResult();
				return s;
			}
	}

	public Command getDefault(String[] args) {
		Command c = (Command) cmds.get(DEFAULT_COMMAND);
		return c.create(args);
	}

	class LS extends Command {

		private String path;

		public LS() {
			setUsage("ls <path>");
		}

		public Command create(String[] args) {
			Command c = new LS();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (path != null) {
				//
			} else {
				path = env.getPath();
			}

			//System.out.println("Running ls with path =" + path);
			String s = list();
			setResult(s );

		}

		public void setArgs(String[] args) {
			init();
			if (args.length != PAR1 +1) {
				path = null;
			} else {
				path = args[PAR1];
			}
		}


		private String list() {
			Hashtable h = new Hashtable();
			StringBuffer b = new StringBuffer(500);
			if (!cmds.isEmpty() ) {
				Enumeration e = cmds.keys();

				while(e.hasMoreElements() ) {
					String p = (String) e.nextElement();

					if (p.startsWith(path) ) {

						//dont append the path:
						p = p.substring(path.length() );

						//cut behind first '/': this must be a directory
						p = cutToSlash(p);

						//check if already got this one:
						if (h.containsKey(p)) {
							//b.append("[duplicate: " + p + "]");
							//yes
						} else {
							b.append('\n');
							h.put(p,new Integer(1));
							b.append(p);
						}
					}

				}

				return b.toString();
			} else {
				return "\n(none)";
			}
		}

	}

	//input a string which may contain a path. Cut so
	//it doesnt do
	private String cutToSlash(String s) {
		if (s == null || s.equals("") ) return s; 	//no path

		int i = s.indexOf((int) '/');
		if (i == -1) return s;

		return s.substring(0,i+1);

	}

	class Monitor extends Command {

		private String cmd;
		private int tty;

		public Monitor() {
			setUsage("monitor <tty> <cmd>");
		}

		public Command create(String[] args) {
			Command c = new Monitor();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				setResult("Got command " + cmd + " for tty=" + tty);
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			if (args.length != PAR2 + 1) {
				isValid = false;
			} else {
				tty = ParseUtil.parseInt(args[PAR1] );
				cmd = args[PAR2];
				if (tty != -1 && cmd != null) {
					isValid = true;
				} else {
					isValid = false;
				}
			}

		}

	}

	class Pwd extends Command {

		public Pwd() {
			setUsage("pwd");
		}

		public Command create(String[] args) {
			Command c = new Pwd();
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
			setResult("[" + env.getPath() + "]");
		}
	}

	class Cd extends Command {

		String home = "/";

		String path = null;

		public Cd() {
			setUsage("cd");
		}

		public Command create(String[] args) {
			Command c = new Cd();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}


		public void setArgs(String[] args) {
			init();
			if (args.length == PAR1 + 1 && !args[PAR1].equals("") ) {
				path = args[PAR1];
			} else {
				path = home;
			}

			isValid = true;
		}

		private void upPath() {

			String p = env.getPath();
			if (p.equals("/") ) return;	//At root

			if (!p.endsWith("/") ) {
				System.out.println("path not ending with /, bad, skipped.");
				return;
			}

			int i = p.lastIndexOf('/', p.length() -2);
			env.setPath(p.substring(0,i+1) );

		}


		public void execute() {
			if (path.equals("..") ) {
				upPath();
				setResult("");
				return;
			}

			if (!path.endsWith("/") ) {
				path = path + "/";
			}

			if (path.startsWith("/")) {
				//System.out.println("cd: absolute path given");
			} else {
				//System.out.println("cd: relative path given");
				path = env.getPath() + path;
			}
			env.setPath(path);
			//setResult("cd: Changed path to [" + path + "]");
			setResult("");
		}
	}

}