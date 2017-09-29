
package root;


import java.awt.Color;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.awt.Component;
import gfx.App;
import gfx.Chars;
import gfx.Matrix;
import gfx.Transformer;
import gfx.Model;

import cmd.DisplayCom;

public class CommandView extends App {

	MultiSwitcher ml = new MultiSwitcher();

	StringBuffer line = new StringBuffer(50);

	String lastReply = "(none)";

	private DisplayCom dc = null;

	private boolean drawCursor = false;

	public CommandView(Model m, DisplayCom dc) {
		super(m);
		this.dc = dc;

		//l.skipLetters(true);

	}

	public String desc() { return "CommandWindow"; }

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

	public void draw() {

		//int w = getWidth();
		//int h = getHeight();

		int x = 0;
		int h = 0;

		//Chars chars = getChars();
		chars.setUpperLeft(10,h-100);

		model.setColor(Color.yellow.getRGB() );

		String s = dc.poll();
		if (s != null) lastReply = s;

		chars.draw(lastReply);
		chars.CR();

		model.commit();

		model.setColor(Color.cyan.getRGB() );

		chars.draw(line.toString());

		drawCursor = !drawCursor;
		if (drawCursor) chars.draw("-");

		//scaleTemp(.4f);

		//flushTemp();
		model.commit();
	}
	class MultiSwitcher implements KeyListener {

		public void keyTyped(KeyEvent e) {  }
		public void keyPressed(KeyEvent e) { }
        public void keyReleased(KeyEvent e) {

	        int code = e.getKeyCode();
	        if (code == KeyEvent.VK_BACK_SPACE) {
				backspace();
			} else if (code == KeyEvent.VK_ENTER) {
				enter();
			} else {
		        char c = e.getKeyChar();
		        line.append(c);
			}
  		}

		private void enter() {
			if (line.length() > 0) {
				System.out.println("Pushing command to executor");
				dc.add(line.toString() );
				line.setLength(0);	//clear all
			} else {
				System.out.println("Nothing entered");
			}

		}

		private void backspace() {
			int l = line.length();
			if (l == 0) return; 	//nothing to remove
			line.setLength(l -1);
		}

	}
}