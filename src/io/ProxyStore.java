package io;

import java.util.Hashtable;
import java.util.Enumeration;


import root.Factory;

import java.util.Vector;
import root.Command;

import root.SpaceMax;

import area.KeyAreaStore;
import area.WarpArea;

import area.Area;
import area.KeyArea;
import area.KeyAreaSet;

import net.IPString;
import java.util.Random;
import statistics.SampleProvider;

public class ProxyStore extends KeyAreaStore implements SampleProvider {

	private Factory f = null;
	private String prefix = null;
	private long localSid;

	static final int RMAX = 1000;

	//Keep at least LOW number of remote nodes updated.
	public static final int LOW = 2;

	private long tick = 0L;

    private final float[] randoms = new float[RMAX];
	private int counter;

	private int wrp = 0;						//number of warp links
	private int cls = 0;						//number of close links
	private int exi = 0;						//number of exile links
	private int ext = 0;						//number of external links

	public ProxyStore(Factory f, String prefix, long localSid) {
		super();
		this.f = f;
		this.prefix = prefix;
		this.localSid = localSid;

		Random r = new Random(System.currentTimeMillis() );
		for (int i = 0; i < RMAX; i++) {
			randoms[i] = r.nextFloat();
		}
		counter = (int) (Math.random()*RMAX);
	}

	public boolean isLow() {
		return size() <= LOW;
	}

	public float sampleValue() {
		return (float) size();
	}

	public Proxy get(long sid) {
		Long l = new Long(sid);
		return (Proxy) get(l);
	}

	public void registerCommands() {
		if (f == null) return;
		//...
	}


	//returns all nodes within NEAR distance from local
	public KeyAreaSet getNear(Area a) {
		if (isEmpty() ) return null;


		KeyAreaSet s = getSet();
		s.removeOutside(a);

		if (s.isEmpty() ) return null;
		else 			  return s;

	}


	public boolean isLocal(long sid) {
		return sid == localSid;
	}

	public long sinceCreated(long sid) {
		Proxy p = (Proxy) get(new Long(sid));
		if (p == null) {
			throw new IllegalArgumentException("proxy not found");
		}
		long created = p.tickCreated();
		return tick - created;
	}

	public KeyAreaSet getOutsideSet(Area a) {
		KeyAreaSet set = getSet();
		set.removeIntersects(a);
		set.removeExiled();		//Remove the exiled nodes from list, they may be new.

		return set;
	}

	public void notifyActivity(long sid) {
		Proxy p = (Proxy) get(sid);
		if (p == null) {
			System.out.println("ProxyStore: no proxy for sid");
		} else {
			p.notifyActivity();
		}
	}

	public ProxySet getSet() {
		return new ProxySet(this);
	}


	//remove warp setting on all proxies
	public void clearLinkStates() {
		if (isEmpty() ) return;
		Enumeration e = elements();
		while(e.hasMoreElements() ) {
			Proxy p = (Proxy) e.nextElement();
			p.setProxyState(null);
		}
	}


	//Update counts of all types of links.
	//Assumes that warp definitions in proxies are up to date.
	public void setState() {
		wrp = 0;						//number of warp links
		cls = 0;						//number of close links
		exi = 0;						//number of exile links
		ext = 0;						//number of external links

		if (isEmpty() ) return;

		//if low set low indicator...

		Enumeration e = elements();
		while(e.hasMoreElements() ) {
			Proxy p = (Proxy) e.nextElement();
			ProxyState s;
			if (p.isExiled() ) {
				s = ProxyState.EXI;
				exi++;
			} else if (p.proxyState() == ProxyState.WRP ) {	//already set to WRP
				s = ProxyState.WRP;
				wrp++;
			} else if (p.horizons() < SpaceMax.FAR) {
				s = ProxyState.CLS;
				cls++;
			} else {
				s = ProxyState.EXT;
				ext++;
			}
			p.setProxyState(s);
		}
	}

