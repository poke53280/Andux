
package root;

import area.MovingProvider;
import net.MessageIO;
import area.AreaProvider;

public class MovingNode extends Node {

	private MovingProvider mp;

	public MovingNode(MovingProvider mp, String prefix, String hostname, Factory f, MessageIO s) {

		super( (AreaProvider) mp,prefix,hostname,f,s);
		this.mp = mp;
	}

	public void run() {
		mp.tick();
		super.run();

	}
	public void shuffle() {
		mp.shuffle();
	}

	public MovingProvider getMovingProvider() {
		return mp;
	}


}