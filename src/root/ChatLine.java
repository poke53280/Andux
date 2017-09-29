
package root;


import net.IPString;

public class ChatLine {

	private String s = null;
	private long sid = -1L;

	private final long local;

	public ChatLine(long local) {
		this.local = local;
	}

	public void set(long sid, String s) {
		if (sid == -1L || s == null || s.length() < 1)
			throw new IllegalArgumentException("bad data");

		this.sid = sid;
		this.s = s;
	}

	public void set(String s) {
		//Using local
		set(local,s);
	}

	public boolean isLocal() { return sid == local; }


	public String show() {

		if (s == null) return "UNUSED";

		String d = null;
		if (sid != local) {
			d = IPString.getAddressString(sid);
		} else {
			d = "LOCAL         ";
		}

		return d + ":'" + s + "'";

	}


}