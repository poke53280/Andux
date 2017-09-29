
package offline;

import root.Factory;
import root.Command;
import root.ParseUtil;

import world.IDDispenser;

import java.util.Hashtable;
import java.util.Enumeration;

public class UserVault {

	Hashtable users = new Hashtable();

	IDDispenser uid = new IDDispenser(0,1000000);

	public UserVault() {
		//System.out.println("UserVault()");
	}

	public void registerCommands(Factory f, String prefix) {
		f.add(prefix + "status", new Status() );
		f.add(prefix + "useradd", new UserAdd() );
		f.add(prefix + "userdel", new UserDel() );
		f.add(prefix + "login", new Login() );
		f.add(prefix + "getscore", new Score() );
		f.add(prefix + "getx", new GetX() );
		f.add(prefix + "gety", new GetY() );
		f.add(prefix + "save", new Save() );
	}


	protected synchronized int add(String name, String password) {
			Enumeration e = users.elements();
			while(e.hasMoreElements() ) {
					User u = (User) e.nextElement();
					if (u.isCalled(name) ) {
						return -1;
					}
			}
			int id = uid.get();
			User n = new User(name, password, id);
			users.put(new Integer(id), n);
			return id;
	}

	protected synchronized int login(String name, String password) {
			Enumeration e = users.elements();
				while(e.hasMoreElements() ) {
						User u = (User) e.nextElement();
						if (u.isCalled(name) && u.hasPassword(password ) ) {
								return u.getUID();
						}
			}
			return -1;
	}

	protected synchronized int del(String name, String password) {
		User delUser = null;
		Enumeration e = users.elements();
		while(e.hasMoreElements() ) {
				User u = (User) e.nextElement();
				if (u.isCalled(name) && u.hasPassword(password) ) {
					delUser = u;
					break;
				}
		}

		if (delUser == null) {
			return -1;
		} else {
			int id = delUser.getUID();
			users.remove(new Integer(id) );
			return id;
		}
	}

	protected synchronized int save(int x, int y, int score, int id) {
			User u = (User) users.get(new Integer(id) );
			if (u == null) {
				System.out.println("UserVault: User not found; " + id);
				return -1;
			} else {
				u.setStats(x,y,score);
				return 0;
			}

	}


	protected synchronized int getScore(int uid) {
		User u = (User) users.get(new Integer(uid) );
		if (u != null) {
			return u.getScore();
		} else {
			return -1;
		}
	}

protected synchronized int getX(int uid) {
		User u = (User) users.get(new Integer(uid) );
		if (u != null) {
			return u.getX();
		} else {
			return -1;
		}
	}

	protected synchronized int getY(int uid) {
			User u = (User) users.get(new Integer(uid) );
			if (u != null) {
				return u.getY();
			} else {
				return -1;
			}
	}



class Score extends Command {

		protected int id;

		public Score() {
			setUsage("score uid");
		}

		public Command create(String[] args) {
			Command c = new Score();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			id = -1;
		}

		public void execute() {
			if (isValid ) {
				setResult("" +getScore(id) );
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length != PAR1 + 1 ) {
				isValid = false;
			} else {
				id = ParseUtil.parseInt(args[PAR1]);
				if (id == -1) {
					isValid = false;
				} else {
					isValid = true;
				}
			}
		}

}

class GetX extends Score {

	public GetX() {
		super();
		setUsage("getx uid");
	}

	public Command create(String[] args) {
			Command c = new GetX();
			c.setArgs(args);
			return c;
	}

	public void execute() {
			if (isValid ) {
				setResult("" +getX(id) );
			} else {
				setResult(showUsage() );
			}
		}

}

class GetY extends Score {

	public GetY() {
		super();
		setUsage("gety uid");
	}

	public Command create(String[] args) {
			Command c = new GetY();
			c.setArgs(args);
			return c;
	}

	public void execute() {
			if (isValid ) {
				setResult("" +getY(id) );
			} else {
				setResult(showUsage() );
			}
		}

}

class Login extends Command {

		private String name;
		private String password;

		public Login() {
			setUsage("login name password");
		}

		public Command create(String[] args) {
			Command c = new Login();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			name = null;
			password = null;
		}

		public void execute() {
			if (isValid ) {
				int id = login(name, password);
				setResult("" + id);	//-1:Failed


			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length < PAR2 + 1 ) {
				isValid = false;
			} else {
				name = args[PAR1];
				password = args[PAR2];
				isValid = true;
				}
			}

		}


class UserAdd extends Command {

		private String name;
		private String password;

		public UserAdd() {
			setUsage("useradd name password");
		}

		public Command create(String[] args) {
			Command c = new UserAdd();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			name = null;
			password = null;
		}

		public void execute() {
			if (isValid ) {
				int id = add(name, password);
				setResult("" + id);

			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length < PAR2 + 1 ) {
				isValid = false;
			} else {
				name = args[PAR1];
				password = args[PAR2];
				isValid = true;
				}
			}

		}

class UserDel extends Command {

		private String name;
		private String password;

		public UserDel() {
			setUsage("userdel name password");
		}

		public Command create(String[] args) {
			Command c = new UserDel();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			name = null;
			password = null;
		}

		public void execute() {
			if (isValid ) {
				int id = del(name, password);
				setResult("" + id);
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length < PAR2 + 1 ) {
				isValid = false;
			} else {
				name = args[PAR1];
				password = args[PAR2];
				isValid = true;
				}
			}

		}

	class Save extends Command {

		private int x;
		private int y;
		private int score;
		private int id;

		public Save() {
			setUsage("save x y score id");
		}

		public Command create(String[] args) {
			Command c = new Save();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				int ret = save(x,y,score,id);
				setResult("" + ret);
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length != PAR4 + 1 ) {
				isValid = false;
			} else {
				x = ParseUtil.parseInt(args[PAR1] );
				y = ParseUtil.parseInt(args[PAR2] );
				score = ParseUtil.parseInt(args[PAR3] );
				id = ParseUtil.parseInt(args[PAR4] );
				if (x != -1 && y != -1 && score != -1 && id != -1) {
					isValid = true;
				} else {
					isValid = false;
				}
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
				setResult(getStatus());
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

		private String getStatus() {

			StringBuffer b = new StringBuffer(200);
			if (users.isEmpty() ) {
				b.append("no users registered");
			} else {

				Enumeration e = users.elements();
				while(e.hasMoreElements() ) {
					User u = (User) e.nextElement();
					b.append(u.status() );
					b.append('\n');
				}
			}
			return b.toString();
		}

	}
}

