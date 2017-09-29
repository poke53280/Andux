package display;

import java.awt.Frame;
import java.awt.Panel;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.awt.event.TextEvent;
import java.awt.event.ActionEvent;

import java.awt.Component;

import java.awt.Button;

import java.awt.EventQueue;
import java.awt.Toolkit;

import java.awt.FlowLayout;

import java.awt.event.TextListener;

public class FrameTest extends Panel {


	public FrameTest() {
		super();
		setLayout(null);

		TextField f = new TextField("TextField",30);
		f.setBounds(30,100,30,20);
		add(f);

		Button b = new FakeButton("CONTROL");
		b.setBounds(100,100,30,30);
		add(b);

		setSize(new Dimension(300,300));
		setVisible(true);

		enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.TEXT_EVENT_MASK);

	}

	public void paint(Graphics g) {
		g.drawString("Click here, then type", 150,150);
	}

	public void processKeyEvent(KeyEvent e) {
		System.out.println("FrameTest.processKeyEvent");
		super.processKeyEvent(e);
	}

	public static void main(String[] args) {
		Frame f = new Frame();
		FrameTest t = new FrameTest();

		f.add(t);
		f.setSize(new Dimension(350,350));
		f.setVisible(true);

	}

	public class FakeButton extends Button {
		FakeButton(String name) {
			super(name);
			enableEvents(AWTEvent.ACTION_EVENT_MASK);
		}
		public void processActionEvent(ActionEvent e) {
			getParent().requestFocus();
			super.processActionEvent(e);
		}
	}

}

