
package io;

import area.KeyAreaSet;

import java.util.Hashtable;
import java.util.Enumeration;

public class ProxySet extends KeyAreaSet {

	public ProxySet(Hashtable t) {
		super(t);
	}


	public int removeEXT() {
		if (size() == 0) return 0;

		int removed = 0;

		for(int i = 0; i < size(); i++) {

			Proxy p = (Proxy) elementAt(i);
			ProxyState s = p.proxyState();
			if (s == ProxyState.EXT) {
				removeElementAt(i);
				i--;
				removed++;
			}

		}
		if (verbose) System.out.println("KeyAreaSet.removeEXT removed:  " + removed);
		return removed;
	}

/*
	public int countCLS() {
		if (size() == 0) return 0;

		int removed = 0;

		for(int i = 0; i < size(); i++) {
			Proxy p = (Proxy) elementAt(i);
			ProxyState s = p.proxyState();
			if (s == ProxyState.CLS) {
				removed++;
			}
		}
		return removed;
	}

	public int countWRP()
*/


}