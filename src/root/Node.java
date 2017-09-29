
package root;

import io.ProxyManager;
import io.Proxy;

import io.NodeSender;
import area.AreaProvider;
import dispatch.Port;
import dispatch.InHandler;
import dispatch.Dispatcher;
import net.MessageSocket;
import net.MessageIO;
import net.IPString;
import net.Connector;

import agent.Agency;

import root.ParseUtil;
import statistics.Sampler;
import statistics.Buffer;
import area.KeyArea;
import area.KeyAreaSet;

import area.MasterAreaHandler;
import java.net.UnknownHostException;
import java.util.Vector;

public class Node implements Runnable {

	private ProxyManager proxies;
	private AreaProvider ap = null;
	private Agency agency = null;
	private Sampler sampler = null;

	private String name;
	private String hostname;

	private MasterAreaHandler mArea;

	private ChatCentral chat;

	private long tick = 0L;

	private int lowHundred = 1 + (int) (100.0*Math.random());

	public Node(AreaProvider ap,Connector c) throws Exception {
		this(ap,c.connect());
	}


	public Node(AreaProvider ap,MessageIO s) {
		this(ap,null,null,null,s);

	}


	public Node(AreaProvider ap, String prefix,
				String hostname, Factory f, MessageIO s) {

		this.hostname = hostname;

		name = IPString.getAddressString(s.getLocalSID() );


		s.registerCommands(prefix + hostname + "/n/", f);

		proxies = new ProxyManager(prefix + hostname + "/n/",f, ap,(MessageIO) s );

		agency = new Agency(proxies,ap);

		agency.registerCommands(prefix + hostname + "/a/",f);

		mArea = new MasterAreaHandler(proxies.getNodeList() );

		Dispatcher d = proxies.getDispatcher();
		InHandler[] h = d.getHandlers();
		h[Port.AGENT] = (InHandler) agency;
		h[Port.AREA] = (InHandler) mArea;

		sampler = new Sampler(proxies.getSampleProvider(), 400, "prox-io");

		chat = new ChatCentral(this);
		chat.registerCommands(f,prefix + hostname + "/chat/");

		h[Port.CHAT] = (InHandler) chat;


	}

	public KeyArea getKeyArea() {
		ProxyManager p = getProxyManager();
		KeyArea a = p.getKeyArea();
		return a;
	}

	public void contact(long sid) {
		proxies.contact(sid);
	}


	public void contact(String s) throws UnknownHostException {
		long remoteSID = -1L;
		try {
			remoteSID = IPString.getLong(s);
		} catch (IllegalArgumentException e) {
			System.out.println("Node:Couldn't parse contact address:" + s);
			throw new UnknownHostException("Bad contact string");
		}
		contact(remoteSID);		//never fails.
	}


	public ProxyManager getProxyManager() {
		return proxies;
	}

	public Agency getAgency() {
		return agency;
	}

	public ChatCentral getChat() {
		return chat;
	}

	public String name() {
		return name;
	}

	public String hostname() {
		return hostname;
	}

	public String desc() {
		return hostname + "[" + name + "]";
	}

	public Buffer getSizeBuffer() {
		return sampler.getBuffer();
	}

	public KeyAreaSet getNear() {
		return proxies.getNear();
	}

	public void run() {

		tick++;

		agency.clearStats();

		proxies.route();								//input
		if (sampler != null) sampler.sample();
		proxies.doIO(tick);								//state update and then output

		agency.tick(tick);								//agents


		if (Math.random() < 0.1) chat.autoSay();

	}

}