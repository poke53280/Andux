package world;


public class PosSet {

  public long time;
  public float x;
  public float y;
  public float vx;
  public float vy;


public PosSet(long time, int x, int y, int vx, int vy) {
	this(time, (float)x, (float)y, (float)vx, (float)vy );
}


public PosSet() {

	this(0,40,50,0,0);
}

  /**
   * Initiates a PosSet with given values
   *
   * @param time
   * @param x x position
   * @param y y position
   * @param vx speed in x direction
   * @param vy speed in y direction
   */

 public PosSet (long time, float x, float y, float vx, float vy) {
    this.time = time;
    this.x    = x;
    this.y    = y;

    this.vx   = vx;
    this.vy   = vy;

  }

  /**
   * Completely resets data in an existing PosSet, leaving no marks of previous state.<P>
   *
   * @param time
   * @param x x position
   * @param y y position
   * @param vx speed in x direction
   * @param vy speed in y direction
   */

  public void setPos (long time, float x, float y, float vx, float vy) {
    this.time = time;
    this.x    = x;
    this.y    = y;
    this.vx   = vx;
    this.vy   = vy;

  }

  public void setPos(PosSet p) {
	  this.time = p.time;
	  this.x = p.x;
	  this.y = p.y;
	  this.vx = p.vx;
	  this.vy = p.vy;

  }

}
