
package io;

import root.WorldSize;
import net.NetMessage;
import root.Factory;
import root.Command;
import area.Area;
import area.AreaProvider;
import area.KeyArea;
import java.text.DecimalFormat;
import dispatch.Port;
import net.PacketTransmitter;

import root.SpaceMax;

public class Proxy extends KeyArea {

  private Wire wire = null;
  private int ID;
  private AreaProvider ap;

  private int initSends = 3;	//Send this many to exiled remote, not more.

  private Area lastArea = new Area();		//Last sent local area

  private int each = 10;
  private int count = 0;

  private long createTick;

  private ProxyState state = ProxyState.EXT;		//uncertain for first run


  //10: Thrashing when no agent sends on warps

  private static final int DECAY = 50;

  private int decay = DECAY;
  private DecimalFormat format = new DecimalFormat();

  public Proxy (int ID, Wire w, AreaProvider ap, long sid, long createTick) {
		super(sid);
		setExiledPos();

		this.ID = ID;
		this.wire = w;
		this.ap = ap;
		this.createTick = createTick;

		format.applyPattern("#####0.00");

  }

	public void setProxyState(ProxyState s) {
		state = s;
	}

	public ProxyState proxyState() {
		return state;
	}


	public void notifyActivity() {
		decay = DECAY;
	}

	private void decay() {
		decay--;
	}

	public int getDecay() {
		return decay;
	}

	private boolean isDead() {
		return decay <= 0;
	}



	//Fill account of free sends, bypassing proximity check
	public void loadInitSends(int s, boolean verbose) {
		if (s < 1) throw new IllegalArgumentException("s=" + s);
		initSends = s;
		if (verbose) System.out.println("Proxy: Set initSends to " + s);
	}


	//This is a connected warp proxy or Im low on nodes, sending
	//a little to stay current
	public void pingSend(float ran) {
		notifyActivity();	//don't delete these links.
		if (0.3f > ran) {
			areaOutput();
		}
	}

	public boolean tick(float ran) {

		if (isExiled() ) {
			if (initSends > 0) {
				//New case
				if (0.1f > ran) {
					initSends--;
					areaOutput();
				} else {
					//Save try for later
				}
				notifyActivity();
			} else {
				decay();
				//Dead case
			}
		} else {
			//Connected case

			float f = horizons();
			if (f > SpaceMax.FAR) {

				//Far-away. If there are 'initSends' left, try one:
				if (initSends > 0) {
					if (0.1f > ran) {
						//System.out.println("Proxy.tick.faraway, burning extra send# " + initSends);
						initSends--;
						areaOutput();
					} else {
						//Save try for later
					}
					notifyActivity();
				} else {
					//far-away, no spark to try recheck.
					decay();		//This will also include remotes who consider *this* as a warp
											//location
				}
			} else {
				float runPri = priority();
				if (runPri > ran) {
					perhapsAreaOutput();
					notifyActivity();
				}
			}
		}

		wire.send();
		return isDead();
	}


	public float priority() {
		float f = horizons();
		if (f > SpaceMax.FAR)   return 0f;
		if (f <= SpaceMax.NEAR) return 1f; //'overlap' if less than zero

		float a = 1f/(SpaceMax.FAR - SpaceMax.NEAR);

		return 1f - a*(f-SpaceMax.NEAR);	//linear fall-off from 1 to 0

	}


	//Pre: proxy has just received an area update from
	//remote part. If self would never send, send.

	//control a random call externally, possible that this
	//may escalate.
	public void considerReply() {
		if (isExiled() ) {
			throw new IllegalStateException("Cannot " +
			"be in exiled state when update just was received");
		}
		notifyActivity();

		if (horizons() > SpaceMax.FAR) {
			//System.out.println("Responding to far node (p <= 0.1)");
			//areaOutput.output();	//always. send next tick
			areaOutput();
		}

	}

    public void send(NetMessage m) {
		if (m == null) throw new IllegalStateException("no message");
		wire.push(m);
		notifyActivity();
    }


	//create a chat message and send the message home
	public void sendChat(String s) {

		if (s == null || s.length() < 1) {
			System.out.println("Proxy.sendChat: String empty, not sending");
			return;
		}

		NetMessage m = NetMessage.getInstance();
		m.init(Port.CHAT);
		m.append(s);

		if (m.getSize() + 4 > PacketTransmitter.MAX_RECEIVE_SIZE) {
			System.out.println("Proxy.sendChat: Message too big.Skipping");
			NetMessage.freeInstance(m);
			m = null;
		} else {
			send(m);
		}
	}


	public int getID() {
		return ID;
	}

	public long tickCreated() {
		return createTick;
	}


	private void perhapsAreaOutput() {
		if (!sendNoMatter() && !isOutside() ) return;
		areaOutput();
	}

	private void areaOutput() {
		Area a = ap.getArea();

		int x = a.getX();
		int y = a.getY();

		NetMessage m = NetMessage.getInstance();
		m.init(Port.AREA);

		m.append(x);
		m.append(y);

		send(m);
		lastArea.setTo(a);

	}


	public String getOverview() {
		String p = null;

		if (isExiled() ) {
			if (initSends > 0) {
				p = "ini";
			} else {
				p = "non";
			}
		} else {
			float f  = horizons();
			float pr = priority();
			p = format.format(new Double(f) ) + "|" + format.format(new Double(pr) );
		}

		return getKeyString() + "|" + p + "|" + decay + "|" +wire.shortState();
	}

	public long outputCount() {
		return wire.getCount();
	}


	public float horizons() {
		if (isExiled() )
			throw new IllegalStateException("not defined for exiled nodes");

		return horizons(ap.getArea() );

	}

	private boolean sendNoMatter() {	return ++count%each == 0; }

	private boolean isOutside() {
		int THRESHOLD = WorldSize.FOCUS_THRESHOLD;

		Area a = ap.getArea();

		int x = a.getX();
		int y = a.getY();


		int lx = lastArea.getX();
		int ly = lastArea.getY();


		int dx = Math.abs(x-lx);
		int dy = Math.abs(y-ly);

		return dx > THRESHOLD || dy > THRESHOLD;
	}


	public String getStatus() {
		Area a = ap.getArea();
		String phase = format.format(new Double(centerAngle(a) ) );

		StringBuffer b = new StringBuffer(200);
		b.append("\nid:                   " + getID() );
		b.append("\nnode-connection:      " + getKeyString() );
		b.append("\nwire:                 " + wire.state() );
		b.append("\nhorizons              " + horizons() );
		b.append("\nrun priority:         " + priority() );
		b.append("\nphase(-pi,pi)         " + phase);
		b.append("\nlocal intersection    " + intersects(a) );
		b.append("\nloc ap:   " + a.state() );
		b.append("\nremote area: " + super.status() );

		return b.toString();
	}

	public void registerCommands(Factory f, String prefix) {
		if (f == null) return;
		f.add(prefix + "status", new Status() );
	}

	public void deregisterCommands(Factory f, String prefix) {
		if (f == null) return;
		f.remove(prefix + "status");
	}

	class Status extends Command {

		public Status() { setUsage("status"); }
		public Command create(String[] args) {
			Command c = new Status();
			c.setArgs(args);
			return c;
		}
		public void init() { super.init(); }
		public void execute() {	setResult(getStatus() ); }
		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}

}