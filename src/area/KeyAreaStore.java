package area;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import net.IPString;

public class KeyAreaStore extends Hashtable {


	private Vector tmp = null;

	public KeyAreaStore() {
		super();
	}

	public void put(KeyArea a) {
		KeyArea overwritten = (KeyArea) put(a.getKeyObj(), a);
		if (overwritten != null) {
			throw new IllegalArgumentException("KeyAreaStore.put: Object overwritten");
		}
	}

	public void delete(KeyArea a) {
		if (isEmpty() )
			throw new IllegalArgumentException("not found:" + a.status() );

		KeyArea del = (KeyArea) remove(a.getKeyObj() );
		if (del == null) throw new IllegalArgumentException("not found:" + a.status() );

	}

	public boolean has(long key) {
		return (get(new Long(key) )) != null;
	}

	public KeyArea getAny() {

		if (isEmpty() ) return null;

		int index = (int) Math.round(Math.random()*(size()-1 ) );

		Enumeration e = elements();
		KeyArea p = null;
		do {
			p = (KeyArea) e.nextElement();
			index--;
		} while (index >= 0);

		if (p == null) {
			System.out.println("Found no element");
			return null;
		} else {
			return p;
		}

	}

	//returns number of elements with  centerpoints inside given WarpArea
	public int numberInside(WarpArea wa) {

		if (isEmpty()) return 0;

		int counter = 0;
		Enumeration e = elements();
		while(e.hasMoreElements() ) {
			KeyArea k = (KeyArea) e.nextElement();
			if(wa.containsCenterOf(k)) counter++;
		}
		return counter;
	}

	//Returns first occurence of an element inside given WarpArea
	//Fragile ordered delivery.
	protected KeyArea firstInside(WarpArea wa) {
		if (isEmpty()) return null;
		Enumeration e = elements();
		while(e.hasMoreElements() ) {
			KeyArea k = (KeyArea) e.nextElement();
			if(wa.containsCenterOf(k)) return k;
		}
		return null;
	}



}