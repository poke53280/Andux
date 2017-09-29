
package root;

import java.util.StringTokenizer;
import java.util.Stack;


public class ParseUtil {

	public static final int NOT_FOUND = -1;

	public static int parseInt(String s) {
			int i = NOT_FOUND;
			try {
				i = Integer.parseInt(s);
			} catch (NumberFormatException n) {
				//
			}
			return i;
	}

	public static Integer parseInteger(String s) {
		int temp;
		try {
			temp = Integer.parseInt(s);
		} catch (NumberFormatException n) {
			return null;
		}
		return new Integer(temp);
	}

	public static String[] getArgsAsArray(String s) {
			return getArgsAsArray(s," ");
		}


	public static String[] getArgsAsArray(String s, String del) {
			StringTokenizer t = new StringTokenizer(s, del);
			if (!t.hasMoreTokens() ) {
				return null;
			}
			String[] args = new String[t.countTokens()];
			int index = 0;
			while (t.hasMoreTokens() ) {
				args[index] = t.nextToken();
				//System.out.println("Arg " + index + " is: " + args[index]);
				index++;
			}

			return args;




	}


}