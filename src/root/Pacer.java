
package root;

/**
 * Tries to return from its launch method every interval millisecond.
 * <P>
 * If launch is called when time is overdue, launch returns immediately.
 * <P>
 * If not, Pacer sleeps until time is right, then returns.
 * <P>
 *
 * @author Anders E. Topper
 * @since 250899
 */

public class Pacer {

    protected Sleeper sleeper;
    protected long interval;

    protected long time;

    /**
     * @param interval Pacer tries to return from its launch method every interval millisecond.
     */

    public Pacer (long interval) {

		if (interval < 0) throw new IllegalArgumentException("Interval = " + interval);

       this.interval = interval;
       sleeper = new Sleeper(interval);

       time = System.currentTimeMillis();
    }


	public void setInterval(long interval) {
			if (interval < 0) throw new IllegalArgumentException("Interval = " + interval);
			this.interval = interval;

			//if (interval == 0L) System.out.println("Pacer: Running MAX");

	}


    /**
     * Launch blocks until interval milliseconds have passed.
     */


    public void launch() {

		if (interval == 0L) return;	//Launch immediately

        time += interval;
        sleeper.sleepFor(Math.max(0, time - System.currentTimeMillis() ) );
    }



}
