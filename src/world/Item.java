package world;

import java.awt.Point;
import root.WorldSize;
import java.util.Observable;
import java.util.Observer;

public class Item extends Observable implements WorldSize {

  private boolean isActive = true;

  private static final float MIN_SPEED = WorldSize.MIN_SPEED;


  static final double GRAD_DIV_RAD = 180.0/Math.PI;
  protected static final double twoPI = Math.PI *2.0;
  public int ID;
  private float mass = 1.0f;

  public PosSet pos = null;
  public RotSet rot = null;

  public ItemState state = null;

  private int hitTargetID = -1;
  private int launchID = -1;

  private boolean isMissile = false;


  public int x;
  public int y;
  public int rotPos;

  protected Force force             = null;
  protected Force1D rotForce = null;

  public int rColor = 1000;		//ENERGY
  public int bColor = 0;
  public int gColor = 0;

  private String message = "";
  private String name = null;

  public int getID() {
    return ID;
  }

  public Item (int ID) {

    this.ID = ID;
    this.pos = new PosSet();
    this.rot = new RotSet();
	force = new Force();

	force.setFriction();

	rotForce = new Force1D();
	rotPos = radtoDegree(rot.position);
    x = Math.round(pos.x);
    y = Math.round(pos.y);

	state = new ItemState();

	//System.out.println("Item created");

  }

	public void exile() {
		destruct(); 		//Notify guiders

		pos.setPos(System.currentTimeMillis(),
								WorldSize.X_EXILE,
								WorldSize.Y_EXILE,0f,0f);

		posChanged();
	}

	private boolean isExiled() {
		return x > WorldSize.WORLD_WIDTH  -1 || y > WorldSize.WORLD_HEIGHT -1;
	}

	public boolean hitBy(Item j) {
		System.out.println("I, "+ ID +", was hit by item " + j.ID);
		rColor -=300;


		if (rColor < 0) {
			System.out.println("I AM DEAD: " + ID);
			//exile();		//Self-desctruct
			return true;
		} else {
			return false;
		}


	}


	public void setMissile(boolean missile, int lid) {
		if(missile) {
			if (lid == -1) {
				throw new IllegalArgumentException("No launchID given");
			}
			launchID = lid;
			isMissile = true;
		} else {
			launchID = -1;
			isMissile = false;
		}
		hitTargetID = -1;

	}


	public void hit(int id) {
		System.out.println("I," + ID + " hit item " + id + "!");
		gColor += 10;
	}


	public boolean isMissile() {
		return isMissile;
	}

public void deleteObserver(Observer obs) {
	super.deleteObserver(obs);
	//System.out.println("" + ID + ": deleteObserver");

}


public void destruct() {

	int pre = countObservers();

	//System.out.println("---Item." + ID + " destruct, has" + pre + " observer(s)");

	setChanged();
	notifyObservers();

	int post = countObservers();

	if (post  > 0) {
		System.out.println("WARN: " + pre + "->" + post + " observer(s)");
	} else if (pre == 0 && post == 0) {
	//	System.out.println("NOTE: 0->0");
	} else {
	//	System.out.println("NOTE: " + pre + "->" + post + " observer(s)");
	}


	deleteObservers();

	//System.out.println("---Item destruct END");

}

  public ItemState getState() {
	  return state;
  }

  public boolean inState(int s) {
	  return state.inState(s);
  }


  public int getStateCode() {
	  return state.getState();
  }

  public void moveTo(int x, int y) {
	  pos.setPos(System.currentTimeMillis(),(float) x, (float) y,0, 0);
	  posChanged();
  }

 public void setScore(int score) {
	 gColor = score;
 }

  public void moveTo(Item i) {

	setPos(i.getPos() );
	setRotation(i.getRotation() );
	setForce(i.getForce() );
	isActive = true;

  }

	public int getLaunchID() {
		if (!isMissile() ) throw new IllegalStateException("Not missile");
		return launchID;
	}


