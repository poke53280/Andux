
package area;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import net.NetMessage;
import net.IPString;

import root.SpaceMax;
import root.WorldSize;

public class KeyAreaSet extends Vector {

	protected boolean verbose = false;

	public KeyAreaSet(Hashtable t) {
		super();
		if (t.isEmpty() ) {
			return;
		}

		Enumeration e = t.elements();
		while(e.hasMoreElements() ) {
			KeyArea a = (KeyArea) e.nextElement();
			addElement(a);
		}
	}

	//An empty set
	public KeyAreaSet() {
		super();
	}

	public void setVerbose(boolean b) {
		verbose = b;
	}


	public void add(KeyArea a) {
		addElement(a);
	}

	public void add(KeyAreaSet s) {
		if (s.isEmpty() ) return;

		for(int i = 0; i < s.size(); i++) {
			KeyArea a = (KeyArea) s.elementAt(i);
			add(a);
		}
	}


	public boolean containsKey(long key) {
		if (isEmpty() ) return false;
		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			long _key = a.getKey();
			if (_key == key) return true;
		}
		return false;

	}


	public String show() {
		if (isEmpty() ) return "vector null or empty";

		StringBuffer b = new StringBuffer(size() *50);
		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			b.append('\n');
			b.append(a.status() );
		}
		return b.toString();

	}

	public String listKeys() {
		if (isEmpty() ) return "vector null or empty";
		StringBuffer b = new StringBuffer(size() *20);
		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			b.append(',');
			b.append(a.getKeyString() );
		}
		return b.toString();
	}



	public KeyArea any() {
		if (isEmpty() ) {
			if (verbose) System.out.println("KeyAreaSet.any(): Empty");
			return null;
		}

		//if (verbose) System.out.println("KeyAreaSet.any(): " + size() + " candidate(s)");

		int index = (int) Math.round(Math.random()*(size()-1 ) );

		KeyArea k = (KeyArea) elementAt(index);
		//if (verbose) System.out.println("KeyAreaSet.any(): Returning: " + k.status());

		return k;
	}

	public void remove(long[] in, int max) {
		if (isEmpty() ) {
			return;	//nothing to remove
		}

		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			long key = a.getKey();
			if(isInList(key,in,max) ) {
				//remove
				removeElementAt(i);
				i--;
			}
		}
	}


	//If many, just removes one occurence
	public void remove(KeyArea k) {
		remove(k.getKey() );
	}


	//f many, just removes one occurence
	public void remove(long l) {
		if (isEmpty() ) {
			return;	//nothing to remove
		}
		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			long key = a.getKey();
			if(key == l) {
				removeElementAt(i);
				return;
			}
		}

	}

	public KeyAreaSet remove(KeyAreaSet s) {
		if (isEmpty() || s.isEmpty() ) {
			return null; //nothing to remove, or asked to be removed
		}

		KeyAreaSet removed = null;

		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			long key = a.getKey();
			if (s.hasKey(key) ) {
				if(removed == null) removed = new KeyAreaSet();
				removed.add(a);
				removeElementAt(i);
				i--;
			}
		}
		return removed;
	}


	//b true: Return the removed elements.
	//b false: Return the elements in input set that caused the local remove
	public KeyAreaSet remove(KeyAreaSet s, boolean b) {
		if (isEmpty() || s.isEmpty() ) {
			return null; //nothing to remove, or asked to be removed
		}

		KeyAreaSet removed = null;

		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			long key = a.getKey();
			KeyArea _a = s.get(key);

			if (_a != null ) {	//We will remove
				if(removed == null) removed = new KeyAreaSet();
				removeElementAt(i);
				i--;
				if (b) 	removed.add(a);
				else 	removed.add(_a);
			}
		}
		return removed;
	}



	public int checkAccuracy(KeyAreaSet approx) {

		if(approx == null || approx.isEmpty() ) {
			//No approx elements given
			return 0;
		}

		if (isEmpty() ) {
			return 0;
		}

		int accurate = 0;

		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			long key = a.getKey();
			KeyArea _a = approx.get(key);
			if (_a == null) {
				throw new IllegalStateException("No approximation found");
			} else {
				int d = _a.centerDistance(a);
				if (d <= WorldSize.POS_DEVIATION) accurate++;
			}
		}
		return accurate;
	}


	//returns accurate nodes
	//keeps inaccurate nodes
	//removes nodes not found in exact

	public KeyAreaSet removeAccurate(KeyAreaSet exact) {
		if (isEmpty() || exact == null || exact.isEmpty() ) {
			return null; //nothing to remove, or asked to be removed
		}

		KeyAreaSet removed = null;
		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			long key = a.getKey();
			KeyArea _a = exact.get(key);

			if (_a == null) {	//not found in exact, remove it
				removeElementAt(i);
				i--;
			} else {
				int d = _a.centerDistance(a);
				if (d <= WorldSize.POS_DEVIATION) {
					//accurate. Remove and store
					if(removed == null) removed = new KeyAreaSet();
					removeElementAt(i);
					i--;
					removed.add(a);
				} else {
					//inaccurate, dont' do anything.
				}
			}

		}
		return removed;

	}



	private static boolean isInList(long sid, long[] list, int counter) {
		for(int i = 0; i < counter; i++) {
			if (sid == list[i]) return true;
		}
		return false;
	}

	public boolean hasKey(long key) {
		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			long _key = a.getKey();
			if (_key == key) return true;
		}
		return false;
	}

	public KeyArea get(long key) {
		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			long _key = a.getKey();
			if (_key == key) return a;
		}
		return null;
	}

	//Also removing self from set if found, BUT NOT EXILED
	public int removeDistant(Area in, float d) {
		if (isEmpty() ) return 0;

		int removed = 0;

		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			long key = a.getKey();
			if (a == in) {
				//System.out.println("(Skipping self)");
				removeElementAt(i);
				removed++;
				i--;
				continue;
			}
			if (a.horizons(in) > d && !a.isExiled() )  {
				removeElementAt(i);
				removed++;
				i--;
			}
		}
		if (verbose) System.out.println("KeyAreaSet.removeDistant(d=" + d + ") removed " + removed);

		return removed;
	}

	//never counting self, if found. and NOT EXILED
	public int countDistant(Area in, float d) {
		if (isEmpty() ) return 0;

		int removed = 0;

		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			long key = a.getKey();
			if (a == in) {
				continue;
			}
			if (a.horizons(in) > d && !a.isExiled() )  {
				removed++;
			}
		}
		return removed;
	}

	//closest to in in set,null if none are closer than d
	public KeyArea getClose(Area in, float d) {
		if (in == null || d <= 0f)
			throw new IllegalArgumentException("bad input");

		if (isEmpty() ) return null;

		KeyArea closest = null;
		float bestDistance = 0f;

		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);

			float distance = a.horizons(in);
			if (distance < d) {
				if (closest == null || distance < bestDistance) {
					closest = a;
					bestDistance = distance;
				}
			}
		}
		return closest;
	}

	//Best warp with respect to in, better than or equal to level

	public KeyArea getWarpClose(Area home, int level) {

		if (level <= 0) throw new IllegalArgumentException("level <= 0");
		if (SpaceMax.levels() == 0) throw new IllegalArgumentException("SpaceMax.levels() == 0");

		if (isEmpty() ) return null;

		SpaceMax.recalc(home);
		int[] _in = SpaceMax.getRoute();	//the position of input area

		//copying the route, before it is overwritten
		int[] in = new int[SpaceMax.levels()];
		for(int i = 0; i < _in.length; i++) {
			in[i] = _in[i];
		}

		int maxCommon = 0;
		KeyArea maxFit = null;

		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			SpaceMax.recalc(a);
			int common = SpaceMax.commonLevels(in);

			if (common >= maxCommon) {
				maxCommon = common;
				maxFit = a;
			}

		}
		if (maxCommon >= level) {
			return maxFit;	//may be null (none found)
		} else {
			return null;
		}

	}




	public int removeOutside(Area in) {
		if (isEmpty() ) return 0;

		int removed = 0;

		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			if (a == in) {
				continue;
			}
			if (!a.intersects(in) ) {
				removeElementAt(i);
				removed++;
				i--;
			}
		}
		return removed;
	}


	public int intersectCount(Area in) {

		if (isEmpty() ) return 0;
		int count = 0;
		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			if (a == in) {
				continue;
			}
			if (a.intersects(in) ) {
				count++;
			}
		}
		return count;
	}


	public int removeIntersects(Area in) {
		if (isEmpty() ) return 0;

		int removed = 0;

		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			//long key = a.getKey();
			if (a == in) {
				removeElementAt(i);
				i--;
				removed++;
			}
			if (a.intersects(in) ) {
				removeElementAt(i);
				i--;
				removed++;
			}
		}
		return removed;
	}


	public int removeExiled() {
		if (size() == 0) return 0;

		int removed = 0;

		for(int i = 0; i < size(); i++) {
			Area a = (Area) elementAt(i);
			if (a.isExiled() ) {
				removeElementAt(i);
				i--;
				removed++;
			}
		}
		if (verbose) System.out.println("KeyAreaSet.removeExiled removed:  " + removed);
		return removed;
	}

	public int countExiled() {
			if (size() == 0) return 0;

			int removed = 0;

			for(int i = 0; i < size(); i++) {
				Area a = (Area) elementAt(i);
				if (a.isExiled() ) {
					removed++;
				}
			}
			return removed;
	}



	//Just keep the f first elements, f=0.0..1.0 of the whole set
	public void cutBack(float f) {
		if (f < 0f || f > 1f)
			throw new IllegalArgumentException("f=" + f);

		if (size() < 2 || f ==1f) return;

		int max = (int) (.5 + f*size() );

		if(max ==0) max++;	//Never remove all


		//System.out.println("KeyAreaSet:Cutting beyond max = " + max);
		cutBack(max);

	}

	//Cutting from behind until size() is max.
	public void cutBack(int max) {
		if (max < 0) throw new IllegalArgumentException("bad new length");
		if (size() <= max) return;

		while (size() > max) {
			removeElementAt(size() -1);
		}
	}


	//Cutting any element until size() is max
	//Returns how many are cut away
	public int cutRandomTo(int max) {
		if (max < 0) throw new IllegalArgumentException("bad new length");
		if (size() <= max) return 0;

		if (verbose) System.out.println("KeyAreaSet.cutRandomTo: " + size() + "->" + max);


		int cut = 0;
		while (size() > max) {
			int index = (int) Math.round(Math.random()*(size()-1 ) );
			removeElementAt(index);
			cut++;
			//System.out.println("removed element at " + index);
		}
		return cut;
	}

	public void unpack(NetMessage m) {
		int size = m.chopInt();

		for (int i = 0; i < size; i++) {
			KeyArea a = m.chopKeyArea();
			add(a);
		}

	}


	public void pack(NetMessage m) {
		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			m.append(a);
		}
		m.append(size() );

	}


	public void packKeys(NetMessage m) {
		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			long key = a.getKey();
			m.append(key);
		}
		m.append(size() );

	}

	public void unpackKeys(NetMessage m, Area area) {

		int size = m.chopInt();
		//System.out.println("unpackKeys: found size = " + size);

		for (int i = 0; i < size; i++) {
			long key = m.chopLong();
			KeyArea a = new KeyArea(key, area);
			add(a);
		}

	}

	public void listDistanceTo(KeyArea k) {

		if (isEmpty() ) {
			System.out.println("set is empty - no list");
		}

		for(int i = 0; i < size(); i++) {
			KeyArea a = (KeyArea) elementAt(i);
			System.out.println(a.status() + " d= " + a.centerDistance(k) );
		}
	}


	private static int d(int index, Area k, Vector v) {
		return k.centerDistance((KeyArea) v.elementAt(index));
	}


	//Smallest first
	public void sort(Area k) {
		quicksort(0,size()-1,k);
	}

	public int firstElementDistance(Area k) {
		if (isEmpty() ) throw new IllegalStateException("Set is empty");

		int d = d(0,k,this);

		if (verbose) System.out.println("KeyAreaSet:first node has distance:" + d);

		return d;
	}


	private void quicksort(int L, int R, Area k) {

		float m = (d(L,k,this) + d(R,k,this))/2;

		int i=L;
		int j=R;
		KeyArea temp;

		do {
			while (d(i,k,this)<m) i++;
			while (d(j,k,this)>m) j--;

			if (i<=j) {
				temp = (KeyArea) elementAt(i);
				setElementAt( (KeyArea) elementAt(j), i);
				setElementAt(temp,j);
				i++;
				j--;
			}
		} while (j>=i);

		if (L<j) quicksort(L,j,k);
		if (R>i) quicksort(i,R,k);

	}

	public static void main(String[] args) {

		KeyAreaSet s1 = new KeyAreaSet();
		s1.add(new KeyArea(1) );
		s1.add(new KeyArea(2) );

		System.out.println(s1.show() );

		//get any
		System.out.println("any()");

		s1.setVerbose(true);
		KeyArea k = s1.any();
		System.out.println(k.status() );

	}

}