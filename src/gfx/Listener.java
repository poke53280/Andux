
package gfx;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;


public class Listener implements KeyListener,
								 MouseListener,
								 MouseMotionListener {

	private int mx = 0;		//Absolute position of mouse on screen
	private int my = 0;

	private int offX;
	private int offY;

	private int kx = 0;		//Counters for keyboard
	private int ky = 0;
	private int kz = 0;

	private int cx = 0;		//last click pos, or other mark pos
	private int cy = 0;

	private int dx = 0;		//drag
	private int dy = 0;

	private int dmx = 0;	//mouse pos while dragging
	private int dmy = 0;


	//toggles
	private boolean requestReset = false;


	private boolean autoRotate = false;
	private boolean worldRotate = false;

	//Offset so that mouse input corresponds to logical screen
	public Listener(int x, int y) {
		offX = x;
		offY = y;
	}

	public int mx() { return mx; }
	public int my() { return my; }

	public int kx() { return kx; }
	public int ky() { return ky; }
	public int kz() { return kz; }

	public int dx() { return dx; }
	public int dy() { return dy; }


	public void resetKeyCounters() {
		kx = 0;
		ky = 0;
		kz = 0;
	}

	public void resetDrag() {
		cx = dmx;
		cy = dmy;
		dx = 0;
		dy = 0;
	}

	public void reset() {
		resetKeyCounters();
		resetDrag();
		requestReset = false;
	}

	public boolean resetRequested() {
		return requestReset;
	}

	public boolean autoRotate() { return autoRotate; }
	public void setAutoRotate(boolean b) {
		autoRotate = b;
	}


	public boolean worldRotate() { return worldRotate; }

	public boolean allZero() {
		return dx == 0 &&
			   dy == 0 &&
			   !requestReset &&
			   kx == 0 &&
			   ky == 0 &&
			   kz == 0;
	}


	public boolean keysZero() {
		return kx == 0 && ky == 0 && kz == 0;
	}

	public boolean dragZero() {
		return dx == 0 && dy == 0;
	}

	public void keyTyped(KeyEvent e) {  }
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_NUMPAD7) {
			requestReset = true;
			return;
		}
	}

    public void keyPressed(KeyEvent e) {

		int code = e.getKeyCode();

		if (code == KeyEvent.VK_NUMPAD6) {
			kx++;
			return;
		}
		if (code == KeyEvent.VK_NUMPAD4) {
			kx--;
			return;
		}

		if (code == KeyEvent.VK_NUMPAD8) {
			ky++;
			return;
		}

		if (code == KeyEvent.VK_NUMPAD2) {
			ky--;
			return;
		}

		if (code == KeyEvent.VK_PAGE_UP) {
			kz--;
			return;
		}

		if (code == KeyEvent.VK_PAGE_DOWN) {
			kz++;
			return;
		}

		if (code == KeyEvent.VK_HOME) {
			autoRotate = !autoRotate;
			return;
		}

		if (code == KeyEvent.VK_INSERT) {
			worldRotate = !worldRotate;
			return;
		}

	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		cx = e.getX();
		cy = e.getY();

	    mx = cx - offX;
	    my = cy - offY;
    }


  public void mouseDragged(MouseEvent e) {
		dmx = e.getX();
		dmy = e.getY();

		dx = dmx - cx;
		dy = dmy - cy;
  }

  public void mouseReleased(MouseEvent e) {
  		dx = 0;
  		dy = 0;

  }
  public void mouseEntered(MouseEvent e) {
  }
  public void mouseExited(MouseEvent e) {
  }
  public void mouseClicked(MouseEvent e) {
  }


}