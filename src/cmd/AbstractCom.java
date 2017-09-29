
package cmd;

import root.SynchedQueue;
import root.ParseUtil;
import java.util.Observable;



public abstract class AbstractCom extends Thread {

	protected SynchedQueue outBound;
	protected SynchedQueue incoming = new SynchedQueue();

	protected int id;
	protected long lastAction;

	protected long wait = 1000L;

	protected String remoteHost = "undefined";
	protected String welcome = null;

	protected DataHolder dataHolder = new DataHolder();

	protected boolean isRunning = true;

	protected Environment e;

	public int getID() {
		return id;
	}

	public Environment getEnv() {
		return e;
	}

	public AbstractCom(int id, String welcome, SynchedQueue q) {
		super("AbstractCom " + welcome);
		this.id = id;
		this.welcome = welcome;
		this.outBound = q;
		this.e = new Environment();
	}

	public void setPeriod(long wait) {
		this.wait = wait;

	}

	protected void pushCommand(String inputLine) {

		String[] args = ParseUtil.getArgsAsArray(inputLine);

		//into request, also set shell environent values:

		//This created request must later be freed some place else

		Request r = Request.getInstance(args, id, e);
		outBound.push(r);
	}

	protected String pullAnswer() {
		Request r = (Request) incoming.pop();
		String s = r.getResponse();
		Request.freeInstance(r);

		return s;
	}

	protected String pollAnswer() {
		//Returns null if no answer ready
		if (incoming.isEmpty() ) return null;

		//Nothing else removes from queue, so
		//there must be an answer for us:
		return pullAnswer();


	}


	protected String execute(String inputLine) {
		pushCommand(inputLine);
		String s = pullAnswer(); //Blocks
		return s;
/*
		String[] args = ParseUtil.getArgsAsArray(inputLine);

		//into request, also set shell environent values:
		Request r = Request.getInstance(args, id, e);

		outBound.push(r);


		r = (Request) incoming.pop();		//Blocks

		String s = r.getResponse();
		Request.freeInstance(r);
		r = null;

		return s;
*/

	}

	public Request getRequest() {
		if(!outBound.isEmpty() ) {
			return (Request) outBound.pop();
		} else {
			return null;
		}
	}

	public void setResponse(Request r) {
		incoming.push(r);
	}

	public boolean isRunning() {
		return isRunning;
	}

	public abstract void close();
	public abstract String terminalStatus();


	public DataHolder getModel() {
		return dataHolder;
	}

	public void setCommand(String cmd) {
		System.out.println("AbstractCom.setCommand");
	}

	public String[] getResult() {
		System.out.println("AbstractCom.getResult");
		return null;
	}

	public void stopExecute() {
		System.out.println("AbstractCom.stopExecute");
	}


}