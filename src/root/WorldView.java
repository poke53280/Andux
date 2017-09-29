
package root;

import java.awt.Color;
import java.awt.Component;

import java.awt.event.KeyEvent;
import area.KeyArea;
import area.KeyAreaSet;
import area.Area;
import area.WarpArea;
import java.awt.event.KeyListener;
import java.util.Vector;

import io.ProxyManager;
import io.ProxyStore;
import io.Proxy;
import io.ProxyState;

import agent.Agency;
import agent.Stay;
import agent.State;
import cm.ClusterMaster;
import cm.AgentTracer;
import cm.AgentTrace;

import area.MovingProvider;
import root.MovingNode;


import gfx.App;
import gfx.Chars;
import gfx.Matrix;
import gfx.Transformer;
import gfx.Model;



import java.util.Enumeration;

public class WorldView extends App {
	//Model model;
	private DuxPrimitives prim;
	protected final int L = SpaceMax.HORIZON;

	ClusterMaster cm = null;
	CycleVector node;
	MultiSwitcher ml = new MultiSwitcher();

	boolean exactView = false;
	boolean follow = true;
	int links = 0;

	boolean clip = true;
	boolean localInfo = false;
	boolean grid = false;
	boolean desiredWarps = false;
	boolean agentView = false;
	boolean traceView = false;
	boolean clearTrace = false;
	boolean newTrace = false;

	int txtStep = 30;
	int rectStep = 40;

	public WorldView(Model m, CycleVector node,	 ClusterMaster cm) {

		super(m);
		this.node = node;
		this.cm = cm;
		prim = new DuxPrimitives(m);

	}

	//public String desc() { return "WorldView " + size(); }

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

		Transformer tr = model.transformer();

		Matrix m = tr.matrix();
		tr.pushMatrix();




		tr.begin();
			if (follow) {
				Node current = (Node) node.current();
				if (current != null) {
					KeyArea local = current.getKeyArea();
					m.translate((float)	-local.getX(),
								(float) -local.getY(),
								0f);
				}
			}
			m.scale(0.1f);
		tr.end();


			if(grid) drawGrid();

			if (exactView) drawExact();
			drawLocalSet();

			if (desiredWarps) drawDesiredWarps();
			if (traceView) drawTrace();

