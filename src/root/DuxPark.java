
package root;


import cmd.Telnetd;
import cmd.DisplayCom;
import java.awt.Point;
import java.util.Vector;
import java.text.DecimalFormat;

import net.Connector;
import net.NetConnector;
import net.VMConnector;
import net.IPString;
import net.MessageIO;

import io.ProxyManager;
import io.Proxy;

import area.AreaProvider;
import area.Area;

import agent.Agency;

import statistics.Buffer;
import statistics.Sampler;
import statistics.SampleProvider;

import java.awt.event.KeyListener;

import cm.ClusterMaster;
import cm.ClusterState;
import cm.AgentTracer;

import gfx.Desk;
import gfx.Model;
import gfx.App;
import gfx.Rasterizer;


public class DuxPark {

	private Sampler sizeSampler;
	private Sampler outTickSampler;
	private Sampler unknownSampler;
	private Sampler goodCountSampler;
	private Sampler accurateCountSampler;
	private Sampler perfectCountSampler;
	private Sampler missPercentSampler;
	private Sampler totalSampler;
	private Sampler densitySampler;
	private Sampler completeSampler;

	private Desk desk2 = null;

	private int period = 50;

	private long startTime;
	private long runTime = 0;
	private long counter = 0;
	private long tmpCounter = 0;

	private boolean screenOn = true;

	private char nextNodes = 'a';

	private DecimalFormat percentFormatter = new DecimalFormat();

	Factory f = new Factory();
	Pacer pacer = new Pacer(period);
	Telnetd shell = null;

	Configurator cfg;
	Connector c = null;
	CycleVector node = new CycleVector();
	ClusterMaster cm;
	private float density = WorldSize.DENSITY;	//nodes/MPixel
	private boolean autoEval = true;
	private boolean frozen = false;
	private boolean contactAll = false;

	public DuxPark(Connector c, ClusterConfig cc) {
		this(c);


		setPeriod(cc.period);
		autoEval = cc.autoEval;
		contactAll = cc.contactAll;
		density = cc.density;
		cfg.setSpeed((float) cc.speed);

		cfg.setAgent(cc.agentFreq);

	}

	public String config() {

		StringBuffer b = new StringBuffer(200);
		b.append("\nperiod =" + period);
		b.append("\nautoeval = " + autoEval);
		b.append("\ncontactAll = " + contactAll);
		b.append("\ndensity/10 = " + density);

		return b.toString();
	}


	private DuxPark(Connector c) {

		percentFormatter.applyPattern("##0.00");

		this.c = c;
		c.registerCommands(f,"/con/");

		f.add("/add", new AddMany() );

		f.add("/period", new Period() );
		f.add("/uptime", new Uptime() );
		f.add("/threads", new Threads() );
		f.add("/show", new Show() );
		f.add("/draw", new Draw() );
		f.add("/agentrun", new AgentFreq() );
		f.add("/terminate", new Terminate() );
		f.add("/size", new Size() );
		f.add("/density", new DensitySet() );
		f.add("/far", new FarSet() );
		f.add("/autoeval", new AutoEval() );
		f.add("/freeze", new Freeze() );
		f.add("/contactall", new ContactAll() );
		f.add("/shuffle", new Shuffle() );

		cfg = new Configurator(c,f);

		cfg.registerCommands("/cfg/");

		SampleProvider sp = c.getHub();
		if (sp != null) {
			outTickSampler = new Sampler(sp,400, "out per tick and node");
		}

		sizeSampler = new Sampler(_size(),400,"number of nodes");

		cm = new ClusterMaster(node, 30, counter);
		cm.registerCommands(f,"/cm/");
		startSamplers();
	}

	private void startTelnetd() {
		shell = new Telnetd(WorldSize.VERSION);
		shell.registerCommands(f, "/t/");
	}


	private void toggleScreenOn() { screenOn = !screenOn; }

	public Telnetd getShell() {
		return shell;
	}


