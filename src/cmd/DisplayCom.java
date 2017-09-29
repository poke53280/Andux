
package cmd;
import root.SynchedQueue;

public class DisplayCom extends AbstractCom {

	public DisplayCom(int id, String welcome, SynchedQueue out) {
		super(id, welcome,out);
	}

	public void close() {
		System.out.println("DisplayCom.close()");
	}

	public String terminalStatus() {
		int env = e.getItemID();

		String s = "disp" + " 	" + id + "		 "
					+ "	 "
						+ (System.currentTimeMillis() - lastAction) + " " + "env=" + env;
		return s;


	}
	public void run() {
		//System.out.println("DisplayCom.run(). finished immediately");
	}

	public void add(String s) {
		pushCommand(s);
	}

	//Returns null if no answers ready,
	//else a string. Always returns
	public String poll() {
		return pollAnswer();
	}


}