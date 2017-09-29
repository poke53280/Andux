package dispatch;

import net.NetMessage;

public interface InHandler {

	public void input(NetMessage m);
	public String desc();

	public boolean isSystem();

}