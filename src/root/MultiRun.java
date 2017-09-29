
package root;

import cmd.Telnetd;
import net.VMConnector;
import net.Connector;
import cm.ClusterMaster;
import cm.ClusterState;
import cm.ConsistentTimer;
import net.IPString;
import net.MessageSocket;
import statistics.SampleProvider;

public class MultiRun {


	ClusterConfig cc = new ClusterConfig();

	public static void main (String[] args) {

		MultiRun m = new MultiRun();
		m.experiment();
	}


	public MultiRun() {
		//
	}


	public void experiment() {

		System.out.println("#MultiRun experiment");
		System.out.println(cc.state() );
		cc.n = getRandom(20,30);
		run();

	}


	public void run() {

			DuxPark p = setup();
			p.preRun();
			int count = 0;
			System.out.println("#tick------n-------output-------%acc");

			ClusterMaster cm = p.getClusterMaster();
			ClusterState cs = cm.getClusterState();
			SampleProvider consistentPercent = cs.completePercent();

			while (true) {

				int delay = getRandom(120,290);

				while (++count%delay != 0) {
					p.tick();
				}



				cm.evalAll();


				float tickOutput = p.outputTick();
				//float accNodes = cs.consistentPercent;	//nodes in consistent env, %
				float accNodes = consistentPercent.sampleValue();


				//float perfAve = cs.perfAve;

				//float totalAve = cs.totalAve;
				//float totalAve = totalAve.sampleValue();

				//remember 10*nodes
				System.out.println(p.getCounter() + "       "
						+ p.size() + "           " + tickOutput + "   #acc%= " + accNodes);



				if (accNodes > 99.999f) {
					//System.out.println("SYSTEM COMPLETE");
					String s = "" + System.currentTimeMillis();
					p.addSeveral(s,10);

				}
			}

			//p.closeShell();

	}

	private DuxPark setup() {
		Connector vm = new VMConnector(IPString.getSID("127.0.0.1",
								WorldSize.NETWORK_PORT) );

                               
		DuxPark p = new DuxPark(vm,cc);

		p.addSeveral("c",cc.n);

		return p;
	}

	private static int getRandom(int min, int max) {	//inclusive min, max

		int w = max - min;
		if (w <=0) throw new IllegalArgumentException("max-min<=0");

		double d = w*Math.random();

		return min + (int) (d + .5);

	}




}