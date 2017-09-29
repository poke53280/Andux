
package root;


import root.Pacer;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import io.ProxyManager;
import io.ProxyStore;
import io.Proxy;
import io.ProxyState;
import area.KeyArea;
import cm.ClusterMaster;
import cm.Evaluation;
import cm.AgentTracer;
import cm.AgentTrace;
import agent.Agency;
import agent.GuestBook;
import agent.Stay;
import agent.State;

import gfx.App;
import gfx.Chars;
import gfx.Matrix;
import gfx.Transformer;
import gfx.Model;


public class BoardView extends App {

	ClusterMaster cm = null;
	CycleVector node;

	DuxPrimitives duxPrims = null;
	int txtStep = 30;
	int rectStep = 40;

	int x1;
	int y1;

	MultiSwitcher ml = new MultiSwitcher();

	boolean evalView = false;
	boolean traceView = false;

	boolean clearTrace = false;
	boolean newTrace = false;
	boolean dispatchView = false;

	int agentView = 0;

	int counter = 0;

	//start converting this one
	public BoardView(Model m,CycleVector node, ClusterMaster cm) {
		super(m);
		this.node = node;
		this.cm = cm;
		duxPrims = new DuxPrimitives(m);
	}

	public String desc() { return "Board"; }

	public void draw() {

		counter++;			//used for blink, et.c

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
			chars.draw("NO DATA");
			//scaleTemp(.4f);
			//flushTemp();
			model.commit();
			return;
		}

		model.setColor(Color.white.getRGB() );
		chars.setUpperLeft(x1,y1);
		chars.draw(current.desc() );
		model.commit();

		y1 -= txtStep;

		drawLinkInfo(current);
		if (evalView) drawEvaled(current);

		y1 -= rectStep;
		y1 -= txtStep;

		if (dispatchView) drawDispatch(current);

