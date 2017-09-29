
package cm;

import root.Command;

import root.Node;
import area.KeyArea;
import area.KeyAreaSet;
import root.Factory;
import root.CycleVector;
import io.ProxyManager;
import io.ProxyStore;
import dispatch.Dispatcher;

import statistics.SampleProvider;
import root.ParseUtil;

public class ClusterMaster {

	CycleVector v;
	Density d;
	ClusterState cs;

	AgentTracer at;

	int evalMax;
	int counter = 0;

	long tick = 0L;

	public ClusterMaster(CycleVector v, int max, long tick) {
		this.tick = tick;
		this.v = v;
		this.d = new Density(v);
		this.evalMax = max;
		this.at = new AgentTracer(v);


		this.cs = new ClusterState(tick, d);
	}

	public void setTick(long tick) {
		this.tick = tick;
		cs.setTick(tick);
	}

	public AgentTracer getTracer() { return at; }


	public void registerCommands(Factory f, String prefix) {

		if (f == null) return;

		f.add(prefix + "tracelist", new Trace() );
		f.add(prefix + "eval", new RunEval() );
		f.add(prefix + "show", new LastEval() );
		f.add(prefix + "apptraffic", new AppTraffic() );
		d.registerCommands(f,prefix);
	}

	public boolean isEmpty() {
		return v == null || v.isEmpty();
	}

	public ClusterState getClusterState() {
		return cs;
	}

	public float sizeAtDensity(float _d) {
		return d.sizeAtDensity(_d);
	}

	public float sizeAtDensity(float _d, int extra) {
		return d.sizeAtDensity(_d,extra);
	}

	//All nodes in VM as area and unique key
	private KeyAreaSet getKeyAreaSet() {
		if (v == null || v.isEmpty() ) return null;

		KeyAreaSet s = new KeyAreaSet();
		for(int i = 0; i < v.size(); i++) {
			Node n = (Node) v.elementAt(i);
			KeyArea a = n.getKeyArea();
			s.add(a);
		}
		return s;
	}

	public boolean perhapsEval() {
		if (++counter%evalMax !=0) {
			return false;
		} else {
			evalAll();
			return true;
		}
	}


	private void evalOne(int index) {
		if (index >= v.size() || index < 0)
			throw new IllegalArgumentException("bad index");

		Node n = (Node) v.elementAt(index);
		Evaluation e = new Evaluation(getKeyAreaSet(),n);
		e.eval();

		cs.register(e);
	}

	private void evalAny() {
		if (v == null || v.isEmpty() ) return;

		int index = (int) (.5 + Math.random()*(v.size()-1));
		evalOne(index);
	}


	public void evalAll() {
		cs.clear();
		if (v == null || v.isEmpty() ) 	return;

		for(int i = 0; i < v.size(); i++) {
			evalOne(i);
		}
		cs.postCalc();
	}

	public void evalSome(int n) {
		if (n <= 0)
			throw new IllegalArgumentException("n <= 0");


		cs.clear();
		if (v == null || v.isEmpty() ) 	return;

		for(int i = 0; i < n; i++) {
			evalAny();
		}
		cs.postCalc();

	}


	public Evaluation getEvaluation() {	//...of current node

		Node n = (Node) v.current();
		if (n == null) return null;
		return new Evaluation(getKeyAreaSet(), n);
	}

	//Find share of app traffic in complete system
	//Later make poll-style
	private String appTraffic() {
		if (v == null || v.isEmpty() ) return "no nodes";

		StringBuffer b = new StringBuffer();

		int sysTotal = 0;
		int userTotal = 0;


		for(int i = 0; i < v.size(); i++) {
			Node n = (Node) v.elementAt(i);
			ProxyManager pm = n.getProxyManager();
			Dispatcher d = pm.getDispatcher();

			int sysCount = d.sysCount();
			int userCount = d.userCount();

			b.append('\n');
			b.append(n.desc());
			b.append(':');
			b.append(sysCount );
			b.append('-');
			b.append(userCount );

			sysTotal += sysCount;
			userTotal += userCount;
		}

		b.append("\nTotal sys  : " + sysTotal);
		b.append("\nTotal user : " + userTotal);

		return b.toString();

	}


	class LastEval extends Command {

		public LastEval() {
			setUsage("lasteval");
		}

		public Command create(String[] args) {
			Command c = new LastEval();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				setResult(cs.state() );
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}



	class Trace extends Command {

		public Trace() {
			setUsage("tracelist");
		}

		public Command create(String[] args) {
			Command c = new Trace();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				setResult(at.list() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

	}

	class AppTraffic extends Command {

		public AppTraffic() {
			setUsage("apptraffic");
		}

		public Command create(String[] args) {
			Command c = new AppTraffic();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				setResult(appTraffic() );
			}
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}

	}

	class RunEval extends Command {

		private int n = -1;

		public RunEval() {
			setUsage("eval [number,0=all]");
		}

		public Command create(String[] args) {
			Command c = new RunEval();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			if (isValid ) {
				if (n == 0) {
					evalAll();
					setResult("all nodes evaluated");
				} else {
					evalSome(n);
					setResult("evaluated " + n + " non-unique node(s)");
				}
			} else {
				setResult(showUsage() );
			}
		}

		public void setArgs(String[] args) {
			init();
			if (args.length != PAR1 +1) {
				isValid = false;

			} else {
				n = ParseUtil.parseInt(args[PAR1]);
				if (n < 0) {
					isValid = false;
				} else {
					isValid = true;
				}
			}
		}

	}


}