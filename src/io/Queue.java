package io;
import java.util.Vector;

/**
 * General class that implements a queue extending a Vector with
 * thread-safe push and pop methods.
 * <P>
 * Queue is a last-in-last-out mechanism, push()ing to the end,
 * and pop()ing from the beginning.
 *
 * @author Mike Criscolo
 * @author Anders E. Topper
 * @since Reviewed 250899
 */

public class Queue extends Vector {

    protected int itemcount;

    public Queue() {
        super();

        itemcount = 0;
    }

    /**
     * Pops the first Object from Queue, and removes it.<P>
     * If the Queue is empty, this method will block in a sleep loop until an
     * element is inserted by another thread.
     *
     * @return The object stored in Queue.
     */

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

    /**
     * Calls the internal notify() method.
     */
    // Handy place to put a separate notify call - used during shutdown.
    public synchronized void bump() {
        notify();
    }



}
