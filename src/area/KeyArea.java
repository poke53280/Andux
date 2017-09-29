
package area;

import net.IPString;

public class KeyArea extends Area {

	private final long key;

	public KeyArea(long key) {
		super();
		this.key = key;

	}

	public KeyArea(long key, Area a) {
		super(a);
		this.key = key;
	}


	public KeyArea(long key, int x, int y) {
		super(x,y);
		this.key = key;
	}

	public String status() {
		String s = IPString.getAddressString(key);
		return s + ":" + super.status();
	}

	public final long getKey() {
		return key;
	}

	public final String getKeyString() {
		return IPString.getAddressString(key);
	}

	public final Long getKeyObj() {
		return new Long(key);
	}

}