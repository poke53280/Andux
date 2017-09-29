
package io.handler;

public interface LocalNode {

	public void contact(RemoteNode.NodeMessage nm);
	public boolean fillMessage(RemoteNode.NodeMessage nm);


}