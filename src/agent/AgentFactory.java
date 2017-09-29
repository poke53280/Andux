
package agent;

import io.ProxyStore;
import io.ProxyManager;
import io.NodeSender;
import io.ProxySet;
import net.NetMessage;

import area.AreaProvider;
import area.KeyAreaSet;
import area.KeyArea;
import area.WarpArea;
import root.SpaceMax;
import net.IPString;
import area.Area;

/*
	The Factory is asked to create a new Agent of specified type. The agent will
	receive data on birth, and will then start to travel on the network. When it
	leaves StatMan takes not of its ID and create time, and will maintain a status
	for the agent, also retired agents, until the info is explicitly deleted.

*/

public class AgentFactory {

	private ProxyStore s;
	private long sid;
	private AreaProvider ap;
	private ProxyManager pm;

	private boolean verbose = false;

	private int nextID = 0;

	public AgentFactory(long sid, AreaProvider ap,
								ProxyManager pm) {

		this.s = pm.getNodeList();
		this.sid = sid;
		this.ap = ap;
		this.pm = pm;
	}

	//Visitor
	public Agent create(NetMessage m) {

		int t = m.peekLastInt();

		KeyArea local = new KeyArea(sid,ap.getArea() );

		//EXP:
		KeyAreaSet set = createLocalSet();


		Agent a = null;

		if (t == Type.PROXAGENT)  a = new ProxAgent(m,set,local );
		if (t == Type.WARPAGENT)  a = new WarpAgent(m,set,local);

		if (a == null) throw new IllegalArgumentException("Unknown agent type: " + t);

		NetMessage.freeInstance(m);

		if (a.hops() == 0) {
			if (a.getHomeKey() != sid) {
				throw new IllegalStateException("Agent at hops== 0, but not local,type=" +t);
			}
		}

		return a;
	}

	public String location() {
		return IPString.getAddressString(sid);
	}

	public void setVerbose(boolean b) {	verbose = b; }

	//Birth
	public Agent create() {
		int type = Type.anyType();

		return create(type);
	}

	private KeyAreaSet createLocalSet() {

		ProxySet set = s.getSet();		//All local proxies

		int cls = s.cls();
		int wrp = s.wrp();

		if (cls + wrp >= ProxyStore.LOW) {
			set.removeExiled();				//Never send to exiled links
			set.removeEXT();				//Never send to external links
		}

		return set;						//Set is now all CLS and WRP links.

	}


	//Birth.
	//May return null if couldn't create agent of given type
	public Agent create(int type) {

		KeyArea local = new KeyArea(sid,ap.getArea() );

		if (type == Type.PROXAGENT ) {

			int id = nextID;
			nextID++;
			KeyAreaSet set = createLocalSet();
			return new ProxAgent(set,local,id, verbose );
		}

		if (type == Type.WARPAGENT) {
			WarpArea wa = pm.getAnyMissingWarp() ;

			if (wa == null) {
				//Register this some place
				//Failed to create WarpAgent-got no warp
				return null;
			} else {
				int id = nextID;
				nextID++;

				KeyAreaSet set = createLocalSet();
				return new WarpAgent(id,local,set,wa,verbose);
			}
		}

		throw new IllegalArgumentException("Unknown agent type: " + type);

	}

	public int lastID() {
		return nextID -1;
	}



}