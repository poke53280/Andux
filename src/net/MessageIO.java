
package net;

import root.Factory;

public interface MessageIO {

	public void registerCommands(String prefix, Factory f);
	public void send(NetMessage m);
	public NetMessage poll();
	public long getLocalSID();
	public void end();
	public void close();
	public long getPrevious();

}