		if (traceView) drawTrace();
		if (agentView > 0) 	drawAgentLog(current);


	}

	private void drawDispatch(Node current) {
		if (current == null)
			throw new IllegalArgumentException("no current");

		ProxyManager p = current.getProxyManager();
		String s = p.getInputStatus();

		model.setColor(Color.yellow.getRGB() );
		chars.setUpperLeft(x1,y1);
		chars.draw(s);
		model.commit();
		y1 -= txtStep;

	}


	private void drawAgentLog(Node current) {
		int n = 10;

		if (current == null)
			throw new IllegalArgumentException("no current");

		Agency a = current.getAgency();
		GuestBook b = a.getGB();

		Stay[] stays = b.stays();

		if(stays == null || stays.length == 0) return;

		int MAX = stays.length;

		int index = b.counter();

		y1 -= rectStep*n;


		for(int i = 0; i < n; i++) {
			index--;
			if(index < 0) index +=MAX;

			chars.setUpperLeft(x1,y1);

			State state = stays[index].state();
			if (state == null) {	//unused
				model.setColor(Color.white.getRGB() );
			} else {
				model.setColor(state.color() );
			}
			if (agentView == 1) {
				if(state == State.THRU) {
					chars.draw(".");
				} else {
					chars.draw(stays[index].overview() );
				}

			} else {
				chars.draw(stays[index].show() );

			}
			model.commit();
			y1 -= rectStep;

		}
		y1 -= rectStep;
		model.commit();
	}


	private void drawLinkInfo(Node current) {
		if (current == null)
			throw new IllegalArgumentException("no current");

		ProxyManager p = current.getProxyManager();
		ProxyStore ps = p.getNodeList();
		chars.setUpperLeft(x1,y1);

		if (ps.isLow() ) {

			if (counter%6 < 4) {
				model.setColor(Color.red.getRGB() );
				chars.draw("LOW");
			}
		}

		y1 -= txtStep;
		model.setColor(ProxyState.EXI.color() );
		duxPrims.addRects(x1,y1,ps.exi(),30);
		model.commit();
		y1 -= rectStep;

		model.setColor(ProxyState.CLS.color() );
		duxPrims.addRects(x1,y1,ps.cls(),30);
		model.commit();
		y1 -= rectStep;

		model.setColor(ProxyState.WRP.color() );
		duxPrims.addRects(x1,y1,ps.wrp(),30);
		model.commit();
		y1 -= rectStep;

		model.setColor(ProxyState.EXT.color() );
		duxPrims.addRects(x1,y1,ps.ext(),30);
		model.commit();
		y1 -= rectStep;

	}


	private void drawEvaled(Node current) {

		if (cm == null || cm.isEmpty() ) {
			model.setColor(Color.red.getRGB() );
			chars.setUpperLeft(10,10);
			chars.draw("CM: NO NODES");
			model.commit();
			return;
		}

		if (current == null)
			throw new IllegalArgumentException("no current");


		Evaluation e = cm.getEvaluation();
		if (e == null) return;	//nothing to draw
		e.eval();

		model.setColor(Color.lightGray.getRGB() );

		int known = e.getGood();
		int accurate = e.getAccurate();
		int missing = e.getMissing();
		int overhead = e.getOverHead();
		int inAccurate = known - accurate;
		int nominal = known + missing;

		int total = known+ overhead;
		if (missing + accurate + inAccurate != nominal)
			throw new IllegalStateException("bad eval data1");

		if (nominal == 0) {
			model.setColor(Color.lightGray.getRGB() );
			duxPrims.addCross(x1+12,y1+10,15);
			model.commit();
		}

		//ACC
		model.setColor(Color.green.getRGB() );
		int xtemp = duxPrims.addRects(x1,y1,accurate,30);
		model.commit();
		//INACC
		model.setColor(Color.orange.getRGB() );
		xtemp = duxPrims.addRects(xtemp,y1,inAccurate,30);
		model.commit();
		//MISSING
		model.setColor(Color.red.getRGB() );
		duxPrims.addRects(xtemp,y1,missing,30);

		model.commit();
	}

	private void drawTrace() {
		int startY = y1;


		if (cm == null || cm.isEmpty() ) {
			model.setColor(Color.red.getRGB() );
			chars.setUpperLeft(x1,y1);
			chars.draw("CM: NO NODES");
			y1 -= txtStep;
			model.commit();
			return;
		}

		AgentTracer at = cm.getTracer();

		if (clearTrace) {
			clearTrace = false;
			at.clear();
		}


		AgentTrace[] trace = at.getTrace();

		for(int i = 0; i < trace.length; i++) {
			AgentTrace t = trace[i];
			if (t != null) {

				drawSingleTrace(t, rotColor(i) );
				y1 += txtStep;
			}

		}


	}

	private static int rotColor(int i) {
		i%=3;
		if (i == 0) return Color.red.getRGB();
		if (i == 1) return Color.green.getRGB();
		if (i == 2) return Color.blue.getRGB();
		throw new IllegalStateException("impossible - bad programming");

	}

	private void drawSingleTrace(AgentTrace trace, int color) {
		chars.setUpperLeft(x1,y1);
		if (trace == null) {
			model.setColor(Color.red.getRGB() );
			chars.draw("No trace available");
			y1 -= txtStep;
			model.commit();
			return;
		}

		model.setColor(color);
		State s = trace.state();
		if (s != null) {
			chars.draw("AG-" + trace.aID() + " " + s.desc());
		} else {
			chars.draw("AG-" + trace.aID() + " INCOMPLETE");
		}
		model.commit();

		//addLabel("AG-" + trace.aID(),x1,y1,1f );
		//y1 += txtStep;

		/*
		for(int i = 0; i < trace.size(); i++) {

			Node n = (Node) trace.elementAt(i);
			addLabel("#" + (i+1) + ":" + n.desc(),x1,y1,1f );
			y1 += txtStep;
		}
		*/
/*
		State s = trace.state();
		if (s != null) {
			addLabel(s.desc(),x1,y1,1f );
		} else {
			addLabel("INCOMPLETE",x1,y1,1f);
		}
		y1 += txtStep;

*/
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
				Node n = (Node) node.next();
			}
			if (code == KeyEvent.VK_Z) {
				Node n = (Node) node.previous();
			}

			if (code == KeyEvent.VK_Y) {
				newTrace = true;
			}

			if (code == KeyEvent.VK_E) {
				evalView = !evalView;
			}
			if (code == KeyEvent.VK_T) {
				traceView = !traceView;
			}

			if (code == KeyEvent.VK_D) {
				dispatchView = !dispatchView;
			}


			if (code == KeyEvent.VK_C) {
				agentView++;
				agentView %=3;
			}

			if (code == KeyEvent.VK_R) {
				clearTrace = true;
			}
  		}
	}
}
