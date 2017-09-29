
package root;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Color;

import gfx.App;
import gfx.Chars;
import gfx.Matrix;
import gfx.Transformer;
import gfx.Model;

import java.awt.Component;

public class TalkView extends App  {

	CycleVector node;
	int txtStep = 30;

	MultiSwitcher ml = new MultiSwitcher();

	StringBuffer line = new StringBuffer(50);

	int x1;
	int y1;

	public TalkView(Model m, CycleVector node) {
		super(m);
		this.node = node;
		//l.skipLetters(true);
	}

	public String desc() { return "ChatView"; }

	public void draw() {

		//int w = getWidth();
		//int h = getHeight();

		x1 = 0;
		y1 = 0;

		//Chars chars = getChars();
		chars.setUpperLeft(x1,y1);

		Node current = null;
		if (node != null) current = (Node) node.current();

		if (current == null) {
			model.setColor(Color.red.getRGB() );
			chars.draw("Chatview");
			//scaleTemp(.4f);
			//flushTemp();
			model.commit();
			return;
		}

		ChatCentral chat = current.getChat();

		ChatLine[] com = chat.getData();
		int index = chat.counter();
		int MAX = com.length;

		int n = MAX;

		for(int i = 0; i < n; i++) {

			ChatLine cl = com[index];
			if (cl.isLocal()) model.setColor(Color.cyan.getRGB());
			else			  model.setColor(Color.white.getRGB());

			chars.setUpperLeft(x1,y1);
			chars.draw(cl.show() );
			model.commit();

			index++;
			if(index >= MAX) index = 0;			y1 -= txtStep;
		}

		y1 -= txtStep;
		if (line.length() > 0) {
			chars.setUpperLeft(x1,y1);
			chars.draw(line.toString() );
		}
		model.commit();
	}

	private void say(String s) {
		if (s == null || s.length() == 0) return;
		Node current = (Node) node.current();
		if (current == null) return;

		ChatCentral chat = current.getChat();
		chat.output(s);

		//System.out.println("Chatview: Sent " + s);
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
				say(line.toString() );
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