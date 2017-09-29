
package agent;



public class Type {

	public static final int PROXAGENT = 0;
	public static final int WARPAGENT = 1;
	public static final int MAX = 2;

	public static String desc(int t) {

		if (t == PROXAGENT) return  "prox ";
		if (t == WARPAGENT) return  "warp ";

		throw new IllegalArgumentException("Unknown agent type: " + t);
	}

	public static String abbrev(int t) {

		if (t == PROXAGENT) return  "p";
		if (t == WARPAGENT) return  "w";

		throw new IllegalArgumentException("Unknown agent type: " + t);
	}




	public static boolean isValid(int type) {
		return type >= 0 && type < MAX;
	}

	public static int anyType() {

		double d = 1000 * Math.random();

		int type = (int) d%MAX;
		return type;
	}

	public static void main(String[] s) {

		System.out.println("t = " + anyType() );
		System.out.println("t = " + anyType() );
		System.out.println("t = " + anyType() );
		System.out.println("t = " + anyType() );
		System.out.println("t = " + anyType() );

	}

}