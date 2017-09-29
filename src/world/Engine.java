package world;

import java.util.Random;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;

import root.WorldSize;
import display.RenderImage;
import root.ParseUtil;
import display.FrameProvider;
import root.Command;
import root.Factory;
import cmd.Environment;

import display.TextLister;
import io.TimeOut;

import offline.User;


public class Engine implements WorldSize, FrameProvider,
								ItemFactory, FocusMaintainer {


	private int EMPTY_TIME = 1000*15;
	public static final int TXT_SIZE = 11;

	public static final int IFOCUS_X = 200;
	public static final int IFOCUS_Y = 200;

    private String name;

	private RenderImage[] closeBuffer; //To commands

	protected TextLister chat;

	private IDDispenser idMaker = null;

	private boolean isFlushing = false;

    private ItemArray data = null;

	private Item lastSaid = null;

	private boolean isRunning = false;
	private long ticks = 0;

	private TimeOut emptyTime = new TimeOut(EMPTY_TIME);

	private Focus focus = null;

	private final Random random =
  				new Random(System.currentTimeMillis() );


	public Item createUser(User u) {
		Item i = createOwn();
		u.setItemID(i.ID);

		i.moveTo(u.getX(), u.getY() );
		i.setScore(u.getScore() );

		String name = u.getUsername();
		//System.out.println("Engine.createUser: setting name to " + name);
		i.setName(name);
		attachFocus(i.ID);
		return i;
	}


	public boolean mayControl(int id) {
		Item i = data.search(id);
		if (i != null) {
			ItemState s = i.getState();
			return s.isControlled();
		} else {
			return false; 	//Cannot control non-existing item.
		}

	}

	public void setIDs(int seed) {
			int min = seed*WorldSize.ID_RANGE;
			int max = min +WorldSize.ID_RANGE;
 			System.out.println("min =" + min + ", max=" + max);

 			setIDDispenser(new IDDispenser(min, max) );
	}

	protected void setIDDispenser(IDDispenser d) {
		if (idMaker != null)
			throw new IllegalStateException("Already has idMaker");
		idMaker = d;
	}

	public void addMessage(String message) {
		chat.add(message);
	}

	public Item getSaid() {
		return lastSaid;
	}

	public void clearSaid() {
		lastSaid = null;
	}


	public ItemArray getData() {
		//Used by comparator
		return data;
	}


	public void doFlush(boolean b) {
		isFlushing = b;
	}


	public PosSet getPos(int ID) {
		Item i = null;
		i = data.search(ID);
		if (i == null) {
			return null;
		}
		PosSet p = i.getPos();
		return p;
	}

	public Focus getFocus() {
		focus.getOffset();
		return focus;
	}

	public Rectangle getFocusRectangle() {
		focus.getOffset();	//Actually *sets* focus. Bad.
		return focus.getRect();
	}


    public Engine(String name, Focus f) {

		this.name = name;
		data = new ItemArray(ITEM_AVERAGE);
		//focus = new Focus(); //with random location, default size.
		focus = f;

		chat = new TextLister(TXT_SIZE);
		closeBuffer = RenderImage.createBuffer(ITEM_AVERAGE);

	}


	public boolean isAbandoned() {
		return emptyTime.hasTimedOut();
	}

	public String abandonStatus() {
		if (isAbandoned() ) {
			return "[  EMPTY  ]";
		} else {
			return "[NON-EMPTY]";
		}

	}


	public int size() {
		return data.size();
	}


	public boolean start() {
		if (isRunning) {
			return false;
		} else {
			isRunning = true;
			return true;
		}

	}

	public boolean stop() {
		if(!isRunning) {
			return false;
		} else {
			isRunning = false;
			return true;
		}

	}

	public String getPosInfo(int ID) {
		Item i = getItem(ID);
		if (i == null) {
			return "Engine " + name + ": Item not found";
		}
		return i.getInfo();
	}

	public Point getOffset() {
		if (focus == null) {
			throw new IllegalStateException("No focus");
		}

		return focus.getOffset();

	}


	public Item getItem(int ID) {
		return data.search(ID);
	}

	public Item getTransfer(Focus f) {
		if (f == null) {
			throw new
				IllegalArgumentException("Engine.getTransfer:"
						+ " Warning: got 0 focus");
		}

		int out = data.sort(f.getRect() );

		Item k = null;
		ItemState s = null;
		Item[] a = data.getArray();

		for (int pos = 0; pos < out; pos++) {

			k = a[pos];
			s = k.getState();

			if (s.inState(ItemState.TRANSFER) ) {
				return k;
			}
		}
		return null;
	}

	public Focus createItemFocus(int id) {
		return createItemFocus(id,IFOCUS_X,IFOCUS_Y);

	}


	private Focus createItemFocus(int id, int width, int height) {

		PosSet p = getPos(id);
		if (p == null) {
			return null;
		}

		Focus f = new Focus(
					new Dimension(width,height)  );

		boolean ok = f.attachTo(id,p);
		if(!ok) {
			System.out.println("Engine.createItemFocus:"
					+ "Warn: attach failed, couldnt happen");
			return null;
		} else {
			f.getOffset();
			return f;
		}

	}

	//XXX 'get-one-in-area-method-needed.

	public int getA(RenderImage[] buffer, Focus f) {

		int counter = 0;
		int out =0;

		//System.out.println("getA: total # of items: " + data.size() );

		if (f != null) {	//Return rectangle

			f.getOffset(); //Bad focus impl, this makes it look up and set values.
			out = data.sort(f.getRect() );
			//System.out.println("getA/Focus: found " + out + " item(s)");

		} else { 			//return everything
			out = data.size();
		}

		if (out > buffer.length) {
			//System.out.println("Engine.getA: Full buffer:"
			//			+ buffer.length + " of " + out + "item(s) delivered");
			out = buffer.length;
		}



		Item k = null;
		Item[] a = data.getArray();
		for (int pos = 0; pos < out; pos++) {

			k = a[pos];
			buffer[counter].x = k.x;
			buffer[counter].y = k.y;
			buffer[counter].imageRef = k.rotPos;
			buffer[counter].id = k.ID;
			buffer[counter].state = k.getStateCode();
			buffer[counter].isMissile = k.isMissile();
			buffer[counter].energy = k.rColor;
			buffer[counter].score = k.gColor;
			buffer[counter].name = k.getName();	//May be null

			counter++;

		}
		return counter;

	}


	public int getArea(RenderImage[] buffer) {

		if (focus == null) {
			throw new IllegalStateException("Focus is null");
		}
		return getA(buffer, focus);

	}


	public int getAll(RenderImage[] buffer) {
		return getA(buffer, null);
	}


	public void tick(long time) {

		if (isFlushing) {

		  double t = random.nextDouble();
			if (t < 0.043) {
				flush();
			}
		}
		turn(time);
	}


    private void turn(long time) {

		if (isRunning) {
			ticks++;
			Item i = null;

			if (data.size() > 0) {
				emptyTime.set();	//Has items.
			}

			Item[] a = data.getArray();
			for(int pos=0;pos < data.size();pos++) {

				i = a[pos];

				i.moveTo(time);
				i.rotateTo(time);

			}
		}
    }


	private boolean inFocus(int x, int y) {
		Point p = focus.getOffset();
		Dimension d = focus.getFocusSize();
		int offX = p.x;
		int offY = p.y;
		int width = d.width;
		int height = d.height;

		int maxX = width + offX;
		int maxY = height + offY;

		if (x>=offX&&x<maxX&&y>=offY&&y< maxY) {
			return true;
		} else {
			return false;
		}
	}


	 public void flush() {

		Item i = null;
		ItemState s = null;

		Item[] a = data.getArray();

		int out = data.sort(focus.getRect() );

		//Insiders
	  	for (int pos = 0; pos < out; pos++) {
			i = a[pos];
			s = i.getState();
			s.setInside();

		}

		//Outsiders
		int delCount = 0;
		int index = data.size();
		for (int pos = out; pos < index; pos++) {
			i = a[pos];
			s = i.getState();
			s.setOutside();

			if (s.inState(ItemState.DROP) ) {
				s.destroy();
				i.destruct();
				i.free();
				i.ID = -1;

				data.removeAt(pos);	//last element is also outside - so OK.
				delCount++;
				index--;
				pos--;
			}

		}
		if (delCount > 0) {
			//System.out.println("FLUSH[" + name + "]del " + delCount);
		}


	}

	private boolean drop(int id) {
		Item i = data.search(id);
		if (i == null) {
			System.out.println("Engine.drop: Item does not exist, id=" + id);
			return false;
		}

		ItemState s = i.getState();

		if (s.inState(ItemState.DROP) ) {
			s.destroy();
			i.destruct();
			i.free();
			i = data.delete(id);
			i.ID = -1;
			return true;
		} else {
			System.out.println("Engine.drop: Couldn't drop item, id = " + id);
			return false;
		}

	}

	public boolean forceDrop(int id) {
		Item i = data.search(id);
		if (i == null) {
				System.out.println("Engine.drop: Item does not exist, id=" + id);
				return false;
		}
		i.exile();
		return true;
		/*
		ItemState s = i.getState();
		System.out.println("Engine.Forcing drop of item " + id + " in state " + s.desc() );
		s.destroy();
		i.destruct();
		i = data.delete(id);
		i.ID = -1;
		return true;
	*/
	}

	public TextLister getChat() {
		return chat;
	}


	//Remove id, use item directly.
	public boolean releaseControl(int id) {
		Item i = data.search(id);
		if (i == null) {
			return false;
		}

		ItemState s = i.getState();

		if (s.inState(ItemState.TRANSFER) ) {
			s.release();
			return true;
		} else {
			return false;
		}

	}

	public Item makeSpec(int id) {
		Item i = data.search(id);

		if (i == null) {

			i = data.add();

			ItemState s = i.getState();
			try {
				s.init(ItemState.SPEC);
			} catch (IllegalStateException ie) {
				System.out.println("Bad item creation, engine, name=" + name);

				System.out.println("Item old ID = " + i.ID);
				System.out.println("Item state = " + s.desc());



				throw new IllegalStateException ("Item not properly repooled");
			}

			i.ID = id;			//Created spec item
			i.setMissile(false,-1);


			return i;
	    } else {
			ItemState s = i.getState();

			if (s.isControlled() ) {
				return null;
			} else {
				return i;
			}
		}

	}


	public Item makeTake(int id) {

		Item i = makeSpec(id);
		if (i == null) {
			System.out.println("Engine.makeTake[" + name
								+ "]:Failed to take, already owned? "  + id);
			return null;
		}

		ItemState s = i.getState();
		s.take();

		//System.out.println("Engine.makeTake[" + name
		//														+ "]:Item taken:  "  + id);
		return i;

	}

	public Item createOwn() {
		if (idMaker == null) {
			System.out.println("Engine[" + name + "]:No ID");
			return null;
		}

		int id = idMaker.get();
		Item i = data.search(id);
		if (i != null) {
			throw new IllegalStateException("Item exists, id=" + id);
		}

		i = data.add();

		ItemState s = i.getState();
		//s.init(ItemState.OWN);

		try {
			s.init(ItemState.OWN);
		} catch (IllegalStateException ie) {
			System.out.println("Bad item creation, engine, name=" + name);

			System.out.println("Item old ID = " + i.ID);
			System.out.println("Item old state = " + s.desc());

			throw new IllegalStateException ("Item not properly repooled");

		}

		i.ID = id;
		i.setMissile(false,-1);

		return i;

	}


/*
	//Not tested in this form. Left out when created new create.

	public Item getOwn(int id) {

		Item i = data.search(id);

		if (i == null) {
			return null;
	    } else {
			ItemState s = i.getState();
			if (s.isControlled() ) {
				return i;
			} else {
				return null;
			}
		}
	}

*/

	public boolean attachFocus(int id) {

		PosSet p = getPos(id);
		return focus.attachTo(id,p); //Handles p==null

	}

	public void fixFocus(int x, int y) {
		focus.detach(x,y);
	}

	public void registerCommands(Factory f, String prefix) {
		f.add(prefix + "start", new Start() );
		f.add(prefix + "ids", new IDS() );

		f.add(prefix + "drop", new Drop() );
		f.add(prefix + "stop", new Stop() );
		f.add(prefix + "say", new Say() );
		f.add(prefix + "get", new Get() );
		f.add(prefix + "status", new Status() );
		f.add(prefix + "create", new Move() );
		f.add(prefix + "push", new Push() );
		f.add(prefix + "populate", new Populate() );
		f.add(prefix + "flush", new Flush() );
		f.add(prefix + "fix", new Fix() );
		f.add(prefix + "fsize", new FocusSize() );
		f.add(prefix + "chat", new Chat() );
		f.add(prefix + "itemfocus", new ItemFocus() );

	}

	public void deregisterCommands(Factory f, String prefix) {
		f.remove(prefix + "start");
		f.remove(prefix + "ids");
		f.remove(prefix + "drop");
		f.remove(prefix + "stop");
		f.remove(prefix + "say");
		f.remove(prefix + "get");
		f.remove(prefix + "status");
		f.remove(prefix + "create");
		f.remove(prefix + "push");
		f.remove(prefix + "populate");
		f.remove(prefix + "flush");
		f.remove(prefix + "fix");
		f.remove(prefix + "fsize");
		f.remove(prefix + "chat");
		f.remove(prefix + "itemfocus");

	}

	public Item getAny() {
		//null if empty
		return data.getAny();
	}


	private Point getAnyPos() {
		int x = (int) Math.round(Math.random()*( WorldSize.WORLD_WIDTH-1 ) );
		int y = (int) Math.round(Math.random()*( WorldSize.WORLD_HEIGHT-1 ) );
		return new Point(x,y);
	}



	/**
	DROP COMMAND
	*/

	class Drop extends Command {

		int id = -1;
		public Drop() {
			setUsage("e_drop <itemID>");
		}

		public Command create(String[] args) {
			Command c = new Drop();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			id = -1;
		}

		public void setArgs(String[] args) {
			init();

			if (args != null && args.length == PAR1 +1) {
				int i = ParseUtil.parseInt(args[PAR1] );
				if (i > -1 ) {
					id = i;
					isValid = true;
					return;
				}
			}
			return;
		}

		public void execute() {
			if (isValid) {
				Item i = getItem(id);

				if (i == null) {
					setResult("Item not found, id=" + id);
					return;
				}

				//boolean ok = drop(id);
				boolean ok = forceDrop(id);
				if (ok) {
					setResult("Item forcefully dropped, id=" + id);
				} else {
					setResult("Couldn't drop item, id=" + id);
				}

			} else {
				setResult(showUsage() );
			}
		}
	}

	class Flush extends Command {

		public Flush() {
					setUsage("flush");
		}

		public Command create(String[] args) {
					Command c = new Flush();
					c.setArgs(args);
					return c;
		}

		public void init() {
					super.init();
		}

		public void setArgs(String[] args) {
					isValid = true;
		}

		public void execute() {
					flush();
					setResult("Flush done");
		}

	}

	/**
		IDS COMMAND
	*/
	class IDS extends Command {

		public IDS() {
			setUsage("ids");
		}

		public Command create(String[] args) {
			Command c = new IDS();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			isValid = true;
		}

		public void execute() {
			int i = env.getItemID();
			if ( i == -1) {
				setResult("bad id seed:" + i);
			} else {
				setIDs(i);
				int min = i*WorldSize.ID_RANGE;
				int max = min + WorldSize.ID_RANGE;
				setResult("ids in range(" + min + "," + max + ") secured");
			}
		}
	}

	/**
		START COMMAND
	*/
	class Start extends Command {

		public Start() {
			setUsage("start");
		}

		public Command create(String[] args) {
			Command c = new Start();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			isValid = true;
		}

		public void execute() {
			boolean changed = start();
			setResult("internal completed");
		}
	}

	/**
	* STOP COMMAND
	*/

	class Stop extends Command {

		public Stop() {
			setUsage("stop");
		}

		public Command create(String[] args) {
			Command c = new Stop();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			isValid = true;
		}

		public void execute() {
			boolean changed = stop();
			setResult("");
		}
	}

	/**
	* SAY COMMAND
	*/


	class Say extends Command {
		private int id = -1;
		private String message = null;

		public Say() {
			setUsage("e_say <itemID> <text>");
		}

		public Command create(String[] args) {
			Command c = new Say();
			c.setArgs(args);
			return c;
		}

		protected void init() {
			super.init();
			id = -1;
			message = null;
		}

		public void setArgs(String[] args) {
			init();
			if (args.length >= PAR2 + 1) {
				StringBuffer b = new StringBuffer(20);
				int id = ParseUtil.parseInt(args[PAR1]);
				for (int i = PAR1 +1;i < args.length;i++) {
					b.append(args[i]);
					b.append(" ");
				}
				this.id = id;
				message = b.toString();
				isValid = true;
				return;
			}
			isValid = false;
		}

		public void execute() {
			if (isValid) {
				Item i = getItem(id);

				if (i == null) {
					setResult("Item not found, id=" + id);
					return;
				}

				boolean ok = i.say(message);

				if (ok ) {
					setResult("said - OK");
					String name = i.getName();
					if (name != null) {
						addMessage("[" + name + ">" + message);
					} else {
						addMessage("[" + id + ">" + message );
					}
				} else {
					setResult("Cannot say - spectating only, item id = " + id);
				}

			} else {
				setResult(showUsage() );
			}

		}
	}


/**
* GET COMMAND
*/

class Get extends Command {

		protected int id = -1;

		public Get() {
			setUsage("e_get <itemID>");
		}

		public Command create(String[] args) {
			Command c = new Get();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			id = -1;
		}

		public void setArgs(String[] args) {
			init();
			if (args.length == PAR1 + 1) {
				int i = ParseUtil.parseInt(args[PAR1]);
				if (i > -1) {
					id = i;
					isValid = true;
					return;
				}
			}
			isValid = false;
		}

		public void execute() {
			if (isValid ) {
				Item i = getItem(id);
				if (i != null) {
					env.setItemID(id);
				}

				setResult(getPosInfo(id) );
				return;
			} else {
				setResult(showUsage() );
			}
		}
	}


	/**
	*  STATUS COMMAND
	*/

	class Status extends Command {

		public Status() {
			setUsage("status");
		}

		public Command create(String[] args) {
			Command c = new Status();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			isValid = true;
		}

		public void execute() {
			setResult(status() );
		}

		private String status() {

			StringBuffer b = new StringBuffer (200);

			if (isRunning) {
				b.append("\nstate: running");
			} else {
				b.append("\nstate: stopped");
			}
			b.append("\nticks: " + ticks );
			b.append("\nitems: " + data.size() );
			b.append("\ndata : " + data.status() );

			b.append(" " + abandonStatus() );

		/*
			if (emptyTime.hasTimedOut() ) {
				b.append("\nempty");
			} else {
				b.append("\nnon-empty");
			}
		*/

			b.append("\nfocus: " + focus.status() );

			//b.append("\nenvitemID " + env.getItemID());
			if (idMaker != null) {
					b.append("\nIdGen: " + idMaker.status() );
			} else {
					b.append("\nIdGen: none");
			}

			return b.toString();

		}

	}


	/**
	* POPULATE COMMAND
	*
	*/
	class Populate extends Command {
		private int count = 0;


		public Populate() {
			setUsage("populate [number]");
		}

		public Command create(String[] args) {
			Command c = new Populate();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {

				int created = 0;
				for (int n = 0;n < count;n++) {
					Item i = createOwn();
					if (i != null) {
						Point pos = getAnyPos();
						PosSet p = new PosSet(System.currentTimeMillis(), pos.x,pos.y,0,0);
						i.setPos(p);
						created++;
					}
				}
				setResult("Populated with "+ created + " item(s)" );

			} else {
				setResult(showUsage() );
			}
		}


		public void setArgs(String[] args) {

			init();

			if (args.length == PAR1 + 1 ) {
				count = ParseUtil.parseInt(args[PAR1] );

				if (count > 0) {
					isValid = true;
				} else {
					isValid = false;
				}
			} else {
				isValid = false;
			}
		}

	}



	/**
	* PUSH COMMAND
	*
	*/

	class Push extends Command {
			private int id;

			private int xConst;
			private int xPos;

			private int yConst;
			private int yPos;


			public Push() {
				setUsage("push id cx posx cy posy");
			}

			public Command create(String[] args) {
				Command c = new Push();
				c.setArgs(args);
				return c;
			}

			public void init() {
				super.init();
			}

			public void execute() {
				if (isValid ) {

					//Item i = makeOwn(id);
					Item i = createOwn();
					if (i == null) {
						System.out.println("Push: item is null, check code here");
						return;
					}


					if (i == null) {
						setResult("Cannot push - spectating only, item id = " + id);
					} else {

						Force force = i.getForce();
						Force1D fX = force.x;
						Force1D fY = force.y;

				    	fX.setParams(xPos/1.0f, Force.FRICTION, xConst/1.0f);
				    	fY.setParams(yPos/1.0f, Force.FRICTION, yConst/1.0f);

						setResult("Item pushed or created - OK");
					}

				} else {
					setResult(showUsage() );
				}
			}

			public void setArgs(String[] args) {
				init();

				if (args.length == PAR5 + 1 ) {
					id = ParseUtil.parseInt(args[PAR1] );

					xConst = ParseUtil.parseInt(args[PAR2] );
					xPos = ParseUtil.parseInt(args[PAR3] );

					yConst = ParseUtil.parseInt(args[PAR4] );
					yPos = ParseUtil.parseInt(args[PAR5] );


					if (id != -1 && xConst != -1 && xPos != -1
							&& yConst != -1 && yPos != -1 ) {
						isValid = true;
					} else {
						isValid = false;
					}
				} else {
					isValid = false;
				}
			}

		}


	/**
	* MOVE COMMAND
	*
	*/
	class Move extends Command {
			private int x;
			private int y;
			private int vx;
			private int vy;

			public Move() {
				//setUsage("create x y vx vy");
				setUsage("create x y");
			}

			public Command create(String[] args) {
				Command c = new Move();
				c.setArgs(args);
				return c;
			}

			public void init() {
				super.init();
			}

			public void execute() {
				if (isValid ) {

					Item i = createOwn();
					if (i == null) {
						System.out.println("No ID resource, cannot create");
					} else {

						PosSet p =
							//new PosSet(System.currentTimeMillis(), x, y, vx, vy);
							new PosSet(System.currentTimeMillis(), x, y, 0,0);
						i.setPos(p);
						setResult("Item created, id = " + i.ID);

						//Set new item environment value:
						env.setItemID(i.ID);
					}

				} else {
					setResult(showUsage() );
				}
			}

			public void setArgs(String[] args) {
				init();

				//if (args.length == PAR4 + 1 ) {
				if (args.length == PAR2 + 1 ) {
					x = ParseUtil.parseInt(args[PAR1] );
					y = ParseUtil.parseInt(args[PAR2] );
					//vx = ParseUtil.parseInt(args[PAR3] );
					//vy = ParseUtil.parseInt(args[PAR4] );

					if (x != -1 && y != -1
							//&& vx != -1 && vy != -1

							) {
						isValid = true;
					} else {
						isValid = false;
					}
				} else {
					isValid = false;
				}
			}

		}

	class Fix extends Command {

		int x = -1;
		int y = -1;

		public Fix() {
			setUsage("fix [{width heigh})]");
		}

		public Command create(String[] args) {
			Command c = new Fix();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			x = -1;
			y = -1;
		}


		public void setArgs(String[] args) {

			init();

			if (args.length == PAR2 +1) {
				x = ParseUtil.parseInt(args[PAR1]);
				y = ParseUtil.parseInt(args[PAR2]);
				if (x == -1 || y == -1) {
					isValid = false;
				} else {
					isValid = true;
				}
			} else {
				isValid = true;
			}

		}

		public void execute() {
			if (focus == null) {
				throw new IllegalStateException("Focus is null");
			}

			if (isValid && x != -1 && y != -1) {
				fixFocus(x,y);
				setResult("");
			} else if (isValid) {
				boolean b = attachFocus(env.getItemID() );
				if(b) {
						setResult("Focus attached to item");
				} else {
						setResult("Item not found");
				}
			} else {
				setResult(showUsage() );
			}
		}
	}

	class FocusSize extends Command {

		int x = -1;
		int y = -1;


		public FocusSize() {
			setUsage("focussize {width>=10 heigh}>=10");
		}

		public Command create(String[] args) {
			Command c = new FocusSize();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			x = -1;
			y = -1;
		}


		public void setArgs(String[] args) {

			init();

			if (args.length == PAR2 +1) {
				x = ParseUtil.parseInt(args[PAR1]);
				y = ParseUtil.parseInt(args[PAR2]);
				if (x < 10 || y < 10) {
					isValid = false;
				} else {
					isValid = true;
				}
			} else {
				isValid = false;
			}

		}

		public void execute() {
			if (isValid) {

				if (focus == null) {
					setResult("No focus defined");
				} else {
					focus.setWindow(new Dimension(x,y) );
					setResult("Focus size set to (" + x + "," + y + ")");

				}

			} else {
				setResult(showUsage() );
			}

		}
	}

	class Chat extends Command {

			public Chat() {
				setUsage("chat");
			}

			public Command create(String[] args) {
				Command c = new Chat();
				c.setArgs(args);
				return c;
			}

			public void init() {
				super.init();
			}

			public void execute() {
				if (isValid ) {
					//setResult(chat.getList() );
					setResult(chat.getReverseList() );
				} else {
					setResult(showUsage() );
				}
			}

			public void setArgs(String[] args) {
				init();
				isValid = true;
			}

	}

class ItemFocus extends Command {

			public ItemFocus() {
				setUsage("itemfocus");
			}

			public Command create(String[] args) {
				Command c = new ItemFocus();
				c.setArgs(args);
				return c;
			}

			public void init() {
				super.init();
			}

			public void execute() {
				if (isValid ) {
					int id = env.getItemID();
					if (id == -1) {
						setResult("no id env set");
					} else {
						Focus f = createItemFocus(id);
						if ( f == null) {
							setResult("Got no Focus");
						} else {
							int count = getA(closeBuffer, f);
							setResult("Found "
									+ count + " item(s) inside focus " + f.status() + list(id, count) );
						}

					}

				} else {
					setResult(showUsage() );
				}
			}

			private String list(int id, int count) {
				if (count <= 1) {
					return "\nNo neighbours found";
				}

				PosSet p = getPos(id);
				if (p == null) {
					return "\nNo item found";
				}
				int locX = (int) p.x;
				int locY = (int) p.y;

				StringBuffer b = new StringBuffer(100);

				for (int i = 0; i < count; i++) {
					b.append('\n');
					int _id = closeBuffer[i].id;
					int _x  = closeBuffer[i].x;
					int _y = closeBuffer[i].y;
					if(_id == id) {
						continue;
					}
					b.append("id=" + _id);
					b.append("x=" + _x);
					b.append("y=" + _y);

					b.append(", dx = " + (_x-locX) );
					b.append(", dy = " + (_y-locY));

				}
				return b.toString();
			}


			public void setArgs(String[] args) {
				init();
				isValid = true;
			}

	}


}