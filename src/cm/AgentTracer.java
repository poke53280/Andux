
package cm;

import root.CycleVector;
import root.Node;

import agent.Agency;
import agent.GuestBook;
import agent.Stay;
import agent.State;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import io.ProxyManager;

public class AgentTracer {

	CycleVector v;
	int MAX = 3;
	AgentTrace[] at = new AgentTrace[MAX];
	int next = 0;	//next to be replaced


	public AgentTracer(CycleVector v) {
		this.v = v;
	}

	public AgentTrace[] getTrace() {
		return at;
	}

	private void addNew() {

		Node current = (Node) v.current();
		if (current == null) return;


		Agency a = current.getAgency();

		int last = a.lastID();
		if (last < 0) return;	//no runs started

		ProxyManager pm = current.getProxyManager();
		long home = pm.getSid();


		boolean isRunning = false;
		for(int i = 0; i < MAX; i++) {
			AgentTrace t = at[i];
			if (t != null && t.isTraceOf(home,last) ) {
				isRunning = true;
				break;
			}
		}

		if (isRunning) {
			//newest agent run is already running.
			return;

		}


		AgentTrace old = at[next];

		if (old == null || old.replace(home,last) ) {	//trace is old, replace
			old = new AgentTrace(v,home,last);
			at[next] = old;
			next++;
			next %= MAX;
		}
	}

	public int getNext() { return next; }

	public void clear() {
		for(int i = 0; i < MAX; i++) {
			at[i] = null;
		}
		next = 0;
	}

	public void update() {
		addNew();
		for(int i = 0; i < MAX; i++) {
			AgentTrace t = at[i];
			if (t != null) t.trace();
		}
	}

	public String list() {
		return "AgentTracer.list() called";
	}

}