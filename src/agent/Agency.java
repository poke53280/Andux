
package agent;

import dispatch.InHandler;

import io.ProxyStore;
import io.Proxy;
import io.ProxyManager;
import io.NodeSender;
import net.NetMessage;
import net.PacketTransmitter;

import root.Command;
import root.Factory;
import root.ParseUtil;

import root.WorldSize;
import area.AreaProvider;

/*
	When the Agent arrives at a remote node, it is given to the local Agency.
	The Agency will not do anything to our Agent, except let it run.

	When the Agent returns to its home, it is also handed to the local Agency.
	The Agency will immediately recognize this Agent as one of its own employees.

	The Agent is handed to the Evaluator. Also Agents that could never enter
	the network are handed to the Evaluator. ('no-go' agents.)

*/

public class Agency implements InHandler {

	private int each = 40;
	private int count;

	private long tick = 0L;

	private AgentFactory f = null;
	private ProxyStore s = null;
	private ProxyManager pm = null;

	private int agentsProcessed = 0;

	private GuestBook guestBook;

	public Agency(ProxyManager pm,AreaProvider ap) {
		this.pm = pm;

		long local = pm.getSid();


		this.f = new AgentFactory(local,ap, pm);
		this.s = pm.getNodeList();
		count = 1 + (int) (1000*Math.random());

		this.guestBook = new GuestBook(local);

	}

	//Agent messaging is part of the system.
	//(not user application)
	public boolean isSystem() { return true; }

	/*last used local id */
	public int lastID() {
		return f.lastID();
	}


	public String desc() { return "agency"; }

	public void tick(long tick) {

		this.tick = tick;

		count++;

		if (each == 0) return;	//No auto-run

		if(count%each == 0) {
			f.setVerbose(false);
			initAgent();
		}

	}

	public void setLaunchFreq(int f) {
		if (f < 0) return;
		each = f;
	}

	public void input(NetMessage m) {

		long sid = m.getSocket();
		s.notifyActivity(sid);

		Agent a = f.create(m);
		process(a);

	}

	private void initAgent() {
		Agent a = f.create();
		if (a == null) {
			//Failed to create agent (normal)
		} else {
			process(a);
		}
	}

	private void initAgent(int type) {
		Agent a = f.create(type);
		if (a == null) {
			System.out.println("Failed to create agent (normal)");
	 	} else {
			process(a);
		}
	}

	public void clearStats() {
		agentsProcessed = 0;
	}

	public int processed() {
		return agentsProcessed;
	}

	//for all agents; new, returned, visiting
	private void process(Agent a) {
		if (a == null)
			throw new IllegalArgumentException("got no agent");

		agentsProcessed++;


		if (a.verbose() ) {
			System.out.println("Agency:pre-tick,L:"
					+ f.location() +":\n" + a.status() );
		}

		boolean overflow = false;		//message too big to be sent
		NetMessage m = a.tick();

		if (m != null) {
			if (m.getSize() + 4 > PacketTransmitter.MAX_RECEIVE_SIZE) {
				overflow = true;
				System.out.println("Agency: Agent too big. hops()=" + a.hops() + ", size=" + m.getSize() );
				NetMessage.freeInstance(m);
				m = null;
			} else {
				Proxy p = pm.getProxy(m.getSocket() );
				if (p == null) {
					//checking that if new, it has to be home.
					long home = a.getHomeKey();
					if (m.getSocket() != home ) throw new IllegalStateException("Illegal access");
					p = pm.contact(home );
					if (p == null) throw new IllegalStateException("contacting self?");
				}
				p.send(m);
			}
		}

		State state = null;

		if (overflow) {
			state = State.OVERFLOW;
		} else if (!a.isHome()) {
			if (m == null) {

				if (a.hops() +1 >= Agent.PATH_MAX) {
					//Should never be bigger, just equal at max
					state = State.ULTIMATE;
				} else {
					state = State.NOPLACE;
				}
			} else {
				state = State.THRU;
			}
		} else {
			if (m == null) {
				if (a.hops() == 0) {
					state = State.NOGO;
				} else {
					int rate = pm.considerContact(a.report(), a.verbose() );
					if (rate > 0) {
						state = State.BACK1;
					} else {
						state = State.BACK0;
					}
				}

			} else {
				if (a.hops() == 0) {
					state = State.INIT;
				}
			}
		}

		if (state == null) throw new IllegalStateException("Agent state undefined");

		if (a.verbose() ) System.out.println("post run state : " + state.desc() );

		Stay stay = guestBook.get();
		stay.register(a,state);


	}

	public GuestBook getGB() {
		return guestBook;
	}

	public void registerCommands(String prefix, Factory f) {
		if (f == null) return;
		f.add(prefix + "send", new SendAgent() );
		f.add(prefix + "auto", new Auto() );
		f.add(prefix + "guests", new Guests() );
	}


	class Guests extends Command {

		public Guests() {
			setUsage("guests");
		}

		public Command create(String[] args) {
			Command c = new Guests();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			setResult(guestBook.list() );
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}

	class SendAgent extends Command {

		private int type = -1;

		public SendAgent() {
			setUsage("send <type>");
		}

		public Command create(String[] args) {
			Command c = new SendAgent();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
			type = -1;
		}

		public void execute() {
			if (isValid) {
				f.setVerbose(true);
				initAgent(type);
				setResult("sent agent of type=" + type);
				f.setVerbose(false);
			} else {
				setResult("send <agent type>");
			}
		}

		public void setArgs(String[] args) {
			init();
			if (args.length == PAR1 + 1 ) {
				type = ParseUtil.parseInt(args[PAR1]);
				if (Type.isValid(type) ) {
					isValid = true;
				} else {
					isValid = false;
				}
			} else {
				isValid = false;
			}
		}
	}

	class Auto extends Command {

		private int freq;

		public Auto() {
			setUsage("auto freq");
		}

		public Command create(String[] args) {
			Command c = new Auto();
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
				if (each == 0) {
					setResult("agency: agent auto launch disabled");
				} else {
					setResult("agency: launching every " + freq + " run(s)");
				}
			} else {
				setResult(showUsage() );
			}
		}
	}

}