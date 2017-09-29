package cmd;

import java.net.*;
import java.io.*;

public abstract class AbstractServer {

	protected int port;

	protected ServerSocket serverSocket = null;

	public AbstractServer(int p)  throws IOException {
            this.port = p;
            serverSocket = new ServerSocket(port);
			System.out.println("Listening on port " + port);
	}

	public void process() throws IOException {
		  boolean listening = true;

 			while (listening) {
			    	Service s = getService();
			    	s.start();
			}
        	serverSocket.close();
	}

	protected Service getService() throws IOException {
		return new Service(serverSocket.accept());
	}


	public class Service extends Thread {

	    private Socket socket = null;
		private int last = -1;

	    public Service(Socket socket) {
			super("Request");
			this.socket = socket;
			System.out.println("processing request...");
	    }

    public void run() {

		try {
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(
					    new InputStreamReader(
					    socket.getInputStream()));

		    String inputLine, outputLine;

			while ((inputLine = in.readLine() ) != null) {
				String s = runCommand(inputLine);
				out.println(s);
			}

		    out.close();
		    in.close();
		    socket.close();
			System.out.println("done");
		} catch (IOException e) {
		    //e.printStackTrace();
			System.out.println("Socket finished");
		}
    }

	protected String runCommand(String in) {
		return "N/A";
	}
}

}
