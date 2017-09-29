
package root;



//NOT TESTED

import java.util.Vector;

//Maintains a current location in a vector of
//elements, cycling when request to previous() to
//first element and when next() while on last element.

//should survive vectors growing and shrinking, but
//with undefined current assignment after shrinking.


public class CycleVector extends Vector {

	private int pos = -1;

	public CycleVector() {
		super();
	}

	public Object current() {
		if (isEmpty() ) return null;

		if (pos == -1) {
			//first assignment
			pos = 0;
		} else if(pos >= size() ) {	//items have been deleted
			pos = size() -1;	//lastpos
		}
		return elementAt(pos);
	}

	//The index of the current element.
	//-1 if never assigned. Undefined if vector empty
	public int pos() { return pos; }


	public Object next() {
		if (isEmpty() ) return null;

		if (pos == -1) {
			//first assignment
			pos = 0;
		} else if(size() > pos+1) {
			//trivial case
			pos++;
		} else if (size() == 1) {
			//nothing to rotate
		} else {
			//wrap
			pos = 0;
		}
		return elementAt(pos);

	}


	public Object previous() {
		if (isEmpty() ) return null;
		if (pos == -1) {
			//first assignment
			pos = 0;
		} else if(pos > 0) {
			//trivial case
			pos--;
		} else if (size() == 1) {
			//nothing to rotate
		} else {
			//wrap
			pos = size() -1;
		}
		return elementAt(pos);

	}




}

