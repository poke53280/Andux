package cmd;

public final class Request {

	private static final int FREE_POOL_SIZE = 40;


	private static final Request[] freeStack = new Request[FREE_POOL_SIZE];
    private static int countFree;
	private static int numberOfConstructed;
	public static final int FILE_SHELL = -1;


    public static synchronized Request getInstance(String[] args, int sID, Environment e) {
        Request r;
        if (countFree == 0) {
			numberOfConstructed++;
            r = new Request(numberOfConstructed, sID);
			//System.out.println("Constructed new request, # " + numberOfConstructed);
        } else {
            r = freeStack[--countFree];
        }
		r.setArgs(args, sID);
		r.setEnv(e);
		return r;
	}

    public static synchronized void freeInstance(Request r) {
        if (countFree < FREE_POOL_SIZE) {
			r.clear();
            freeStack[countFree++] = r;
        }
    }

	private String args[] = null;
	private String response = null;
	private long launchTime = 0;
	private long receiveTime = 0;
	private boolean available = false;
	private int shellID = 0;

	private final int id;

	private Environment env = null;


	private Request(int id, int sID) {
		this.id = id;
		this.shellID = sID;
		clear();
	}

	public int getShellID() {
		return shellID;
	}


	private void clear() {
		args = null;
		response = null;
		available = false;
		launchTime = 0;
		receiveTime = 0;
		shellID = 0;
		env = null;
	}

	private void setArgs(String[] args, int sID) {
		this.args = args;
		this.shellID = sID;
		launchTime = System.currentTimeMillis();
	}

	private void setEnv(Environment e) {
		if (e == null) {
			throw new IllegalArgumentException("Environment is null");
		}
		this.env = e;
	}

	public Environment getEnv() {
		return env;
	}

	public String[] getRequest() {
		return args;
	}

	public int getID() {
		return id;
	}


	public String getResponse() {

//	    return "[id=" + id + ",t=" + (receiveTime - launchTime)
//				+ "ms:" + getArgString() + "]\n" + response;

//		return "(" + (receiveTime - launchTime) + "ms): " + response;

		return response + "\n(" + (receiveTime - launchTime) + "ms)";


	}

	public void setResponse(String s) {
	  receiveTime = System.currentTimeMillis();
	  response = s;
	}

	public String getArgString() {
		if (args == null || args.length == 0) {
			return("No args found");
		} else {
			StringBuffer b = new StringBuffer(100);
			for(int i = 0; i < args.length; i++) {
					b.append(args[i]);
					b.append(' ');
			}
			return b.toString();
		}

	}


}