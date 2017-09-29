
package agent;

import area.Area;
import net.NetMessage;
import area.KeyAreaSet;
import area.KeyArea;
import dispatch.Port;
import net.IPString;
import root.SpaceMax;

public class ProxAgent extends Agent {

	protected KeyAreaSet localSet = null;
	protected int nodeNumber = 1;
	protected final int id;
	protected final int flags;
	protected final KeyArea home;
	protected final KeyArea localNode;
	protected long[] path = new long[PATH_MAX];

	protected long destination = -1L;

	//Known nodes from home. But their location is not sent, just sids.
	//Using the location of home in their KeyArea representations.
	protected KeyAreaSet mySids  = new KeyAreaSet();
	protected boolean initTermination = false;
	private final int MAX_SIDS = 20;
	private long resNode = 0L;

	//Birth
	public ProxAgent(KeyAreaSet localSet,
			KeyArea localKeyArea, int id, boolean verbose) {

		this.localNode = localKeyArea;
		this.home = new KeyArea(localNode.getKey(), (Area) localNode);
		this.id = id;

		this.localSet = localSet;

		if (verbose) {
			flags = 1;
		} else {
			flags = 0;
		}
		localSet.setVerbose(verbose() );

		path[0] = home.getKey();

	}

	//Resurrection
	public ProxAgent(NetMessage m, KeyAreaSet localSet,
						KeyArea localKeyArea) {

		this.localSet = localSet;
		m.chopInt();
		this.localNode = localKeyArea;

		this.flags = m.chopInt();
		this.resNode = m.chopLong();
		this.id = m.chopInt();

		this.home = m.chopKeyArea();

		mySids.unpackKeys(m,(Area) home);


		int arraySize = m.chopArray(path);
		nodeNumber = arraySize +1;

		path[nodeNumber-1] = localNode.getKey();

		localSet.setVerbose(verbose() );

	}

	public long getHomeKey() {
		return home.getKey();
	}

	public int getID() {
		return id;
	}

	//Pre: Agent state is up-to-date at this local node.
	public NetMessage tick() {

		if (nodeNumber > PATH_MAX) {
			throw new IllegalStateException("node# too large");
		}

		if (nodeNumber == 1) {
			return onInit();
		}

		if (nodeNumber == 2) {
			//return onDefault();
			return onSecond();
		}

		if (isHome() ) {
			return onBackHome();
		}

		if (nodeNumber == PATH_MAX -1) {
			//XXX TRIM PATH? contents matters, not
			//necessarily sequence.

			//return onSecondLast();
			return onDefault();
		}

		if (nodeNumber == PATH_MAX) {
			//XXX TRIM PATH?
			return onUltimate();
		}

		return onDefault();

	}

	public boolean verbose() {
		return flags == 1;
	}

	public boolean terminated() {
		boolean isBackHome = (isHome() && nodeNumber > 1);
		return isBackHome || initTermination;
	}


	public boolean isHome() {
		return home.getKey() == localNode.getKey();
	}



	public int getType() { return Type.PROXAGENT; }




	protected NetMessage onSecond() {
		//as onDefault, but never go home with success.
		if (verbose()) System.out.println("onSecond - proceeding no matter");
		Long l = getOftenClose();
		return goTo(l);

	}


	protected NetMessage onDefault() {
		if (verbose()) System.out.println("onDefault");


		//local is known, proceed no matter overlap or not
		if (mySids.containsKey(localNode.getKey() ) ) {
			if (verbose() ) System.out.println("Node is known. Proceeding.");
			Long l = getOftenClose();
			return goTo(l);
		}


		float proximity = localNode.horizons(home);
		if (verbose() ) System.out.println("ProxAgent: proximity = " + proximity);
		//possible trashing problem induced here
		boolean overlaps = (proximity < SpaceMax.FAR);

		if (overlaps) {

			if(verbose() ) System.out.println("Overlap. Returning.");

			if (resNode != 0L) throw new IllegalStateException("resNode already set");
			resNode = localNode.getKey();

			return moveTo(home.getKey());


		} else {
			if (verbose() ) System.out.println("Node unknown but far away. Proceeding.");
			Long l = getOftenClose();
			return goTo(l);
		}

	}

	protected NetMessage onInit() {
		if (verbose() ) System.out.println("onInit");

		//Long l = getOftenClose();		//post:far away nodes (from local) is cut from localset

		Long l = anywhere();		//Anywhere first jump to get away. Then approach.



		//Remove some far away nodes before removing randomly - if there are enougth
		//close left (70% full of close ones)
		if (localSet.size() > MAX_SIDS) {
			int distant = localSet.countDistant(home,SpaceMax.FAR);
			int close = localSet.size() - distant;

			if (close > (int) (.5f + 0.7f*MAX_SIDS) ) {
				int removed = localSet.removeDistant(home, SpaceMax.FAR);
				if (verbose() ) System.out.println("onInit: Removed "
									+ removed + " distant nodes from mySid");
			}
		}


		int cut = localSet.cutRandomTo(MAX_SIDS);	//removes random known nodes, if needed

		if (verbose() ) {

			if (cut == 0) {
				System.out.println("onInit: No nodes cut before install to mySids");
			} else {
				System.out.println("onInit: Nodes cut before install to mySids: " + cut);
			}
		}


		mySids.add(localSet);			//handles empty case, too.


		if (verbose()) {
			System.out.println("onInit: installing these mySids to agent:\n");
			System.out.println(mySids.show() );
		}

		NetMessage m = goTo(l);	//if l is null (no place to go, m will be null, too)
		if (m == null) {
			initTermination = true;
			if (resNode != 0L) throw new IllegalStateException("result already set");
		}
		return m;
	}