		tr.popMatrix();

	}


	private void drawTrace() {
		Node current = (Node) node.current();
		if (current == null) return;

		KeyArea local = current.getKeyArea();

		int x1 = local.getX();
		int y1 = local.getY();

		if (cm == null || cm.isEmpty() ) {
			model.setColor(Color.red.getRGB() );

			chars.setUpperLeft(x1,y1);
			chars.draw("CM: NO NODES");

			y1 += txtStep;
			model.commit();

			return;
		}


		AgentTracer at = cm.getTracer();

		if (clearTrace) {
			clearTrace = false;
			at.clear();
		}


		AgentTrace[] trace = at.getTrace();

		for (int i = 0; i < trace.length; i++) {
			AgentTrace t = trace[i];
			if (t != null) drawSingleTrace(t,rotColor(i), x1, y1 );
		}
	}

	private static int rotColor(int i) {
			i%=3;
			if (i == 0) return Color.red.getRGB();
			if (i == 1) return Color.green.getRGB();
			if (i == 2) return Color.blue.getRGB();
			throw new IllegalStateException("impossible - bad programming");

	}


	private void drawSingleTrace(AgentTrace trace, int color, int x1, int y1) {
		if (trace == null || trace.isEmpty() ) {
			model.setColor(Color.red.getRGB() );
			chars.setUpperLeft(x1,y1);
			chars.draw("No trace available");
			model.commit();
			y1 += txtStep;
			return;
		}

		model.setColor(color);
		Node start = (Node) trace.elementAt(0);
		KeyArea a = start.getKeyArea();
		x1 = a.getX();
		y1 = a.getY();

		int x2 = x1;
		int y2 = y1;

		int A = model.addVertex(x1,y1);
		for(int i = 1; i < trace.size(); i++) {

			Node n = (Node) trace.elementAt(i);
			a = n.getKeyArea();
			x2 = a.getX();
			y2 = a.getY();
			int B = model.addVertex(x2,y2);
			model.addLine(A,B);
			A = B;
			//if (i%10 == 0) model.commit();
		}

		model.commit();

	}


	private void drawLocalSet() {
		drawLocalCells();

		Node current = (Node) node.current();
		if (current == null) return;

		KeyArea local = current.getKeyArea();
		ProxyManager p = current.getProxyManager();
		ProxyStore ps = p.getNodeList();

		model.setColor(Color.white.getRGB() );
		prim.addArea(local);

		model.commit();
		//flushTemp();

		if(localInfo) {
			nodeInfo(current);
		}


/*
		if (follow) {
			setCenterPoint(local.getX(),local.getY());
			setFixed(100,100);
		}
*/

		drawSet(ps, local);

	}


	private void nodeInfo(Node current) {
		if (current == null)
			throw new IllegalStateException("node is null");

		KeyArea local = current.getKeyArea();
		int x1 = local.getX() - L +8;
		int y1 = local.getY() - L +8;

		y1 += txtStep;

		ProxyManager p = current.getProxyManager();
		ProxyStore ps = p.getNodeList();
		Enumeration e = ps.elements();
		while(e.hasMoreElements() ) {
			Proxy p_ = (Proxy) e.nextElement();

			ProxyState s = p_.proxyState();
			model.setColor(s.color());

			int rx = p_.getX();
			int ry = p_.getY();

			int decay = p_.getDecay();
			long output = p_.outputCount();
			if (p_.isExiled() ) {
				prim.addRotor(x1,y1,100,output);
				if (decay < 47) {
					prim.addBar(x1-250,y1-250,30,5*decay);
				}
			} else {
				prim.addRotor(rx,ry,100,p_.outputCount());
				if (decay < 47) {
					prim.addBar(rx-100,ry-70,30,5*decay);
				}
			}
			model.commit();
		}
	}


	private void drawSet(ProxyStore ps,  KeyArea local) {
		if (ps == null || ps.isEmpty() ) return;

		if (links > 0) {

			int x1 = local.getX();
			int y1 = local.getY();

			int A = model.addVertex(x1,y1);

			int lineCount = 0;
			Enumeration e = ps.elements();
			while(e.hasMoreElements() ) {
				Proxy a = (Proxy) e.nextElement();

				ProxyState s = a.proxyState();
				if (links < 2 && s != ProxyState.WRP) continue;	//draw warp links only

				model.setColor(s.color());

				int x2;
				int y2;
				if(s == ProxyState.EXI) {
					x2 = x1 + (int) (SpaceMax.FAR*SpaceMax.HORIZON);
					y2 = y1 + (int) (SpaceMax.FAR*SpaceMax.HORIZON);
				} else {
					x2 = a.getX();
					y2 = a.getY();
				}

				int B = model.addVertex(x2,y2);
				model.addLine(A,B);
				lineCount++;

				//if (lineCount%10 == 0) model.commit();
			}
			model.commit();
			//if (lineCount > 0) flushTemp();
		}

		Enumeration e = ps.elements();
		while(e.hasMoreElements() ) {
			Proxy a = (Proxy) e.nextElement();
			ProxyState s = a.proxyState();
			model.setColor(s.color());

			prim.addCross(a);
			model.commit();
			//flushTemp();


		}

	}


	private void drawExact() {

		Node current = (Node) node.current();
		if (current == null) return;
		KeyArea local = current.getKeyArea();

		//the FAR boundary
		int x1 = local.getX();
		int y1 = local.getY();
		model.setColor(Color.darkGray.getRGB());

		//clipping (if any) at FAR distance.
		int clipDistance = (int) (SpaceMax.FAR*SpaceMax.HORIZON);
		prim.addRect(x1-clipDistance, y1-clipDistance,
				clipDistance*2, clipDistance*2);

		//flushTemp();
		model.commit();

		for (int i = 0; i < node.size(); i++) {
			Node n = (Node) node.elementAt(i);
			KeyArea a = n.getKeyArea();

			if (clip && a.horizons(local) > SpaceMax.FAR) {
				continue;	//far away - skip
			}

			model.setColor(Color.darkGray.getRGB() );
			prim.addArea(a);
			//flushTemp();

			int x = a.getX();
			int y = a.getY();

			if (agentView) {

				Agency ag = n.getAgency();
				if (ag.processed() > 0) {
					model.setColor(Color.orange.getRGB() );
					prim.addCrosses(x-200, y-200, ag.processed(), 30);
				}
			}
			model.commit();
		}

		//drawFrame(Color.darkGray.getRGB(),
		//		-SpaceMax.H,-SpaceMax.H,SpaceMax.H,SpaceMax.H);


	}

	private void drawGrid() {

			model.setColor(Color.darkGray.getRGB() );

			float offX = 0f;
			float offY = 0f;
			float spacing = 400f;

			Node current = (Node) node.current();
			if (current == null) return;
			KeyArea local = current.getKeyArea();

			int locX = local.getX();
			int locY = local.getY();

			locX  -=  (locX%spacing);
			locY -= (locY%spacing);

			offX = (float)  locX;
			offY = (float) locY;

			float range = SpaceMax.FAR*SpaceMax.HORIZON;

			int w = 100;
			int count = 0;
			for (float px = -range +offX+spacing; px < range+offX; px +=spacing) {
					for (float py = -range+offY+spacing; py < range+offY; py +=spacing) {
						prim.addRect((int)px,(int)py,w,w);
						count++;
						if (count %10 == 0 ) model.commit();
					}
			}
			model.commit();

	}


	//world frame and all warp cell frames
	private void drawCells() {
		model.setColor(Color.darkGray.getRGB() );
		//drawFrame(Color.darkGray.getRGB(),
		//		-SpaceMax.H,-SpaceMax.H,SpaceMax.H,SpaceMax.H);


		int l = SpaceMax.levels();
		if (l == 0) return; 	//warp not running

		//place some << stuff here instead
		float spacing = (float) (2f*SpaceMax.H/Math.pow(2.0,l) );

		int count = 0;
		for (float p = -SpaceMax.H+spacing; p < SpaceMax.H; p +=spacing) {

			int A = model.addVertex(p,-SpaceMax.H);
			int B = model.addVertex(p,SpaceMax.H);

			int C = model.addVertex(-SpaceMax.H,p);
			int D = model.addVertex(SpaceMax.H,p);

			model.addLine(A,B);
			model.addLine(C,D);
			count++;
			if(count %10 == 0) model.commit();
		}
		model.commit();

	}

	private void drawDesiredWarps() {
		Node current = (Node) node.current();
		if (current == null) return;

		int l = SpaceMax.levels();
		if (l == 0) return; 	//warp not running

		ProxyManager pm = current.getProxyManager();
		WarpArea[] dw = pm.getDesiredWarps();
		if (dw == null) return;

		model.setColor(Color.yellow.getRGB());
		for(int i = 0; i < dw.length; i++) {
			WarpArea wa = dw[i];
			prim.addMark(wa);
		}
		model.commit();
		//flushTemp();

	}


	private void drawLocalCells() {
		Node current = (Node) node.current();
		if (current == null) return;

		int l = SpaceMax.levels();
		if (l == 0) return; 	//warp not running

		model.setColor(Color.darkGray.getRGB() );

		prim.addCross(0,0,(int)SpaceMax.H);

		//if (l == 1) return;		//the cross was first level

		KeyArea local = current.getKeyArea();
		WarpArea[] quarter = SpaceMax.getQuarter((Area) local);

		//draws not the finest level. This draw routine uses
		//previous level to draw the current.
		for (int i = 0; i < l-1;i++) {
			prim.addCross(quarter[i]);
		}

		//small cross to indicate cell where node is located.
		//addMark(quarter[l-1]);

		model.commit();
		//flushTemp();
	}


	class MultiSwitcher implements KeyListener {

		public void keyTyped(KeyEvent e) {  }
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();

			if (code == KeyEvent.VK_RIGHT ||
				code == KeyEvent.VK_LEFT ||
				code == KeyEvent.VK_UP ||
				code == KeyEvent.VK_DOWN) {

				MovingNode mn = (MovingNode) node.current();
				MovingProvider mp = mn.getMovingProvider();
				if (code == KeyEvent.VK_RIGHT) mp.east();
				if (code == KeyEvent.VK_LEFT) mp.west();
				if (code == KeyEvent.VK_UP) mp.north();
				if (code == KeyEvent.VK_DOWN) mp.south();

			}

		}
        public void keyReleased(KeyEvent e) {

	        int code = e.getKeyCode();

	        if (code == KeyEvent.VK_X) {
				Node n = (Node) node.next();

				MovingNode mn = (MovingNode) n;
				MovingProvider mp = mn.getMovingProvider();

			}



			if (code == KeyEvent.VK_Z) {
				Node n = (Node) node.previous();
			}

			if (code == KeyEvent.VK_B) {
				agentView = !agentView;
			}

			if (code == KeyEvent.VK_F) {
				follow = !follow;
			}

			if (code == KeyEvent.VK_L) {
				links++;
				links%=3;
			}

			if (code == KeyEvent.VK_Y) {
				newTrace = true;
			}

			if (code == KeyEvent.VK_C) {
				clip = !clip;
			}

			if (code == KeyEvent.VK_G) {
				grid = !grid;
			}

			if (code == KeyEvent.VK_N) {
				exactView = !exactView;
			}

			if (code == KeyEvent.VK_I) {
				localInfo = !localInfo;
			}

			if (code == KeyEvent.VK_D) {
				desiredWarps = !desiredWarps;
			}

			if (code == KeyEvent.VK_T) {
				traceView = !traceView;
			}

			if (code == KeyEvent.VK_R) {
				clearTrace = true;
			}

  		}
	}
}