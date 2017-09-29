
package agent;

import dispatch.Port;
import net.NetMessage;

import area.Area;
import area.KeyArea;
import area.KeyAreaSet;
import area.WarpArea;
import net.IPString;
import root.SpaceMax;

class WarpAgent extends Agent {

	//protected final int PATH_MAX = 15;
	protected int nodeNumber = 1;
	protected long[] path = new long[PATH_MAX];

	protected final int id;
	protected final KeyArea home;
	protected KeyAreaSet localSet = null;
	protected final int flags;
	protected final KeyArea localNode;
	protected boolean initTermination = false;
	private WarpArea wa = null;
	private long resNode = 0L;

	protected long destination = -1L;

	public WarpAgent(int id, KeyArea home, KeyAreaSet localSet, WarpArea w, boolean verbose) {
		this.id = id;
		this.home = new KeyArea(home.getKey(), (Area) home);
		this.localNode = new KeyArea(home.getKey(), (Area) home);

		this.localSet = localSet;

		if (verbose) {
			flags = 1;
		} else {
			flags = 0;
		}

		if (w == null)
			throw new IllegalStateException("Found no warp location");

		wa = w;

		path[0] = home.getKey();

	}


	public WarpAgent(NetMessage m, KeyAreaSet localSet, KeyArea local) {
		m.chopInt();

		this.id = m.chopInt();
		this.resNode = m.chopLong();
		this.flags = m.chopInt();
		this.wa = m.chopWarpArea();
		this.home = m.chopKeyArea();
		this.localSet = localSet;
		this.localNode = local;

		int arraySize = m.chopArray(path);
		nodeNumber = arraySize +1;
		path[nodeNumber-1] = localNode.getKey();

	}

	public long getHomeKey() {
		return home.getKey();
	}

	public boolean verbose() { return flags == 1; }

	public String status() {
		return "WarpAgent.status.Home=" + home.status()
				+ "\n    warparea:" + wa.state() +"local:" + localNode.status()
				+ "\n path " + pathContent();
	}

	protected String pathContent() {
		StringBuffer b = new StringBuffer(20*(nodeNumber +1));
		for (int i = 0; i < nodeNumber; i++)  {
			b.append('\n');
			b.append("node # " + i + ":");
			b.append(IPString.getAddressString(path[i]));
		}
		return b.toString();
	}


	public long destination() { return destination; }

	public int getType() { return Type.WARPAGENT; }

	public int getID() { return id; }

	public NetMessage tick() {

		if (nodeNumber > PATH_MAX) {
			throw new IllegalStateException("node# too large");
		}

		if (nodeNumber == PATH_MAX) {
			if (verbose()) System.out.println("on last giving up");
			return null;
		}

		if (nodeNumber == 1) {
			NetMessage m = proceed();
			if (m == null) {
				initTermination = true;
				return null;
		 	}
			return m;
		}

		if (isHome() ) {
			if (verbose()) System.out.println("back home!");

			//checks:

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

		boolean intersects = wa.intersects(localNode);
		if (verbose() ) {
			if (intersects) System.out.println("--INTERSECTION");
			else			System.out.println("--NO INTERSECTION");
		}

		if (intersects) {
			if (resNode != 0L) throw new IllegalStateException("resNode already set");
			resNode = localNode.getKey();

			return moveTo(home.getKey() );
		} else {
			return proceed();
		}

	}

	public boolean terminated() {
		boolean isBackHome = (isHome() && nodeNumber > 1);
		return isBackHome || initTermination;
	}


	private NetMessage proceed() {
		localSet.remove(home);	//don't go home unless has success
		localSet.remove(path,nodeNumber);	//don't go to a place already seen
		if(localSet.isEmpty() ) {
			if (verbose() ) System.out.println("no node found");
			return null;
		}

		if (SpaceMax.levels() == 0) {
			System.out.println("WarpAgent operation in non-warp running system");
			return null;
		}

		Area warpTarget = new Area ((int) wa.getX(), (int) wa.getY() );
		int localWarpShare = localNode.commonLevel(warpTarget);
		if (verbose() ) System.out.println("warp agent to local:" + localWarpShare);
		//propose a remote node if can find one at least at this level of
		//proximity.

		KeyArea maxFit = localSet.getWarpClose(warpTarget, localWarpShare+1);

		if (maxFit != null) {
			if (verbose() ) System.out.println("Found WL1+ node :" + maxFit.state() );

			long target = maxFit.getKey();
			return moveTo(target);

		}


		if (localWarpShare > 0) {

			maxFit = localSet.getWarpClose(warpTarget, localWarpShare);

			if (maxFit != null) {
				if (verbose() ) System.out.println("Found WL0+ node :" + maxFit.state() );
				long target = maxFit.getKey();
				return moveTo(target);

			} else {
				if (verbose() ) System.out.println("Found no WL0,WL1 node");
			}
		} else {
			if (verbose() ) System.out.println("Found no WL1node, WL0 is lvl 0 so skipping");
		}


		//Couldnt find one using the warp system. Returning random node
		KeyArea ia = localSet.any();
		if (verbose() ) System.out.println("Found ANY node :" + ia.state() );
		long target = ia.getKey();
		return moveTo(target);
	}

	private NetMessage moveTo(long target) {
		if (target == -1L)
			throw new IllegalArgumentException("bad destination");


		destination = target;

		NetMessage m = NetMessage.getInstance();
		m.init(Port.AGENT);


		m.append(path,nodeNumber);

		m.append(home);
		m.append(wa);
		m.append(flags);
		m.append(resNode);
		m.append(id);
		m.append(getType() );

		m.setSocket(target);

		return m;
	}

	public boolean isHome() {
		return home.getKey() == localNode.getKey();
	}

	public long report() {
		if (!terminated() ) throw new IllegalStateException("agent not terminated");
		if (resNode == 0L && !initTermination) throw new IllegalStateException("no result exists");
		return resNode;
	}


	public int hops() { return nodeNumber -1; }


}