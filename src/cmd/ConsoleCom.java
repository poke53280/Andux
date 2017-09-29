
package cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import root.SynchedQueue;

public class ConsoleCom extends AbstractCom {

	PrintWriter out = null;
	BufferedReader in = null;

	String consolePath = "#";

	public ConsoleCom(int id, String welcome, SynchedQueue out) {
		this(id,welcome,out,null);
	}

	public ConsoleCom(int id, String welcome, SynchedQueue out, String consolePath) {
		super(id, welcome, out);
		remoteHost = "console";
		if (consolePath != null) {
			this.consolePath = consolePath;
		}

	}

	public void close() {
		System.out.println("ConsoleCom.close()");
		out = null;
		in = null;
		System.out.println("ConsoleCom.close() finished");
	}

	public String terminalStatus() {
		int env = e.getItemID();

		String s = "root" + " 	" + id + "		 "
			+ remoteHost + "	 "
				+ (System.currentTimeMillis() - lastAction) + " " + "env=" + env;
		return s;

	}

	public void setIO() {

		out = new PrintWriter(
				System.out, true );

		in = new BufferedReader(
							new InputStreamReader(
						System.in ) );
	}


	public void run() {

		if (!isRunning || out == null) return;

		printPrompt(out);
		cmdLine();
		close();
		isRunning = false;


	}

	protected void cmdLine() {

		String inputLine = null;

	    while ( (inputLine = getLine(in) ) != null) {

			lastAction = System.currentTimeMillis();

			if (inputLine.equals("") ) {
	            //nop
			} else if(inputLine.equals("exit") ) {
				break;
			} else {
				String s = execute(inputLine);
				if (!s.equals("") ) {
					out.println(s);
				}
			}

			if (out != null) {
				printPrompt(out);
				out.flush();
			}
   		 }
	}


	protected String getLine(BufferedReader in) {

		String inputLine = null;
		boolean isOK = true;

		try {
			inputLine = in.readLine();
		} catch (IOException ie) {
			System.out.println("ConsoleCom.getLine. IOException " + ie.getMessage() );
			isOK = false;
		} catch (NullPointerException ne) {
			isOK = false;
		}

		if (isOK) {
			return inputLine;
		} else {
			System.out.println("ConsoleCom.getLine returning null");
			return null;
		}
	}

	protected void printPrompt(PrintWriter p) {
		p.print(consolePath);
		p.flush();
	}

}
