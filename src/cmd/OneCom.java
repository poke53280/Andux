
package cmd;

import java.io.*;
import java.net.*;

public class OneCom {

    private String host;
    private int port;
	private String name;

 	Socket s = null;
    PrintWriter out = null;
    BufferedReader in = null;

    public OneCom(String name, String host, int port)
    				throws UnknownHostException, IOException {

		this.host = host;
		this.port = port;
		this.name = name;
		connect();

	}

	public StringBuffer status() {
		StringBuffer b = new StringBuffer(100);
		b.append(name + "                   [" + host + ":" + port + "]");
		return b;
	}


	private void connect() throws UnknownHostException, IOException {

        s = new Socket(host, port);
        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));

	}



  public String exec(String o) throws IOException {
		out.println(o);
		String result = in.readLine();
		//System.out.println("OneCom.exec:localaddress is " + s.getLocalAddress().toString());
		return result;
	}

	public void close() throws IOException {
        out.close();
        in.close();
        s.close();
	 }

}

