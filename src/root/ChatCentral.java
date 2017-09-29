
package root;


import dispatch.InHandler;
import net.NetMessage;

import area.KeyAreaSet;

import io.Proxy;
import root.Factory;
import root.Command;

import net.IPString;

public class ChatCentral implements InHandler {

	private Node n;

	private int MAX = 10;
	private ChatLine[] com = new ChatLine[MAX];
	int counter = 0;	//always ready for insert


	public ChatCentral(Node n)  {
		this.n = n;

		long localSid = n.getKeyArea().getKey();


		for(int i = 0; i < MAX; i++) {
			com[i] = new ChatLine(localSid);
		}
	}

	//Chat is part of user application (not system)
	public boolean isSystem() { return false; }

	public int counter() { return counter; }
	public ChatLine[] getData() { return com; }


	public void registerCommands(Factory f, String prefix) {
		if (f == null) return;
		f.add(prefix + "say", new Say() );
		f.add(prefix + "list", new List() );
	}


	public void input(NetMessage m) {
		long sid = m.getSocket();
		String msg = m.chopString();

		NetMessage.freeInstance(m);

		//System.out.println("Chatcentral.input: Received msg=" + msg);

		ChatLine cl = com[counter];
		cl.set(sid,msg);
		counter++;
		counter%=MAX;

	}

	public void autoSay() {
		//Skip if noone NEAR
		KeyAreaSet s = n.getNear();
		if (s == null || s.isEmpty()) {		//ALONE, not sent
			return;
		}

		//We have listeners. Create a message
		long localSid = n.getKeyArea().getKey();
		//String myCall = IPString.getAddressString(localSid) + " " + System.currentTimeMillis();

		String myCall = "t=" + System.currentTimeMillis();	//Just anything new and local
		output(myCall,s);
	}

	public void output(String msg) {
		if(msg == null || msg.length() < 1) return;

		KeyAreaSet s = n.getNear();
		if (s == null || s.isEmpty()) {		//ALONE, not sent
			//System.out.println("ChatCentral.output: ALONE");
			ChatLine cl = com[counter];
			cl.set("(ALONE)" + msg);	//local
			counter++;
			counter%=MAX;
		} else {
			//System.out.println("ChatCentral.output: SENDING");
			output(msg,s);
		}
	}

	private void output(String msg, KeyAreaSet s) {
		if (msg == null || msg.length() < 1 || s == null || s.isEmpty() )
			throw new IllegalArgumentException("message and or NEAR nodes empty or null");


		ChatLine cl = com[counter];
		cl.set(msg);	//local
		counter++;
		counter%=MAX;

		for(int i = 0; i < s.size(); i++) {
			Proxy p = (Proxy) s.elementAt(i);
			p.sendChat(msg);
		}
	}



	String list(int n) {

		StringBuffer b = new StringBuffer(n*20);

		int index = counter;
		for(int i = 0; i < n; i++) {
			index--;
			if (index < 0) index += MAX;

			b.append('\n');
			b.append(com[index].show());
		}
		return b.toString();
	}

	public String list() {
		return list(MAX);
	}

	public String desc() { return "chatcentral"; }

	class Say extends Command {

		String msg = null;

		public Say() {
			setUsage("say <msg>");
		}

		public Command create(String[] args) {
			Command c = new Say();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			msg = null;
		}

		public void setArgs(String[] args) {
			init();
			if (args.length != PAR1 +1) {
				isValid = false;

			} else {
				msg = args[PAR1];
				isValid = true;
			}
		}

		public void execute() {
			if (isValid) {
				output(msg);
				setResult("said ok");
			} else {
				setResult(showUsage() );
			}
		}
	}

	class List extends Command {

		public List() {
			setUsage("list");
		}

		public Command create(String[] args) {
			Command c = new List();
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
			if (isValid) {
				setResult(list() );
			} else {
				setResult(showUsage() );
			}
		}
	}

}