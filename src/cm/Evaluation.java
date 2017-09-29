
package cm;

import area.KeyArea;
import area.KeyAreaSet;
import io.ProxyManager;
import io.ProxyStore;

import dispatch.Dispatcher;
import root.Node;

public class Evaluation {

	KeyAreaSet exact;
	KeyAreaSet local;
	KeyArea current;
	KeyAreaSet accurateSet;
	Node n;

	private int good;		//Local connections, needed for consistency
	private int overHead;	//Local connections, not needed
	private int missing;	//Needed for consitency, not existant
	private int accurate;	//Local connections, needed and accurate


	private int userCount;
	private int sysCount;

	public Evaluation(KeyAreaSet exact, Node n) {

		this.exact = exact;
		this.n = n;
		ProxyManager p = n.getProxyManager();
		ProxyStore s = p.getNodeList();
		this.local = s.getSet();

		this.current = n.getKeyArea();
	}

/*
	public Evaluation(KeyAreaSet exact,
				KeyAreaSet local, KeyArea current) {

		this.exact = exact;
		this.local = local;
		this.current = current;
	}
*/

	public void eval() {

		exact.remove(current);
		exact.removeOutside(current);

		int perfects = exact.size();
		int known = local.size();

		KeyAreaSet goodSet = exact.remove(local); //good is now what has been removed

		missing  = exact.size();
		good     = perfects - missing;
		overHead = known - good;

		if (goodSet != null) {
			accurate = goodSet.checkAccuracy(local);
		} else {
			accurate = 0;
		}
		evalTraffic();
	}

	private void evalTraffic() {

		ProxyManager pm = n.getProxyManager();
		Dispatcher d = pm.getDispatcher();

		sysCount = d.sysCount();
		userCount = d.userCount();

	}



	public String status() {
		return "missing,good,overhead: "
			+ missing + "," + good + "," + overHead;
	}

	public int getGood() 	 { return good; }
	public int getAccurate() { return accurate; }
	public int getMissing()  { return missing; }
	public int getOverHead() { return overHead; }

	public int sysCount() { return sysCount; }
	public int userCount() { return userCount; }


	public KeyAreaSet getMissingSet() {
		return exact;
	}

	public KeyAreaSet getGoodSet() {
		return local;
	}

	public KeyAreaSet getAccurateSet() {
		return accurateSet;
	}

	public boolean isOffline() {
		return good == 0 && overHead == 0;
	}

	public boolean isComplete() {
		return missing == 0 && good == accurate;
	}
}