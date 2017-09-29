
package display;

import root.WorldSize;

import java.awt.Graphics;
import java.awt.Component;
import java.awt.Rectangle;

public abstract class AbstractRender {

	public static final int MAX_FB	= WorldSize.ITEM_AVERAGE;
	public static final int MAX_FOCA = WorldSize.MAX_FOCA;

	protected Component c;
	protected Rectangle b;


	public AbstractRender(Component c) {
		this.c = c;
	}

	public abstract void init();
	public abstract void draw();

	public void update(Graphics g) {
		System.out.println("AbstractRender.update()");
	}

	public void paint(Graphics g) {
		System.out.println("AbstractRender.paint()");
	}

	public void cleanUp() {
		System.out.println("AbstractRender.cleanUp()");
	}

	public void setBounds(int x, int y, int w, int h) {
		b = new Rectangle(x,y,w,h);
	}

	public Rectangle getBounds() {
		return b;
	}

}