package cmd;

import java.io.IOException;
import java.net.Socket;

import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.InputStreamReader;

import root.ParseUtil;

import root.SynchedQueue;



public class ShellCom extends ConsoleCom {

	protected Socket s = null;

	private String prompt = null;
	private String user = null;

	private long wait = 100L;

	private boolean goOn = true;


	private String correctUsername = null;
	private String correctPassword = null;

	public ShellCom(int id, String welcome, SynchedQueue out, String username, String password) {

		super(id, welcome, out);

		isRunning = true;
		remoteHost = "unknown";

		this.correctUsername = username;
		this.correctPassword = password;

	}

	public void setIO(Socket s) throws IOException {

		out = new PrintWriter(
				s.getOutputStream(), true );

		in = new BufferedReader(
				new InputStreamReader(
						s.getInputStream() ) );

		this.s = s;

	}



	public void run() {

		boolean ok = authenticate();

		if (!ok) {
			close();
			isRunning = false;
			return;
		}

		printPrompt(out);

		cmdLine();

		close();
		isRunning = false;

	}

	private boolean authenticate() {

		String inputLine;

		out.println(welcome);
		String password = null;

		out.print("login: ");
		out.flush();
		user = getLine(in);

		if (user == null) {
			System.out.println("ShellCom.runShell: got null user");
		}

		out.print("Password: ");
		out.flush();
		password = getLine(in);

		if (password == null) {
			System.out.println("ShellCom.runShell: got null password");
		}

		if (!isAuthenticated(user, password) ) {
			out.println("FAILED");
			out.flush();
			return false;
		} else {
			lastAction = System.currentTimeMillis();
			prompt = "[" + user + "@host]$ ";
			out.println("OK");
			out.flush();
			return true;
		}

	}

	public void close() {
		try {
			if (out != null) {
				out.close();
			}

			if (in != null) {
				in.close();
			}

			if (s != null) {
				s.close();	//Fix for real
			}
			s = null;
		 } catch (IOException ie) {
			//
		 }
	}

	protected boolean isAuthenticated(String user, String password) {

		if (user == null || user.equals("") ||
			password == null || password.equals("") ) {
			return false;
		}

		if (user.equals(correctUsername) && password.equals(correctPassword) ) {
			return true;
		} else {
			return false;
		}

	}

	protected void printPrompt(PrintWriter p) {
		p.print(prompt);
		p.flush();
	}

	public String terminalStatus() {
		String s = user + " 	" + id + "		 "
			+ remoteHost + "	 "
				+ (System.currentTimeMillis() - lastAction) + " ";
		return s;

	}

}