	public int wrp() { return wrp; }
	public int cls() { return cls; }
	public int exi() { return exi; }
	public int ext() { return ext; }


	public void runNodes(long tick) {

		this.tick = tick;
		if (isEmpty() ) return;

		Vector del = null;
		Enumeration e = elements();
		while(e.hasMoreElements() ) {
			Proxy p = (Proxy) e.nextElement();
			counter++;
			counter %= RMAX;
			float ran = randoms[counter];

			boolean delete = p.tick(ran);

			if (delete) {
				if (size() > LOW) {	//Will never delete beyond LOW
					if (del == null) del = new Vector();

					if (size() - del.size() > LOW) {	//already remaining must be bigger than LOW
						del.addElement(p);
					}
				}
			}
		}

		if (del != null) {
			//System.out.println("del " + del.size() + " proxies");
			while (!del.isEmpty() ) {
				Proxy p = (Proxy) del.elementAt(0);
				del.removeElementAt(0);
				delete(p);
				p.deregisterCommands(f, prefix + p.getID() + "/");
			}
		}
	}


	public void runLow() {
		//If low on nodes  send a little from time to time
		//to receive updates back.
		if (size() > LOW) return;	//We are not lonely

		Enumeration e = elements();
		while(e.hasMoreElements() ) {
			Proxy p = (Proxy) e.nextElement();
			counter++;
			counter %= RMAX;
			float ran = randoms[counter];
			p.pingSend(ran);
		}

	}


	public void runWarps(WarpArea[] wa) {

		//run first occurence of each desired warp area, if any
		//found. Picking 'first' should be reasonably stable at one
		//particular warp proxy if several are existant. No problems
		//anticpated of altering warp proxies are returned.

		Vector v = getWarps(wa);
		if (v == null) return;

		for (int i = 0; i < v.size(); i++) {
			Proxy p = (Proxy) v.elementAt(i);
			counter++;
			counter %= RMAX;
			float ran = randoms[counter];

			p.setProxyState(ProxyState.WRP);
			p.pingSend(ran);	//Perhaps send an area output,
											//and always notifyActivty to prevent deletion

		}
	}


	public Vector getWarps(WarpArea[] wa) {
		if (wa == null || wa.length == 0) return null;

		//Picking 'first' should be reasonably stable at one
		//particular warp proxy if several are existant. No problems
		//anticpated of altering warp proxies are returned.

		Vector v = null;
		for (int i = 0; i < wa.length; i++) {
			Proxy p = (Proxy) firstInside(wa[i]);
			if (p != null) {
				//This is a desired warp link that we want to keep
				if (v == null) v = new Vector();
				v.addElement(p);
			}
		}
		return v;
	}


	public String listWarps(WarpArea[] wa) {
		Vector v = getWarps(wa);
		if (v == null) return "warp links: none";
		StringBuffer b = new StringBuffer(v.size()*20);
		b.append("\nwarp links:");
		for (int i = 0; i < v.size(); i++) {
			b.append('\n');
			Proxy p = (Proxy) v.elementAt(i);
			b.append(p.getOverview() );
		}
		return b.toString();

	}



	public String list() {
			StringBuffer b = new StringBuffer(500);


			if (!isEmpty() ) {

				Enumeration e = elements();

				while(e.hasMoreElements() ) {
					Proxy p = (Proxy) e.nextElement();
					b.append(p.getOverview() );
					b.append('[');
					b.append('P');
					b.append(p.getID() );
					b.append(']');
					b.append('\n');
				}
			 	if (size() > 10) {
					b.append("In total " + size() + " nodes");
				}
			} else {
				b.append("OFFLINE");
			}
			return b.toString();
		}


	public void add(Proxy p) {

		long sid = p.getKey();

		if (has(sid) ) throw new IllegalStateException("Has node, sid=" + sid);

		if (f != null) {
			//System.out.println("ProxyStore: registering commands for new proxy");
			p.registerCommands(f, prefix + p.getID() + "/");
		}
		put(new Long(sid), p);
	}





}