	public static void main (String[] args) {
		Connector ip = new NetConnector(IPString.getSID("127.0.0.1", WorldSize.NETWORK_PORT) );

		Connector vm = new VMConnector(IPString.getSID("127.0.0.1",
						WorldSize.NETWORK_PORT) );

		Connector c = null;

		if (args != null && args.length > 0 && args[0].equals("IP") ) {
			c = ip;
		}

		if (args != null && args.length > 0 && args[0].equals("VM") ) {
			c = vm;
		}

		if (c == null) {
			System.out.println("DuxPark IP|VM");
		} else {

			ClusterConfig cc = new ClusterConfig();

			DuxPark p = new DuxPark(c,cc);
			p.startTelnetd();


			//Starts with no graphics, and a running shell on console:
			Telnetd t = p.getShell();
			t.startConsole("#");

			//p.setupDisplay(4);

			p.preRun();
			while(true) {
				p.tick();
			}


		}
	}

	private void startSamplers() {

		ClusterState cs = cm.getClusterState();
		unknownSampler = new Sampler(cs.missCount(),400, "missing");
		goodCountSampler = new Sampler(cs.goodCount(), 400, "known");
		accurateCountSampler = new Sampler(cs.accurateCount(), 400, "accurate");
		missPercentSampler = new Sampler(cs.missPercent(), 400, "miss (percent)");

		perfectCountSampler = new Sampler(cs.perfAve(), 400, "nominal pr node");

		totalSampler = new Sampler(cs.totalAve(), 400, "total pr node");
		densitySampler = new Sampler(cs.coverage(),400,"density (percent)");
		completeSampler = new Sampler(cs.completePercent(),400,
											"complete hood (percent)");

	}


	private void adjustSize(int extra) {
		//Extra: Adjust so density is correct with 'extra' more nodes.
		if (extra < 0) throw new IllegalArgumentException("extra < 0");

		float d = 0f;

		if (extra == 0) {
			d = cm.sizeAtDensity(density);
		} else {
			d = cm.sizeAtDensity(density,extra);
		}

		if (d == 0f) return;	//No adjust (world empty)

		int size = (int) d;

		if (SpaceMax.H != size) {
			setSize(size);
			System.out.println("#DuxPark.adjustSize: H=" + size);
		}
	}

	//Reposition all running nodes
	public void shuffle() {
		for (int i = 0; i < node.size(); i++) {
			MovingNode n = (MovingNode) node.elementAt(i);
			n.shuffle();
		}

	}


	//Returns last sampled value for output per node, VM only
	public float output() {
		SampleProvider sp = c.getHub();
		if (sp == null)
			throw new IllegalStateException("Not in VM mode");
		return sp.sampleValue();
	}

	public float outputTick() {
		SampleProvider sp = c.getHub();
		if (sp == null)
					throw new IllegalStateException("Not in VM mode");
		return sp.sampleValue();

	}

	public long getCounter() { return counter; }

	public void preRun() {
		startTime = System.currentTimeMillis();
		counter = 0L;
		tmpCounter = 0L;
	}

	public void closeShell() {
		if (shell != null) shell.close();
	}

	public ClusterMaster getClusterMaster() {
		return cm; 	//May be null
	}



	public void tick() {

		pacer.launch();
		long t = System.currentTimeMillis();

		adjustSize(0);

		//if (screen != null && screenOn) screen.repaint();

		if (shell != null) shell.execute();

		if (outTickSampler != null) {
			c.setTick(counter);
			outTickSampler.sample(10);
		}

		sizeSampler.sample(10);

		cm.setTick(counter);

		AgentTracer at = cm.getTracer();
		at.update();

		if (autoEval) {

			unknownSampler.sample(10);
			goodCountSampler.sample(10);
			accurateCountSampler.sample(10);
			missPercentSampler.sample(10);
			perfectCountSampler.sample(10);
			totalSampler.sample(10);
			densitySampler.sample(10);
			completeSampler.sample(10);

			boolean didEval = cm.perhapsEval();

		}

		if(!frozen) {
			for (int i = 0; i < node.size(); i++) {
				Runnable r = (Runnable) node.elementAt(i);
				r.run();
			}
		}


		if (desk2 != null && screenOn) desk2.draw();

		runTime += (System.currentTimeMillis() - t);
		counter++;
		tmpCounter++;
	}

	public int size() { return node.size(); }

	private SampleProvider _size() {
		return new SampleProvider() {
			public float sampleValue() {
				return (float) node.size();
			}
		};
	}

