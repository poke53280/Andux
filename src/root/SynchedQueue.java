
package root;

import java.util.Vector;

public class SynchedQueue extends Vector {

    protected int itemcount;

    public SynchedQueue() {
        super();
        itemcount = 0;
    }


    public synchronized Object pop() {
        Object   item = null;
        // If no items available, block in wait() call
        if (itemcount == 0) {
            //System.out.println("Queue.pop: Queue is empty, blocking on wait");
            try {
                wait();
            } catch (InterruptedException e) {
               //System.out.println("Woken up");
            }
        }

        item = (Object) firstElement();
        removeElement(item);

        itemcount--;

        return item;
    }

    /**
     * Inserts an Object at the end of the Queue.
     *
     * @param o Object to be placed on stack.
     */

    public synchronized void push(Object o) {
        itemcount++;
        addElement(o);
        notify();
    }

	public synchronized void clear() {
		setSize(0);
	}


}
