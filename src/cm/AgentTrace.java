
package cm;

import java.util.Vector;
import agent.State;
import net.IPString;
import root.Node;

import agent.Agency;
import agent.GuestBook;
import agent.Stay;
import root.CycleVector;
import io.ProxyManager;
public class AgentTrace extends Vector {

	private State finalState = null;

	private CycleVector v;
	private final long home;
	private final int id;

	public AgentTrace(CycleVector v, long home, int id) {
		super();
		this.v = v;
		this.home = home;
		this.id = id;

		Node h = findNode(home);
		if (h == null) throw new IllegalStateException("home not found");
		addElement(h);


	}

	public State state() {
		return finalState;
	}

	public String aID() {
		return "(" + IPString.getAddressString(home) + "-" + id + ")";
	}


	//Trace agent starting at given node and follow it
	//as long as there are traces if it.
	public void trace() {

		Node current = (Node) elementAt(size() -1);

		if (current == null)
			throw new IllegalArgumentException("node null. not possible");

		if(finalState != null) {
			//System.out.println("cm.AgentTrace.traceFrom: Trace already completed");
			return;
		}

		//System.out.println("AgentTrace.trace, starting from node number=" + size());

		while(current != null) {
			Agency a = current.getAgency();
			GuestBook gb = a.getGB();

			//next location number must be next location in vector
			Stay s = gb.search(home,id,size());

			if (s != null) {

				long target = s.target();
				if (target != -1L) {
					current = findNode(target);
				} else {
					//At last node
					finalState = s.state();
					current = null;
				}
			} else {
				current = null;	//giving up - at least for now.
			}
			if (current != null) {
				addElement(current);
			}
		}
	}

	public boolean replace(long s, int id) {
		return s != this.home || this.id < id;
	}

	public boolean isTraceOf(long s, int id) {
		return s == this.home && this.id == id;
	}

	private Node findNode(long target) {
		if (target == -1L) return null;
		for (int i = 0; i < v.size(); i++) {
			Node n = (Node) v.elementAt(i);
			ProxyManager pm = n.getProxyManager();
			long sid = pm.getSid();
			if (sid == target) return n;
		}
		return null;
	}
}