	//Change agent launch freq of all nodes
	private void setLaunchFreq(int f) {

		for (int i = 0; i < node.size(); i++) {
			Node n = (Node) node.elementAt(i);
			Agency a = n.getAgency();
			a.setLaunchFreq(f);
		}
	}


	private void contactAll(Node n) {

		for (int i = 0; i < node.size(); i++) {
			Node _n = (Node) node.elementAt(i);
			contact(_n,n);
		}

	}

	public void contact(Node n1, Node n2) {

		if (n1 == n2) {
			System.out.println("Node: cannot contact self");
			return;
		}

		ProxyManager pm1 = n1.getProxyManager();
		ProxyManager pm2 = n2.getProxyManager();

		pm1.contact(pm2.getSid() );

	}


	public void setupDisplay(int mode) {

		if (mode < 1 || mode > 7)
			throw new IllegalArgumentException("bad mode: " + mode);


		if(desk2 != null) return; 	//Already set up

		if (mode == 1) desk2 = new Desk(400,400);
		if (mode == 2) desk2 = new Desk(200,200);
		if (mode == 3) desk2 = new Desk(1280,1024);
		if (mode == 4) desk2 = new Desk(700,700);

		if (mode == 5) {
			desk2 = new Desk(400,400);
			Rasterizer r = desk2.raster();
			r.setAntiAlias(true);
		}
		if (mode == 6) {
			desk2 = new Desk(320,200);
			Rasterizer r = desk2.raster();
			r.setMotionBlur(true);
			r.setAntiAlias(true);
		}

		if (mode == 7) {
			desk2 = new Desk(1024,768);
		}

		if (desk2 == null)
			throw new IllegalStateException("desk is null");


		desk2.registerCommands("/desk/",f);

		CycleVector cv = new CycleVector();
		if (outTickSampler != null) cv.addElement(outTickSampler.getBuffer());

		cv.addElement(sizeSampler.getBuffer());

		if (cm != null) {
			cv.addElement(unknownSampler.getBuffer());
			cv.addElement(perfectCountSampler.getBuffer() );
			cv.addElement(missPercentSampler.getBuffer() );
			cv.addElement(goodCountSampler.getBuffer() );
			cv.addElement(accurateCountSampler.getBuffer() );
			cv.addElement(totalSampler.getBuffer() );
			cv.addElement(densitySampler.getBuffer() );
			cv.addElement(completeSampler.getBuffer() );
		}

		DisplayCom dc = shell.startDisplayCom();


		Model m = desk2.getModel();
		if (m == null)
			throw new IllegalStateException("Desk returned null model");

		App a = new App(m);
		desk2.add(a, "app", 30f,30f);

		a = new WorldView(m,node,cm);
		desk2.add(a,"firstapp",-100f,0f);

		a = new TalkView(m,node);
		desk2.add(a,"talkview",50f,-50f);

		a = new CommandView(m,dc);
		desk2.add(a,"commandview", 40f,200f);

		a = new BoardView(m,node,cm);
		desk2.add(a,"boardview", -20f, 100f);

		//Model m,  CycleVector v, String name
		a = new GraphView(m,cv, "Graph1");
		desk2.add(a,"Graph1", 90f, -90f);
	}

	public void addSeveral(String baseName, int number) {

		//Resize world
		adjustSize(number);

		//Until loop has completed, the density in the system
		//is wrong

		if (baseName == null){
			baseName = "" + nextNodes;
			//System.out.println("assigning base name: '" + baseName + "'");
			nextNodes++;
		}

		for (int i = 0; i < number; i++) {
			String name = baseName + i;

			Node n = cfg.createNode(name);

			if(contactAll) contactAll(n);
			node.addElement(n);
		}


	}

	private void setPeriod(int p) {
		if (p >= 0) {   //= 0 means no wait
			period = p;
			pacer.setInterval(p);
		}
	}

	private String listThreads() {

		StringBuffer b = new StringBuffer(150);
		Thread[] threads = new Thread[80];
		int max = Thread.enumerate(threads);
		for (int i = 0; i < max; i++) {

			Thread t = threads[i];
			b.append(t.toString() );
			b.append('\n');
		}
		return b.toString();
	}


	class Threads extends Command {

		public Threads() {
			setUsage("threads");
		}

		public Command create(String[] args) {
			Command c = new Threads();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			isValid = true;
		}

