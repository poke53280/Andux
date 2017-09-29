
package userio;

import java.util.Stack;

import userio.CommandSource;


public class LineBuffer extends Stack implements CommandSource {

	public LineBuffer() {
		super();
	}

	public String poll() {
		if (isEmpty() ) {
			return null;
		} else {
			return (String) pop();
		}
	}

}