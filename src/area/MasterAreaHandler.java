
package area;

import dispatch.InHandler;
import net.NetMessage;

import io.ProxyStore;
import io.Proxy;

public class MasterAreaHandler implements InHandler {

	private ProxyStore s;

	public MasterAreaHandler(ProxyStore s) {
		this.s = s;
	}

	//Area messaging is part of the system
	//(not user application)
	public boolean isSystem() { return true; }

	public String desc() { return "areahandler"; }

	public void input(NetMessage m) {

		long socket = m.getSocket();

		Proxy p = s.get(socket);

		if (p== null)
			throw new IllegalStateException("no proxy found");

		int y = m.chopInt();
		int x = m.chopInt();

		p.move(x,y);
		NetMessage.freeInstance(m);



		//consider reply (if this receive was unexpected), but
		//never more than in 10% of cases.

		//EXPERIMENTAL

		if (Math.random() < 0.5) {
			p.considerReply();
		}


	}

}