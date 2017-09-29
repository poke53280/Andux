
package agent;


import io.NodeSender;
import area.KeyArea;
import area.KeyAreaSet;
import net.NetMessage;
import area.Area;

public abstract class Agent {

	public static final int PATH_MAX = 20;	//75 works

	//Process agent visit verbosely
	public abstract boolean verbose();


	//Agent indicates that it has terminated,
	//and won't move any further.
	public abstract boolean terminated();

	//Process agent at node and return itself
	//packed to message for further propagation,
	//or return null.
	public abstract NetMessage tick();

	//The result of the agent's work. Exception if
	//called when not terminated(). 0 indicates a failure.
	public abstract long report();

	public abstract String status();

	public abstract int getType();

	public abstract int getID();

	//Number of hops the agent has done.
	//At 0, the agent has not moved.
	//A terminated agent at 0 hops
	//(terminated() && hops() == 0) indicates that
	//the agent couldn't even start. It is still
	//at its local node.
	public abstract int hops();

	public abstract long getHomeKey();

	//returns the destination of this agent, if
	//it has been sorted out, and is existing.
	//This is set to -1 at agent construction time.
	//It will thus *always* indicate *next* location, or -1:
	//It will never be not updated (still showing *this* location
	//as destination, e.g.)
	public abstract long destination();


	//Agent is at home (not started or back)
	public abstract boolean isHome();

}