	public long destination() { return destination; }


	protected NetMessage onBackHome() {
		if (verbose() ) System.out.println("onBackHome");
		if (!terminated() ) throw new IllegalStateException("agent not terminated");

		if (initTermination ) {
			if (resNode != 0L)
				throw new IllegalStateException("init termination, but got result");
		} else {
			if (resNode == 0L) {
				throw new IllegalStateException("back home, but no results");
			}
		}
		return null;
	}

	protected NetMessage onUltimate() {
		if (verbose() ) System.out.println("onUltimate-giving up");
		return null;
	}

	protected String pathContent() {
		StringBuffer b = new StringBuffer(20*(nodeNumber +1));
		for (int i = 0; i < nodeNumber; i++)  {
			b.append(',');
			b.append("[#" + i + ":");
			b.append(IPString.getAddressString(path[i]));
			b.append(']');

		}
		return b.toString();
	}

	public int hops() {
		return nodeNumber -1;
	}

	protected NetMessage goTo(Long l) {	//Handles null address, too.

		if ( l == null) {
			if (verbose()) {
				System.out.println("goTo - no place found: terminating");
			}
			return null;
		} else {
			long lv = l.longValue();
			if (verbose() ) {
				System.out.println("goTo - found target: " +
					IPString.getAddressString(lv) );
			}
			return moveTo(lv);
		}
	}

	private NetMessage moveTo(long sid) {

		if (sid == -1L)
			throw new IllegalArgumentException("bad destination");


		destination = sid;

		NetMessage m = NetMessage.getInstance();
		m.init(Port.AGENT);
		m.append(path,nodeNumber);

		mySids.packKeys(m);

		m.append(home);

		m.append(id);
		m.append(resNode);

		m.append(flags);

		m.append(getType() );
		m.setSocket(sid);
		return m;
	}

	private void trimLocalSet() {
		localSet.remove(path,nodeNumber);
		localSet.removeExiled();
	}

	private void removeMySids() {
		KeyAreaSet sidRemoved = localSet.remove(mySids);
		if (sidRemoved != null) {
			if (verbose() ) {
				System.out.println("Removed these nodes from localset because found in mySids:");
				System.out.println(sidRemoved.show() );
			}
		}
	}


	private Long anywhere() {
		trimLocalSet();
		if (localSet.isEmpty() ) return null;

		KeyArea k = localSet.any();
		if (k == null) throw new IllegalStateException("got no element");
		if (verbose() ) System.out.println("anywhere:Found ANY node : " + k.status() );

		return k.getKeyObj();

	}


	private Long getOftenClose() {

		trimLocalSet();	//remove places can never go: path, exiled

		//XXX NOT SURE WHETHER TO DO THIS OR NOT
		//a mySid node is never a solution, but is it better to go here
		//than nowhere? PERHAPS NOT. It can be visited directly from home.
		removeMySids();


		if (localSet.isEmpty() ) return null;

		int distantCount = localSet.countDistant((Area) home, SpaceMax.FAR);

		if (localSet.size() - distantCount > 0) {
			//Found close node(s), use one.
			localSet.removeDistant((Area) home,SpaceMax.FAR);	//Remove those far away
			KeyArea k = localSet.any();
			if (k == null) throw new IllegalStateException("got no element");

			if (verbose() ) System.out.println("ProxAgent-CLS:" + k.status() );
			return k.getKeyObj();
		}

		if (SpaceMax.levels() > 0) {
			int localWarpShare = localNode.commonLevel(home);
			if (verbose() ) System.out.println("warp prox to local:" + localWarpShare);
			//propose a remote node if can find one at least at this level of
			//proximity.

			KeyArea maxFit = localSet.getWarpClose(home, localWarpShare+1);

			if (maxFit != null) {
				if (verbose() ) System.out.println("ProxAgent-WL1+:" + maxFit.status() );
				return maxFit.getKeyObj();
			}


			if (localWarpShare > 0) {

				maxFit = localSet.getWarpClose(home, localWarpShare);

				if (maxFit != null) {
					if (verbose() ) System.out.println("ProxAgent-WL0:" + maxFit.status() );
					return maxFit.getKeyObj();
				} else {
					if (verbose() ) System.out.println("Found no WL0,WL1 node");
				}
			} else {
				if (verbose() ) System.out.println("Found no WL1node, WL0 is lvl 0 so skipping");
			}

		}

		KeyArea k = localSet.any();
		if (k == null) throw new IllegalStateException("got no element");
		if (verbose() ) System.out.println("Found ANY node : " + k.status() );

		return k.getKeyObj();

	}


	public String status() {
		StringBuffer b = new StringBuffer(20*(nodeNumber +1));

		b.append("\ntype =        " + Type.desc(getType()) );
		b.append("\nid =          " + id);
		b.append("\nresnode =     " + IPString.getAddressString(resNode) );
		b.append("\nflags =       " + flags);
		b.append("\nhome-area =   " + home.status() );
		b.append("\nlocalNode =   " + localNode.status() );
		b.append("\nnode number = " + nodeNumber);
		b.append("\n-------------------path--------------------\n");
		b.append(pathContent() );
		b.append("\n-------------------mySid-------------------\n");
		b.append(mySids.listKeys() );
		b.append("\n-------------------------------------------\n");
		return b.toString();
	}

	public long report() {
		if (!terminated() ) throw new IllegalStateException("agent not terminated");
		if (resNode == 0L && !initTermination) throw new IllegalStateException("no result exists");
		return resNode;
	}

}