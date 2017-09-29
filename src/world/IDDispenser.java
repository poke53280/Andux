package world;

import root.Factory;
import root.Command;

public class IDDispenser {

	protected int min; //Inclusive
	protected int max; //Exclusive

	protected int last; //highest number in use

	public IDDispenser(int min, int max) {
		this.min = min;
		this.max = max;
		this.last = this.min -1;
	}

	public void registerCommands(Factory f, String prefix) {
		f.add(prefix + "get", new Get() );
	}


	public String status() {
		StringBuffer b = new StringBuffer(100);
		b.append("range=(" + min + "," + max + "), used="
									+ (100* (last-min+1)/(max-min) ) + "%");
		return b.toString();
	}


	public synchronized int get() {
		last++;
		if (last >= max) {
			throw new IllegalStateException("Out of IDs");
		}
		return last;
	}


class Get extends Command {

		public Get() {
			setUsage("get");
		}

		public Command create(String[] args) {
			Command c = new Get();
			c.setArgs(args);
			return c;
		}

		public void init() {
			super.init();
		}

		public void execute() {
			setResult("" + get() );
		}

		public void setArgs(String[] args) {
			init();
			isValid = true;
		}
	}

}