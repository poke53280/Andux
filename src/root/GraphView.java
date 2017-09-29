
package root;

import statistics.Buffer;
import root.Pacer;
import java.awt.Color;

import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.Component;
import gfx.App;
import gfx.Chars;
import gfx.Matrix;
import gfx.Transformer;
import gfx.Model;
import java.awt.event.KeyEvent;

public class GraphView extends App {

	Buffer b;

	double degrees = 0.0;

	String name = null;
	CycleVector v;
	MultiSwitcher ml = new MultiSwitcher();

	public GraphView(Model m,  CycleVector v, String name) {
		super(m);
		this.b = (Buffer) v.next();
		this.name = name;
		this.v = v;

	}

	public String desc() { return "Graph:" + name; }

	public void setAngle(double d) {
		degrees = d;
	}

	public void draw() {

		if (b == null) return;
		//int w = getWidth();
		//int h = getHeight();

		int x = 0;

		//Chars chars = getChars();
		drawAxis();

		chars.setUpperLeft(0,0);
		if (b.size() == 0) {	//no data
			model.setColor(Color.red.getRGB() );
			chars.draw("no data");
			model.commit();
			//scaleTemp(.4f);
			//flushTemp();
			return;
		}

		float[] data = b.data();
		int next = b.next();
		int length = b.capacity();

		//XXX Optimize: If straight line: combine!
		drawX();

		model.setColor(Color.cyan.getRGB() );


		if (b.isWrapped() ) {
			for (int i = next; i < length; i++) {
				int y = b.scaledAmp(i);
				//y = h - y -1;

				int V = model.addVertex(x,y);


				if (x != 0) {
					model.addLine(V-1,V);
				}
				x++;

				//if (x %10 == 0) model.commit();
			}
		}
		//model.commit();
		for (int i = 0; i < next; i++) {
			int y = b.scaledAmp(i);
			//y = h - y -1;

			int V = model.addVertex(x,y);
			if (x != 0) {
				model.addLine(V-1,V);
			}
			x++;
			//if (x %10 == 0) model.commit();
		}
		model.commit();

		drawAverage();
		drawExtreme();

		model.commit();

		model.setColor(Color.green.getRGB() );
		chars.setUpperLeft(10,15);

		chars.draw(b.name() );
		chars.CR();

		model.commit();

		chars.draw("min-max");
		chars.draw(b.min(),5 );
		chars.draw(b.max(), 5);
		chars.CR();
		chars.draw("ave-now");
		model.commit();

		chars.draw(b.average(),5 );
		model.setColor(Color.white.getRGB() );
		chars.draw(b.last(),5 );
		model.setColor(Color.green.getRGB() );

		model.commit();
		//scaleTemp(.4f);

		degrees += 1.0;
		degrees %= 360.0;

		//flushTemp();

	}


	private void drawX() {

		model.setColor(0xFFFFFF);
		int w = 100;
		int h = 100;

		int y = b.xAxis();

		if (y < 0) return; 	//not on screen

		y = h - y -1;
		int A = model.addVertex(0,y);
		int B = model.addVertex(w-1,y);
		model.addLine(A,B);
	}

	private void drawAverage() {

		int c = Color.yellow.getRGB();
		model.setColor(c);

		//int w = getWidth();
		//int h = getHeight();

		int w = 100;
		int h = 100;

		int y = b.scaledAverage();
		//y = h - y -1;
		if (y > 1)	{
			int A = model.addVertex(w/3,y-1);
			int B = model.addVertex(2*(w-1)/3,y-1);
			model.addLine(A,B);
		}
		if(y < h-1 ) {
			int A = model.addVertex(w/3,y+1);
			int B = model.addVertex(2*(w-1)/3,y+1);
			model.addLine(A,B);
		}
		model.commit();
	}

	private void drawExtreme() {
		int c = Color.yellow.getRGB();
		model.setColor(c);

		int w = 100;
		int h = 100;

		float min = b.min();
		float max = b.max();
		if(min == max) return;

		int y = b.scaled(min);
		//y = h - y -1;


		int A = model.addVertex(w/3,y);
		int B = model.addVertex(2*(w-1)/3,y);
		model.addLine(A,B);

		y = b.scaled(max);
		//y = h - y -1;
		A = model.addVertex(w/3,y);
		B = model.addVertex(2*(w-1)/3,y);
		model.addLine(A,B);
		model.commit();

	}

	public void active(Component c) {
		if (ml != null) {
			c.addKeyListener(ml);
		}
	}

	public void inActive(Component c) {
		if (ml != null) {
			c.removeKeyListener(ml);
		}
	}


	class MultiSwitcher implements KeyListener {

		public void keyTyped(KeyEvent e) {  }
		public void keyPressed(KeyEvent e) { }
        public void keyReleased(KeyEvent e) {

	        int code = e.getKeyCode();

	        if (code == KeyEvent.VK_X) {
				b = (Buffer) v.next();
			}
			if (code == KeyEvent.VK_Z) {
				b = (Buffer) v.previous();
			}
  		}
	}

}