		public void execute() {
			setResult(listThreads() );
		}
	}

	class AddMany extends Command {

		private String basename;
		private int number;

		public AddMany() {
			setUsage("add [number] [basename]");
		}

		public Command create(String[] args) {
			Command c = new AddMany();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			basename = null;
			number = 0;
		}

		public void execute() {
			if (isValid ) {
				addSeveral(basename,number);
				setResult("" + number + " node(s) added");
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();

			if (args.length >= PAR1 + 1 ) {
				number = ParseUtil.parseInt(args[PAR1]);
				if (number < 1) {
					isValid = false;
				} else {
					isValid = true;
				}

				if (args.length >= PAR2 +1) {
					basename = args[PAR2];
				}

			} else {
				isValid = false;
			}
		}
	}

	private void setSize(int h) {
		SpaceMax.setSize(h);

	}


	private String uptime() {

		long up = System.currentTimeMillis() - startTime;
		String usage = percentFormatter.format(new Double((double)100.0*runTime/(double)up) );

		long cycleTime = tmpCounter * period;


		long realPeriod = 0L;
		if (tmpCounter > 0) realPeriod = runTime/tmpCounter;	//ms time per tick

		//Reset
		runTime = 0L;
		startTime = System.currentTimeMillis();
		tmpCounter = 0L;


		long deltaReal = up - cycleTime;	//Time that really has passed minus time that should have
											//passed if tick launched at exactly the right intervals.


		String del = percentFormatter.format(new Double((double)100.0*deltaReal/(double)up) );

		return "n=" + node.size() + ", " + sizeInfo() + ", T=" + period + "ms, sampleT="
							+ (up/1000) + "s, run%=" + usage + ", late%=" + del + ", Treal=" + realPeriod;

	}

	private String sizeInfo() {

		return "H=" + SpaceMax.H + ",#warps= " + SpaceMax.levels();
	}


	class Uptime extends Command {

		public Uptime() {
			setUsage("uptime");
		}

		public Command create(String[] args) {
			Command c = new Uptime();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			isValid = true;
		}

		public void execute() {
			setResult(uptime() );
		}
	}

	class Size extends Command {


		int h;

		public Size() {
			setUsage("size h");
		}

		public Command create(String[] args) {
			Command c = new Size();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			init();
			if (args.length != PAR1 +1) {
				isValid = false;

			} else {
				h = ParseUtil.parseInt(args[PAR1]);
				if (SpaceMax.sizeOK(h) ) {
					isValid = true;
				} else {
					isValid = false;
				}
			}
		}

		public void execute() {
			if (isValid ) {
				setSize(h);
				setResult("world size set to h +/-" + h);
			} else {
				setResult(showUsage() );
			}
		}
	}

	class Draw extends Command {

		public Draw() {
			setUsage("draw");
		}

		public Command create(String[] args) {
			Command c = new Draw();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			isValid = true;
		}

		public void execute() {
			toggleScreenOn();
			if (screenOn) {
				setResult("Screen update ON");
			} else {
				setResult("Screen update OFF");
			}
		}
	}

	class Shuffle extends Command {

		public Shuffle() {
			setUsage("shuffle");
		}

		public Command create(String[] args) {
			Command c = new Shuffle();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			isValid = true;
		}

		public void execute() {
			shuffle();
			setResult("nodes shuffled");
		}
	}
	class AgentFreq extends Command {

		private int freq;

		public AgentFreq() {
			setUsage("agentfreq <run-each,0=never>");
		}

		public Command create(String[] args) {
			Command c = new AgentFreq();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			init();
			if (args.length == PAR1 + 1 ) {
				freq = ParseUtil.parseInt(args[PAR1]);
				if (freq <= -1) {
					isValid = false;
				} else {
					isValid = true;
				}
			} else {
				isValid = false;
			}
		}
		public void execute() {
			if (isValid) {
				setLaunchFreq(freq);
				if (freq == 0) {
					setResult("all nodes: agent auto launch disabled");
				} else {
					setResult("all nodes: launching every " + freq + " run(s)");
				}
			} else {
				setResult(showUsage() );
			}
		}
	}

	class Terminate extends Command {

		public Terminate() {
			setUsage("terminate");
		}