	public void launchFrom(Item i) {
		if (!isMissile() ) throw new IllegalStateException("Not missile");

		launchID = i.ID;

		setLaunchPos(i.getPos() );		//Twice the input speed

		rot.setRotation(i.getRotation() );
		rot.setStraight();									//like input,, but no speed

		rotPos = radtoDegree(rot.position);
		rotateTo(System.currentTimeMillis() );

		isActive = true;

	}


  public void colorTo(Item i) {
	  setColor(i.getRed(), i.getGreen(), i.getBlue() );
	  setMessage(i.getMessage() );
	  if (i.getName() != null) {
		  setName(i.getName() );
	  }
  }


  public Force1D getRotForce() {
	  return rotForce;
  }

  public void setRotForce(Force1D f) {
	 rotForce = f;

	isActive = true;
  }

  public Force getForce() {
   return force;
  }

  public void setForce(Force f) {
	Force1D fX = f.x;
	Force1D fY = f.y;

	Force1D fXmy = force.x;
	Force1D fYmy = force.y;

	fXmy.copyFrom(fX);
	fYmy.copyFrom(fY);

	isActive = true;

  }

  public RotSet getRotation() {
    return rot;
  }

 public void setRotSpeed(int speed) {
	 rot.speed = speed;
	 rotPos = radtoDegree(rot.position);

	isActive = true;
 }

  public void setRotation(RotSet r) {
    rot.setRotation(r);
    //rot = r;
    rotPos = radtoDegree(rot.position);
	rotateTo(System.currentTimeMillis() );

	isActive = true;
 }

  private int radtoDegree(double rad) {
     if (rad < 0.0 || rad >= twoPI) {
            rad = 0.0;
        }
    int angle = (int) Math.round(rad * GRAD_DIV_RAD);
    return angle;
  }

  public PosSet getPos () {
      return pos;
  }

  public void rotationChanged() {
	  rotPos = radtoDegree(rot.position);
	  rotateTo(System.currentTimeMillis() );
	  isActive = true;
  }


  public void setPos (PosSet in) {
       pos.setPos(in.time,in.x, in.y, in.vx, in.vy);
       x = Math.round(pos.x);
       y = Math.round(pos.y);

		isActive = true;
  }

	private void setLaunchPos (PosSet in) {
		pos.setPos(in.time,in.x, in.y, in.vx*2f + 4f, in.vy*2f+4f);
		 x = Math.round(pos.x);
        y = Math.round(pos.y);

		isActive = true;

	}


  public void posChanged() {
	  x = Math.round(pos.x);
      y = Math.round(pos.y);

 	  isActive = true;
  }

   public void setMessage(String n) {
        message = n;
		//TextImage.setText(ID, n);
    }

    public String getMessage() {
        return message;
    }

	public void setName(String n) {
		if (n == null) throw new IllegalStateException("name is null");
		name = n;
		System.out.println("Item " +ID + ": Name set to " + name);
	}

	public String getName() {
		return name;
	}



	public Point getDiff(Item i) {
		if (i == null) {
			System.out.println("Item.getDiff: Received null item. " +
				"Not handling this situation well.");
			return null;
		}
		return new Point(Math.abs(x-i.x), Math.abs(y-i.y) );
	}

	public static Point getDiff(Item i, Item j) {
		if (i == null || j == null) {
			throw new IllegalArgumentException("Received null item");
		}
		return new Point(Math.abs(j.x-i.x), Math.abs(j.y-i.y) );

	}

/*

	public boolean isHeaded(Item i) {
		return rot.isHeaded(i.getRotation() );
	}
*/

/*
	public static boolean isHeaded(Item i, Item j) {
		if (i == null || j == null) {
					throw new IllegalArgumentException("Received null item");
		}

		RotSet r = i.getRotation();
		return r.isHeaded(j.getRotation() );

	}
*/

  public void setColor(int r, int g, int b) {
    this.rColor = r;
    this.gColor = g;
    this.bColor = b;

  }

  public int getRed() {
     return rColor;
  }

  public int getGreen() {
      return gColor;
  }

  public int getBlue() {
        return bColor;
  }

