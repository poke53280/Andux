package io;

import java.util.Vector;

import root.WorldSize;

import net.MessageSocket;

import net.MessageIO;

import net.IPString;
import root.Command;

import root.Factory;
import root.ParseUtil;
import net.NetMessage;
import dispatch.Dispatcher;
import dispatch.Port;

import area.AreaProvider;
import area.Area;
import area.KeyArea;
import area.KeyAreaSet;
import area.WarpArea;

import area.WarpDesire;

import root.SpaceMax;

import statistics.SampleProvider;

public class ProxyManager implements NodeSender {

	private int nextID = 0;

	protected MessageIO net = null;

	protected Factory f = null;

	protected String prefix;

	protected ProxyStore nodeStore = null;

	private AreaProvider ap = null;

	private int losePromille = WorldSize.DROP_PACKETS;

	private KeyArea localKeyArea;

	private Dispatcher d;
	private int lastRouted;
	private long tick;

	private WarpDesire wd;

    public ProxyManager(String prefix, Factory f,
    							AreaProvider ap, MessageIO net) {

    	this.prefix = prefix;

		this.f = f;

		this.wd = new WarpDesire(ap);
		this.wd.registerCommands(prefix + "wd/",f);

		this.nodeStore = new ProxyStore(f, prefix, net.getLocalSID() );

		if (ap == null) throw new IllegalArgumentException("ap is null");
		this.ap = ap;
		this.net = net;
		if (net == null) throw new IllegalArgumentException("net is null");

		localKeyArea = new KeyArea(net.getLocalSID(),ap.getArea() );
		//needs updates to be good.

		registerCommands();

		d = new Dispatcher(prefix + "d/",f);
		d.registerCommands();


	}

	public String getInputStatus() {
		return d.overview().toString();
	}



	public long getSid() {
		return net.getLocalSID();
	}


	public KeyAreaSet getNear() {
		return nodeStore.getNear(ap.getArea() );
	}

	public void route() {

		lastRouted = 0;

		NetMessage m = net.poll();

		while (m != null) {

			long sid = m.getSocket();
			addProxy(sid);
			d.dispatch(m);
			lastRouted++;
			m = net.poll();
		}
	}


	public void setLosePromille(int i) {
		if (i < 0 || i > 1000) {
			System.out.println("ProxyManager.setLosePromille:Bad data, not setting");
			return;
		}
		losePromille = i;
	}

	public int getLosePromille() {
		return losePromille;
	}

	public void contactPrevious() {
		long sid = net.getPrevious();
		contact(sid);
	}

	public Dispatcher getDispatcher() { return d; }

	public Area getLocalArea() {
		return ap.getArea();
	}

	//Description of local node: sid and area
	public KeyArea getKeyArea() {
		//sid already in place
		localKeyArea.setTo(ap.getArea() );
		return localKeyArea;
	}

	public ProxyStore getNodeList() {
		return nodeStore;
	}

	public void doIO(long tick) {

		this.tick = tick;

		nodeStore.clearLinkStates();		//now, no proxies are warp

		wd.update();
		WarpArea[] warps = wd.getDesiredWarps();  //set of desired warps

		nodeStore.runWarps(warps);		//pick a warp set for this tick, mark and run them

		nodeStore.runLow();						//if low, also running warps never necessary

		nodeStore.runNodes(tick);

		nodeStore.setState();


	}

	public WarpArea[] getDesiredWarps() {
		return wd.getDesiredWarps();
	}


	public Vector getCurrentWarps() {
		WarpArea[] dw = getDesiredWarps();
		Vector v = nodeStore.getWarps(dw);
		return v;
	}


	public WarpArea getAnyMissingWarp() {

		WarpArea[] warps = wd.getDesiredWarps();
		if (warps == null) {
			//System.out.println("WM.getNeeded: warps not running");
			return null;
		}

		for(int i = 0; i < warps.length; i++) {
			WarpArea wa = warps[i];
			int numberInside = nodeStore.numberInside(wa);
			if(numberInside == 0) {
				return wa;
			}
		}
		return null;
	}


	public SampleProvider getSampleProvider() {
		return nodeStore;
	}


    public void stop() {
		if (net != null) {
	        net.end();
		}
		//XXX
		net.close();
		net = null;
    }

	/* returns null if sid is local, else returns the proxy, old or new */
	public Proxy contact(long sid) {
		if (nodeStore.has(sid)) {
			return getProxy(sid);
		} else {
			Proxy p = addProxy(sid);
			return p;	//if null, tried to contact self
		}
	}

	public Proxy getProxy(long sid) {
		return nodeStore.get(sid);
	}

