
package display;

import java.awt.TextArea;
import java.util.Observer;
import java.util.Observable;
import java.awt.Color;

public class TextOutput extends TextArea implements Observer {

	public TextOutput() {
		super();
		//setBackground(Color.black);
		setForeground(Color.red);
		setEditable(false);
	}

	public void update(Observable o, Object arg) {
		if (arg != null) {
			setText(arg.toString() );
		} else {
			System.out.println("TextOutput: arg is null");
		}
	}

}