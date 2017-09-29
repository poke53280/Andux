
package net;

import root.Factory;
import statistics.SampleProvider;

public abstract class Connector {



	public abstract MessageIO scan() throws Exception;

	public abstract MessageIO connect() throws Exception;

	public abstract void registerCommands(Factory f, String prefix);

	public void setTick(long c) {
		//
	}

	public SampleProvider getHub() {
		return null;
	}

	public SampleProvider getTickTraffic() {
		return null;
	}


}