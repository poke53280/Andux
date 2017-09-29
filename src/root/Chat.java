
package root;


import net.NetConnector;
import net.Connector;
import net.MessageIO;
import net.IPString;

import area.AreaProvider;
import area.Area;
import area.KeyArea;
import area.KeyAreaSet;

import root.Node;
import root.ParseUtil;
import java.net.UnknownHostException;

public class Chat {


	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("Chat <locIP:locport> <remIP:remport>");
			System.exit(0);
		}

		String localSocket  = args[0];
		String remoteSocket = args[1];

		StillProvider sp = new StillProvider();

		Connector c = null;
		try {
			c = new NetConnector(localSocket);
		} catch (UnknownHostException e) {
			System.out.println("Couldn't parse local address");
		}


		Node a = null;

		try {
			a = new Node(sp,c);
		} catch (Exception e) {
			System.out.println("Couldn't connect to local socket: " + localSocket);
			System.exit(0);
		}


		try {
			a.contact(remoteSocket);
		} catch (UnknownHostException e) {
			System.out.println("Couldn't parse contact string. Continuing.");
		}


		Pacer pacer = new Pacer(50);
		int stateCount = 0;

		while(true) {
			pacer.launch();
			sp.update();			//Reposition

			a.run();
			stateCount++;
			stateCount %=100;
			if (stateCount == 0) {
				System.out.println("-----getNear returned KeyAreaSet:----");
				KeyAreaSet k = a.getNear();
				if (k == null) {
					System.out.println("NONE - ALONE");
				} else {
					System.out.println(k.show() );
				}
			}
		}
	}
}

class StillProvider implements AreaProvider {
		Area a = new Area(100,100);
		public Area getArea() {	return a; }
		public void update() { }
}
