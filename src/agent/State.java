
package agent;
import java.awt.Color;
public class State {


	public static final State INIT  =    new State(Color.yellow.getRGB(),"INIT");
	public static final State NOGO  =    new State(Color.red.getRGB(),"NOGO");
	public static final State BACK0 =    new State(Color.orange.getRGB(),"BAD ");
	public static final State BACK1 =    new State(Color.green.getRGB(),"GOOD");
	public static final State THRU =     new State(Color.darkGray.getRGB(),"THRU");
	public static final State ULTIMATE = new State(Color.red.getRGB(),"MAX ");
	public static final State NOPLACE =  new State(Color.red.getRGB(),"NOPL");
	public static final State OVERFLOW = new State(Color.red.getRGB(),"OVFL");


	private int c;
	private String desc;
	private State(int c, String desc) {
		this.c = c;
		this.desc = desc;
	}

	public int color() { return c; }
	public String desc() { return desc; }


}