		public Command create(String[] args) {
			Command c = new Terminate();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			isValid = true;
		}

		public void execute() {
			System.exit(0);
		}
	}

	class AutoEval extends Command {

		public AutoEval() {
			setUsage("autoeval");
		}

		public Command create(String[] args) {
			Command c = new AutoEval();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			isValid = true;
		}

		public void execute() {
			autoEval = !autoEval;
			setResult("Auto eval set to : " + autoEval);
		}
	}

	class Freeze extends Command {

		public Freeze() {
			setUsage("freeze");
		}

		public Command create(String[] args) {
			Command c = new Freeze();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void setArgs(String[] args) {
			isValid = true;
		}

		public void execute() {
			frozen = !frozen;
			if(frozen) setResult ("Frozen - ticks halted");
			else	   setResult ("All nodes running");
		}
	}



	class Period extends Command {

		int delay = -1;

		public Period() {
			setUsage("period <ms>");
		}

		public Command create(String[] args) {
			Command c = new Period();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			delay = -1;
		}

		public void setArgs(String[] args) {
			init();
			if (args.length != PAR1 +1) {
				isValid = false;

			} else {
				delay = ParseUtil.parseInt(args[PAR1]);
				if (delay == -1) {
					isValid = false;
				} else {
					isValid = true;
				}
			}
		}

		public void execute() {
			if (isValid) {
				setPeriod(delay);
				setResult("");
			} else {
				setResult(showUsage() );
			}

		}
	}

	class DensitySet extends Command {

		int d = -1;

		public DensitySet() {
			setUsage("density <nodes:100000/Mpixel>");
		}

		public Command create(String[] args) {
			Command c = new DensitySet();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			d = -1;
		}

		public void setArgs(String[] args) {
			init();
			if (args.length != PAR1 +1) {
				isValid = false;

			} else {
				d = ParseUtil.parseInt(args[PAR1]);
				if (d <= 0) {
					isValid = false;
				} else {
					isValid = true;
				}
			}
		}

		public void execute() {
			if (isValid) {
				if (cm == null) {
					setResult("cm not running, can't change density");
				} else {
					density = d/10000000f;
					setResult("density set to " + density + " nodes/Mpixel");
				}
			} else {
				if (cm == null) {
					setResult("cm not running, no density set");
				} else {
					setResult("current density maintained:" + density + " nodes/Mpixel");
				}
			}

		}
	}

	class FarSet extends Command {

		int d = -1;

		public FarSet() {
			setUsage("far <horizons>");
		}

		public Command create(String[] args) {
			Command c = new FarSet();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			d = -1;
		}

		public void setArgs(String[] args) {
			init();
			if (args.length != PAR1 +1) {
				isValid = false;

			} else {
				d = ParseUtil.parseInt(args[PAR1]);
				if (d <= 0) {
					isValid = false;
				} else {
					isValid = true;
				}
			}
		}

		public void execute() {
			if (isValid) {
				if ((float) d > SpaceMax.NEAR) {
					SpaceMax.setFar((float) d);
					setResult("far limit set to " + SpaceMax.FAR + " horizons");
				} else {
					setResult("FAR must be larger than NEAR. Not changed.");
				}

			} else {
				setResult("horizon FAR is " + SpaceMax.FAR);
			}

		}
	}

	class Show extends Command {

		int mode = -1;

		public Show() {
			setUsage("show <mode=1,2,3,4,5,6>");
		}

		public Command create(String[] args) {
			Command c = new Show();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			mode = -1;
		}

		public void setArgs(String[] args) {
			init();
			if (args.length != PAR1 +1) {
				isValid = false;

			} else {
				mode = ParseUtil.parseInt(args[PAR1]);
				if (mode < 1 || mode > 7) {
					isValid = false;
				} else {
					isValid = true;
				}
			}
		}

		public void execute() {
			if (isValid) {
				setupDisplay(mode);
				setResult("Displayed launched, mode=" + mode);
			} else {
				setResult(showUsage() );
			}

		}
	}

	class ContactAll extends Command {

		public ContactAll() {
			setUsage("contactall");
		}

		public Command create(String[] args) {
			Command c = new ContactAll();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				contactAll = !contactAll;
				setResult("contact all is now " + contactAll);
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}


}