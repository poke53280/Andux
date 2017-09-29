
package root;


import net.Connector;
import net.MessageIO;
import java.awt.Point;
import io.ProxyManager;
import area.MovingProvider;
import area.AreaProvider;
import agent.Agency;

public class Configurator {

	Connector c;
	Factory f;

	int agentLaunch = WorldSize.AGENCY_LAUNCH_FREQ;
	int loseRate = WorldSize.DROP_PACKETS;
	float maxSpeed = WorldSize.MAX_SPEED;

	public Configurator(Connector c, Factory f) {
		this.c = c;
		this.f = f;
	}

	public void setSpeed(float s) {
		if (s < 0f) throw new IllegalArgumentException("bad speed input");
		maxSpeed = s;
	}


	public void setAgent(int a) {
		if (a < 0) throw new IllegalArgumentException("a=" + a);
		agentLaunch = a;
	}



	public void registerCommands(String prefix) {
		if (f != null) {
			f.add(prefix + "list", new Status() );
			f.add(prefix + "agentrun", new AutoAgent() );
			f.add(prefix + "speed", new Speeder() );
			f.add(prefix + "lose", new Lose() );
		}
	}

	public MovingNode createNode(String name) {
		MessageIO s = getSocket();

		MovingProvider mp =
			new MovingProvider(maxSpeed);


		mp.registerCommands("/nodes/" + name + "/",f);


		MovingNode a = new MovingNode(mp,"/nodes/",name,f,s);

		ProxyManager pm = a.getProxyManager();

		pm.setLosePromille(loseRate);

		Agency ag = a.getAgency();

		ag.setLaunchFreq(agentLaunch);

		pm.contactPrevious();

		return a;

	}

	private MessageIO getSocket() {
		MessageIO s = null;
		try {
			s = c.scan();
		} catch (Exception e) {
			System.out.println("Couldn't create node:" + e.getMessage() );
			System.exit(1);
		}
		return s;

	}

	private String status() {
		StringBuffer b = new StringBuffer(200);

		if (agentLaunch > 0) {
			b.append("\nauto agent launch= " + agentLaunch);
		} else {
			b.append("\nauto agent: disabled");
		}

		b.append("\nguider speed: " + maxSpeed);

		if(loseRate == 0) {
			b.append("\npacket loss: none");
		} else {
			b.append("\npacket loss: " + loseRate + " of 1000");
		}

		return b.toString();
	}

	class Status extends Command {

		public Status() {
			setUsage("status");
		}

		public Command create(String[] args) {
			Command c = new Status();
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
			setResult(status() );
		}

	}

	class AutoAgent extends Command {

		private int freq;

		public AutoAgent() {
			setUsage("agentrun freq (0=disabled)");
		}

		public Command create(String[] args) {
			Command c = new AutoAgent();
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
				agentLaunch = freq;
				if (agentLaunch == 0) {
					setResult("agency: agent auto launch disabled");
				} else {
					setResult("agency: launching every "
									+ agentLaunch + " run(s)");
				}
			} else {
				setResult(showUsage() );
			}
		}
	}

	class Speeder extends Command {

		float speed = 0f;

		public Speeder() {
			setUsage("speed max-speed");
		}

		public Command create(String[] args) {
			Command c = new Speeder();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			speed = 0f;
		}

		public void setArgs(String[] args) {

			init();

			if (args.length != PAR1 +1) {
				isValid = false;
			} else {
				int i = ParseUtil.parseInt(args[PAR1]);
				if (i < 0) {
					isValid = false;
				} else {
					isValid = true;
					speed = (float) i;

				}
			}
		}

		public void execute() {
			if (isValid) {
				setSpeed(speed);
				setResult("Node guider speed set to : " + speed);
			} else {
				setResult("Invalid input, speed still is " + maxSpeed);
			}
		}
	}

	class Lose extends Command {
		int lose = -1;
		public Lose() {	setUsage("lose <1/1000s>"); }
		public Command create(String[] args) {
			Command c = new Lose();
			c.setArgs(args);
			return c;
		}
		public void init() {
			super.init();
			lose = -1;
		}
		public void setArgs(String[] args) {
			init();
			if (args.length != PAR1 +1) {
				isValid = false;
			} else {
				lose = ParseUtil.parseInt(args[PAR1]);
				if (lose < 0 || lose > 1000) {
					isValid = false;
				} else {
					isValid = true;
				}
			}
		}

		public void execute() {
			if (isValid) {
				loseRate = lose;
				setResult("Lose OUT set to " + loseRate + " pr.1000");
			} else {
				setResult("Losing : " + loseRate + " pr.1000");
			}
		}
	}
}
