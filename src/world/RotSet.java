package world;

import java.util.Random;

/**
 * Container for data describing a certain rotation angle, speed of rotation at a
 * specified time.
 *
 * @author Anders E. Topper
 * @since Reviewed 250899
 */
public class RotSet {

  public long time;
  public double position;
  public int speed;

 /*
 public static final double NORTH = Math.PI/2d;
  public static final double WEST = Math.PI;
  public static final double SOUTH = 3d*Math.PI/2d;
  public static final double EAST = 0d;
	*/

 public static final double NORTH = Math.PI;
 public static final double SOUTH = 0d;
 public static final double WEST = Math.PI/2d;
  public static final double EAST = 3d*Math.PI/2d;

	public static final double SLACK = Math.PI/40d;///20d works best for  patrol

	private static final Random rand = new Random();

	public static final double MAX_ANGLE = 2d*Math.PI;
	public static final double MIN_ANGLE = 0d;

  public RotSet() {

		this.time = 0L;
	    this.speed = 0;
	    this.position =Math.PI*2*rand.nextDouble();

		checkAngle(position);

  }
	public static void checkAngle(double angle) {
		if (angle < MIN_ANGLE || angle >= MAX_ANGLE) {
						throw new
							IllegalStateException("Angle out of boundaries: angle="
																																+ angle);
			}

	}

	public boolean isHeaded(double direction) {
		checkAngle(direction);
		double dA = Math.abs(direction-position);
		//System.out.println("----");
		//System.out.println("local=" + position);
		//System.out.println("ext   = " + direction);
		//System.out.println("dA   = " + dA);
		//System.out.println("----");
		return dA < SLACK;
	}


	public boolean isHeaded(RotSet r) {
		if (r == this) {
			System.out.println("RotSet:WARN: isHeaded: Checking against self");
		}

		double direction = r.position;
		return isHeaded(direction);

	}


	public boolean isClockwiseTo(double direction) {
		checkAngle(direction);

		//double dA = position - direction;
		//System.out.println("local rot = " + position);
		//System.out.println("course   = " + direction);

		boolean isCounter = (position < direction) || (position > direction + Math.PI);
		return isCounter;

	}





  public RotSet (long time, double position, int speed) {
    this.time = time;
    this.speed = speed;
    this.position = position;

	checkAngle(position);

  }

  /**
   * Resets the object with new, given values, totally renewing the object.
   *
   * @param time Time when this position and speed occured
   * @param position Rotation position in radians
   * @param speed Rotation speed as Integer.
   */

  public void setRotation (long time, double position, int speed) {
    this.time = time;
    this.speed = speed;
    this.position = position;
	checkAngle(position);
  }


	public void setRotation(RotSet r) {
		this.time = r.time;
		this.speed = r.speed;
		this.position = r.position;

	}

	public void setStraight() {
		this.speed = 0;
	}


	public static void main(String[] args) {

		double angle = RotSet.NORTH;

		double check = 1.28;

		RotSet r = new RotSet(0L, angle, 0);

		if (r.isHeaded(check) ) {
			System.out.println("Is headed");
		} else {
			System.out.println("Is not headed");
		}

	}


}
