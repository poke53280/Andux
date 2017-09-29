package io.handler;

import world.Focus;

import java.awt.Dimension;
import java.awt.Point;


import world.FocusMaintainer;
import root.WorldSize;


public class FocusHandler implements InHandler {

	private MessageSender sender;

	private Focus mainFocus = null;

	private FocusMessage fm = null;

	private FocusMaintainer my = null;

	private int alwaysEach = 10;
	private int alwaysCount = 0;


	private Point lastOffset = new Point(0,0);
	private Dimension lastSize = new Dimension(0,0);

	private int THRESHOLD = WorldSize.FOCUS_THRESHOLD;


	public String desc() {
		return "focus";
	}


	private boolean sendNoMatter() {
		alwaysCount++;
		if (alwaysCount >= alwaysEach) {
			alwaysCount = 0;
			return true;
		} else {
			return false;
		}

	}


	private boolean isOutside(Point p, Dimension d) {

		int dx = Math.abs(p.x - lastOffset.x);
		int dy = Math.abs(p.y - lastOffset.y);

		boolean resized = (d.width != lastSize.width)
							|| (d.height != lastSize.height);

		if (dx > THRESHOLD || dy > THRESHOLD || resized) {
			return true;
		} else {
			return false;
		}


		//System.out.println("FocusHandler.checkDiff: dx = "
		//		+ dx + ",dy=" + dy);


	}


	public FocusHandler(MessageSender s, Focus main, FocusMaintainer my) {
	    sender = s;
		fm = new FocusMessage();
		mainFocus = main;

		this.my = my;
	}


	public void setRemoteFocus() {

		if (mainFocus == null) {
			throw new IllegalStateException("Focus of main engine not set");
		}

		Dimension d = mainFocus.getFocusSize();
		Point p = mainFocus.getOffset();

		if (!sendNoMatter() && !isOutside(p,d) ) {
			//System.out.println("Focus send aborted");
			return;
		}

		fm.create();


		int id = mainFocus.getFocusedItem();
		if (id == -1) {
			fm.setWindow(d);
			fm.setOffset(p);
			fm.setAttached(false);

		} else {
			fm.setID(id);
			fm.setWindow(d);
			fm.setOffset(p);
			fm.setAttached(true);

		}

		Message m = fm.getMessage();
		fm.forget();
		sender.push(m);

		mainFocus.copyFocus(lastOffset,lastSize);


	}

	public void input(Message m) {

		if (m== null) {
			System.out.println("RemoteFocus: message null");
			return;
		}

		fm.setMessage(m);

		Focus f = my.getFocus();

		if (f == null) {
			throw new IllegalStateException("RemoteFocus : Local focus is null ");
		} else {
			f.setWindow(fm.getWindow() );
		}

		Point p = fm.getOffset();

		if (fm.getAttached() ) {
			boolean b = my.attachFocus(fm.getID() );
			if (!b) {
				my.fixFocus(p.x, p.y);
			}

		} else {
			my.fixFocus(p.x, p.y);
		}
		fm.release();
	}

public class FocusMessage extends Manipulator {

   protected static final int ID = 1;
   protected static final int IS_ATTACHED   =       5;
   protected static final int WINDOW_XCOORD	=		6;
   protected static final int WINDOW_YCOORD = 		10;
   protected static final int OFFSET_X		=       14;
   protected static final int OFFSET_Y		= 		18;
   protected static final int SIZE			=		22;


	public FocusMessage() {
		super();
    }

	public void setMessage(Message m) {
		if (this.m == null) {
			this.m = m;
			if (m.getSize() != SIZE) {
				throw new IllegalStateException("wrong size:" + m.getSize() );
			}
		}
	}

	public void create() {
		if (m == null) {
			m = Message.getInstance();
			m.setByte(Port.PORT, (byte) Port.FOCUS);
			m.setSize(SIZE);


		} else {
			throw new IllegalStateException ("Message already set");
		}
	}


	public void setWindow(Dimension d) {
		int x = d.width;
		int y = d.height;
		setWinX(x);
		setWinY(y);
	}


	public Dimension getWindow() {
	  int x = getWinX();
	  int y = getWinY();

	  return new Dimension(x, y);

	  }

	public void setOffset(Point p) {

	  int x = p.x;
	  int y = p.y;

	  setXOff(x);
	  setYOff(y);

	}

	public Point getOffset() {
		  int x = getXOff();
		  int y = getYOff();
		  return new Point(x, y);
	  }


	public void setID(int id) {
		m.setInteger(ID, id);
	}

	public int getID() {
		return m.getInteger(ID);
	}

	public void setAttached(boolean b) {
		m.setBoolean(IS_ATTACHED, b);
	}

	public boolean getAttached() {
		return m.getBoolean(IS_ATTACHED);
	}

	public void setWinX(int x) {
		m.setInteger(WINDOW_XCOORD, x);
	}

	public int getWinX() {
		return m.getInteger(WINDOW_XCOORD);
	}

	public void setWinY(int y) {
		m.setInteger(WINDOW_YCOORD, y);
	}

	public int getWinY() {
		return m.getInteger(WINDOW_YCOORD);
	}

	public void setXOff(int x) {
		m.setInteger(OFFSET_X, x);
	}

	public int getXOff() {
		return m.getInteger(OFFSET_X);
	}

	public void setYOff(int y) {
		m.setInteger(OFFSET_Y, y);
	}

	public int getYOff() {
		return m.getInteger(OFFSET_Y);
	}


}


}