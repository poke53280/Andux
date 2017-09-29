
package cmd;

import root.SynchedQueue;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;


public class FileCom extends AbstractCom {

	RandomFile file = null;
	PrintWriter out = null;

	String name = null;

	public FileCom(int id, String welcome, SynchedQueue out) {
		super(id, welcome, out);
		remoteHost = "file";
	}


	public String terminalStatus() {
		return "File        " + name;
	}

	public void setIO(String name) throws FileNotFoundException, SecurityException {

		out = null;
		file = new RandomFile(name, true); 	//Read only
		//System.out.println("FileCom: File opened");
		this.name = name;
	}

	public void close() {
		if (file != null) file.close();
		file = null;
		name = null;
		isRunning = false;
	}

	public void run() {

		while(true) {

			String in = file.readLine();

			if (in == null ) {
				if(out != null) out.println("EOF");
				break;
			} else {
				if ( in.equals("") || in.startsWith("//") ) {
					if(out != null) out.println("(Comment/blank)");
				} else {
					String s = execute(in);	//blocks
					if (out != null) out.println("in=[" + in + "], result = " + s);
				}
			}

		}

		close();
	}
}