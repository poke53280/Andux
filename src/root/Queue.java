
package root;

import java.util.Vector;

public class Queue extends Vector {

	public Object poll() { 	//returns null if none

		if (isEmpty()  ) return null;

		Object o = (Object) firstElement();
		removeElement(o);
		return o;

	}

    public void push(Object o) {
        addElement(o);
    }

	public void clear() {
		setSize(0);
	}

}
