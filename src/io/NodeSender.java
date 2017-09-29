
package io;

public interface NodeSender {
	Proxy getProxy(long sid);
	Proxy contact(long sid);
}