	public boolean say(String s) {

		if (state.isControlled() ) {
			setMessage(s);
			return true;
		} else {
			System.out.println("Item not controlled, cannot say");
			return false;
		}
	}

  public String getInfo() {
	  StringBuffer b = new StringBuffer(200);

	  b.append("\nid =" + ID);
	  b.append("\nACTIVE " + isActive);
	  b.append("\nstate " + state.desc() );
	  b.append("\nenergy " + rColor);

   	  b.append("\nx  =" + pos.x );
	  b.append("\ny  =" + pos.y );
	  b.append("\nvx =" + pos.vx);
	  b.append("\nvy =" + pos.vy);

	  Force1D fX = force.x;
	  Force1D fY = force.y;

	  b.append("\nfx const=" + fX.getConstParam() );
	  b.append("\nfy const=" + fY.getConstParam() );

	  b.append("\nfx speed=" + fX.getSpeedParam() );
	  b.append("\nfy speed=" + fY.getSpeedParam() );

	  b.append("\nfx tot=" + force.getXComponent(pos) );
	  b.append("\nfy tot=" + force.getYComponent(pos) );

	  b.append("\nrotation (degree)=" + rotPos);
	  b.append("\nrotation (rad       ) = " + rot.position);
	  b.append("\nsay =  " + message);
	  b.append("\nname= " + name);
	  b.append("\n# of observers: ");
	  b.append(countObservers() );

	  return b.toString();

  }

	public void moveTo(long time) {

		if (isExiled() ) {
			return;
		}

		if(!isActive) {
			pos.time = time;
			return;
		}



	    float dT = (time - pos.time) /1000.0f;  //secs

	    float forceX = force.getXComponent(pos);
	    float newVX = (float)forceX/mass* dT+ pos.vx;
	    float newX = pos.x + dT*pos.vx;

	    if (newX < 0 ) {
	      newX = 0;
	      newVX = -(float) newVX;
	    }

	    if (newX > WORLD_WIDTH -1) {
	      newX = WORLD_WIDTH -1;
	      newVX = -(float) newVX;
	    }

		float forceY = force.getYComponent(pos);
		float newVY = (float)forceY/mass* dT+ pos.vy;
	    float newY = pos.y + dT*pos.vy;

	    if (newY < 0 ) {
	        newY = 0;
	        newVY = -(float) newVY;
	    }

	    if (newY > WORLD_HEIGHT -1) {
	        newY = WORLD_HEIGHT -1;
	    	newVY = -(float) newVY;
	    }

		pos.setPos(time, newX, newY, newVX, newVY);
		x = Math.round(pos.x);
	    y = Math.round(pos.y);

	}

	public void rotateTo(long time) {

		if (isExiled() ) {
			return;
		}


		if(!isActive) {
			rot.time = time;
			return;
		}


        long lastTime = rot.time;
        double pos = rot.position;
        int speed = rot.speed;

        pos += 2.0 * speed * (time - lastTime)/ 7000.0 ;

        while (pos > twoPI) {
            pos -= twoPI;
        }

        while (pos < 0) {
            pos += twoPI;
        }

        rot.position = pos;
        rot.time = time;
       	rotPos = radtoDegree(rot.position);

		checkActivity();
	}


	public void impact(Item target) {
		if (!isMissile() ) throw new IllegalStateException("Not missile");
		hitTargetID = target.ID;
	}

	public int getTargetID() {
		if (!isMissile() ) throw new IllegalStateException("Not missile");
		return hitTargetID;
	}


	private void checkActivity() {

		if (!isActive) {
			return;
		}

		if (force.hasPush() || Math.abs(pos.vx) > MIN_SPEED ||
				Math.abs(pos.vy) > MIN_SPEED || Math.abs(rot.speed) > 0) {
			//is active
		} else {
			//System.out.println("Setting item to passive, id = " + ID);
			idle();
		}
	}


	protected void idle() {
		force.removePush();
		pos.vx = 0f;
		pos.vy = 0f;
		rot.speed = 0;
		isActive = false;
	}


	public void free() {
		//System.out.println("Item.free()");
		name = null;
		idle();

	}

}
