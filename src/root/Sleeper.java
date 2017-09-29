
package root;

/**
 * Blocks for wait milliseconds when called with sleep() method.<P>
 * If a wait less than zero is specified, Sleeper sets wait to 10 ms, and
 * returns a warning to stdout.
 *
 * @author Anders E. Topper
 * @since Reviewed 250899
 */
public class Sleeper {

 protected long wait;

 /**
  *
  * @param wait Block for wait milliseconds when told to sleep.
  */

 public Sleeper (long wait) {

        setSafeWait(wait);

 }

 /**
  * Changes wait parameter
  *
  * @param wait Time in milliseconds to sleep
  */

 public void setWait(long wait) {

        setSafeWait(wait);
 }

 /**
  * Returns current wait parameter.
  */


 public long getWait() {
    return wait;
 }


 /**
  * Sleep a number of milliseconds as specified by setWait().
  */


 public void sleep() {
        sleepFor(wait);
 }

/**
 * Sleep for the specified number of milliseconds.
 */


 public void sleepFor(long w) {

    try {
	      Thread.sleep (w);
    	} catch (InterruptedException e) {
    	  System.out.println("Sleeper.sleepFor() woken up");
    	}


 }

 protected void setSafeWait(long wait) {

    if (wait < 0 ) {
        System.out.println("Sleeper: Warning: wait < 0, setting wait = 10");
        wait = 10;

    }

    this.wait = wait;




 }



}