	private Proxy addProxy(long sid) {

		long localSID = net.getLocalSID();

		if (localSID == sid) {
			System.out.println("PM.addProxy: Cannot create proxy to self");
			return null;
		}

		Proxy p = nodeStore.get(sid);

		if (p == null ) {
			Wire w = new Wire(net, sid);

			w.setLoseRate(losePromille);

			p = new Proxy(nextID,w, ap, w.getSID(), tick );

			nextID++;
			nodeStore.add(p);

		}
		return p;

	}

	public String listWarps() {

		wd.update();
		WarpArea[] warps = wd.getDesiredWarps();
		if (warps == null) return "warp system not running";

		StringBuffer b = new StringBuffer(15*warps.length);

		for(int i = 0; i < warps.length; i++) {
			b.append('\n');


			WarpArea wa = warps[i];
			b.append(wa.state());

			int numberInside = nodeStore.numberInside(wa);
			b.append("# proxies inside=" + numberInside);
		}

		b.append("\n--------------------------");

		String s = nodeStore.listWarps(warps);


		return b.toString() + s;
	}



	private void registerCommands() {
		if (f != null) {
			f.add(prefix + "status", new Status() );
			f.add(prefix + "contact", new Contact() );
			f.add(prefix + "wlist", new WList() );
			nodeStore.registerCommands();
		}

	}


	public String list() {
		return nodeStore.list();
	}

	class Status extends Command {

		public Status() {
			setUsage("status (PM)");
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
				setResult(getStatus() + "\n" + list() );
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}

	public String getStatus() {
		if (net == null) {
			return "NOT CONNECTED";
		}
		String nType = "LO";
		long localSID = net.getLocalSID();
		String s = nType + IPString.getAddressString(localSID);

		return s;
	}


	public String getName() {
		if (net == null) {
			return "NOT CONNECTED";
		}
		long localSID = net.getLocalSID();
		return IPString.getAddressString(localSID);
	}



	public int considerContact(long sid, boolean verbose) {

		if (nodeStore.isLocal(sid)) {
			throw new IllegalArgumentException("home is never a remote node");
		}

		if ( !nodeStore.has(sid) ) {
			//The node is not known locally, and considered a success.
			//The ProxyStore is informed immediately

			contact(sid);
			return 1;
		}

		//Agent accredited succees if the node link has just been brought up.

		long sinceCreated = nodeStore.sinceCreated(sid);
		if (sinceCreated <= 1 ) {
			 if (verbose) System.out.println("Proxy created within last 1 ticks - considered success");
			return 1;
		} else {
			//XXX If known, but far away - send to this node. Position may have changed,
			//and this agent may have been successful still..

			//known node
			Proxy p = nodeStore.get(sid);
			if (p == null) throw new IllegalStateException("known/not known node, sid=" + sid);

			if (p.isExiled() ) {
				//XXX CHECK THIS CASE, SHOULD SEND INITSENDS IF RETURNED?
				if (verbose) System.out.println("Evaluator:Agent returned locally exiled node, refueling initsends");
				p.loadInitSends(3, verbose);
				return 1;
			} else {
				float d = p.horizons();
				if (verbose) System.out.println("Evaluator:Agent returned known node, currently at horizon distance d = " + d);

				if (d > SpaceMax.FAR) {
					//tell this node to start sending a little to see if they have moved.
					if (verbose) System.out.println("Evaluator:Distance 0: Loading initsends into proxy");
					p.loadInitSends(3,verbose);
					return 1;
				} else {
					return 0;
				}

			}

		}

	}


	class Contact extends Command {

		private String ip = null;
		private int port = -1;

		public Contact() {
			setUsage("contact {ip} {port}");
		}

		public Command create(String[] args) {
			Command c = new Contact();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			port = -1;
			String ip = null;
		}

		public void execute() {
			if (isValid ) {
				if (IPString.portIsValid(port) &&
						IPString.isValid(ip) ) {

					long sid = -1L;
					try {
						sid = IPString.getSID(ip, port);
					} catch (IllegalArgumentException e) { ; }

					if (sid != -1) {
						contact(sid);
						setResult("Node contacted:" + IPString.getAddressString(sid));
					} else {
						setResult("Failed to connect" );
					}
				} else {
					setResult("Invalid ip and/or port" );
				}
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length == PAR2 + 1 ) {
				ip = args[PAR1];
				port = ParseUtil.parseInt(args[PAR2]);
				isValid = true;


				//System.out.println("PM.contact PAR1 = '" + ip + "'");
				//System.out.println("PM.contact PAR2 = '" + port + "'");


			} else {
				isValid = false;
			}
		}
	}
	class WList extends Command {

		public WList() {
			setUsage("wlist");
		}

		public Command create(String[] args) {
			Command c = new WList();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			setResult(listWarps() );
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}

}