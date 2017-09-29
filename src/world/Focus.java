package world;

import root.ParseUtil;

import java.awt.Dimension;
import java.util.StringTokenizer;
import java.awt.Point;
import java.awt.Rectangle;
import root.WorldSize;

public class Focus {

	private Point offset = new Point(0,0);
	private Dimension focusSize = null;
	private final Dimension worldSize;
	private int itemID = -1;

	private boolean attached = false;
	private PosSet pos = null;


	public Focus() {
		this(WorldSize.WORLD_SIZE,
										WorldSize.DEFAULT_WINDOW);

	}

	public Focus(Dimension window) {
		this(WorldSize.WORLD_SIZE, window);
	}

	public Focus(Dimension world, Dimension window) {
		worldSize = world;
		setWindow(window);
		detach(new Point(0,0) );

		setRandomPos();
	}

	public boolean attachTo(int id, PosSet p) {

		if (p == null) {
			itemID = -1;
			return false;
		} else {

			itemID = id;
			pos = p;
			attached = true;
			return true;

		}
	}

	public void copyFocus(Point p, Dimension s) {

		p.x = offset.x;
		p.y = offset.y;
		s.width = focusSize.width;
		s.height = focusSize.height;

	}


	public void setExiledPos() {
			setWindow(WorldSize.EXILED_SIZE);
			detach(WorldSize.EXILED_FOCUS);

	}


	protected void setRandomPos() {
		 int x = (int) Math.round(Math.random()*( worldSize.width - focusSize.width ) );
		 int y = (int) Math.round(Math.random()*( worldSize.height - focusSize.height ) );

		 detach(x,y);
	}

	public void setWindow(Dimension window) {
		if (focusSize == null)  focusSize = new Dimension();
		focusSize.setSize(window);

//		focusSize = window;
	}

	public void detach(Point p) {
		detach(p.x, p.y);
	}


	public void detach(int x, int y) {
		attached = false;
		itemID = -1;
		if (x >= 0 && y >= 0) {
			setOffset(x, y);
		} else {
			System.out.println("Focus.detach: Illegal values: x = " + x + ", y=" + y);
			setOffset(0,0);
		}
	}

	private void setOffset(int newX, int newY) {

		if (newX < 0 ) {
			newX = 0;
		} else if (worldSize.width - newX < focusSize.width) {
			newX = worldSize.width -focusSize.width;
		}

		if (newY < 0 ) {
			newY = 0;
		} else if (worldSize.height - newY < focusSize.height) {
			newY = worldSize.height -focusSize.height;
		}
		offset.setLocation(newX, newY);
	}

	private void setItemOffset() {

		int	itemX = Math.round(pos.x);
		int	itemY = Math.round(pos.y);

		int offsetX = itemX - focusSize.width/2;
		int offsetY = itemY - focusSize.height/2;
		setOffset(offsetX, offsetY);
	}

	public int getFocusedItem() {
		return itemID;
	}

	public Dimension getFocusSize() {
		return focusSize;
	}


	public Point getOffset() {
		if (attached) {
			setItemOffset();
		}
		return offset;

	}

	public String status() {
		StringBuffer b = new StringBuffer(100);
		Point p = getOffset();

		b.append("at(" + p.x + "," + p.y + "), ");
		b.append("s=(" + focusSize.width + "," + focusSize.height + ")");

		if (attached) {
			b.append("a(id=" + itemID + ")");
		} else {
			b.append("detchd");
		}

		return b.toString();
	}

	public Rectangle getRect() {
		return new Rectangle(offset, focusSize);
	}


	public boolean intersects(Focus f) {
		Rectangle local = new Rectangle(offset, focusSize);
		Rectangle arg = new Rectangle(f.getOffset(), f.getFocusSize() );

		return arg.intersects(local);

	}

}