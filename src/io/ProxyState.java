
package io;

import java.awt.Color;

public class ProxyState {

	public static final ProxyState WRP = new ProxyState(Color.yellow.getRGB());
	public static final ProxyState EXT = new ProxyState(Color.lightGray.getRGB());
	public static final ProxyState EXI = new ProxyState(Color.pink.getRGB());
	public static final ProxyState CLS = new ProxyState(Color.cyan.getRGB());

	private int c;
	private ProxyState(int c) {
		this.c = c;
	}
	public int color() { return c; }

}