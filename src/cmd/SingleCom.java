package cmd;

import root.SynchedQueue;

public class SingleCom extends AbstractCom {

	private CommandSource b;

	private boolean goOn = true;

	private String cmd = null;


	public SingleCom(CommandSource b, int id, String welcome, SynchedQueue out) {
		super(id, welcome,out);
		this.b = b;
	}

	public String terminalStatus() {
			if (b == null) {
				return "SingleCom: NO SOURCE";
			} else if (cmd == null) {
				return "SingleCom: IDLE";
			} else {
				return "SingleCom: wait=" + wait + " ms: " + cmd;
			}
	}

	public void close() {
		System.out.println("SingleCom.close()");
		isRunning = false;
		goOn = false;
	}

	public void run () {

		while(goOn) {
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				//
			}

			if (b==null) continue;

			cmd = b.poll();
			if (cmd != null) {
				String out = execute(cmd);
				dataHolder.setValue(out);
			}
		}
	}

}