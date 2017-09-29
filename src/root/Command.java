
package root;


import cmd.Environment;

public abstract class Command {
		public static final int CMD = 0;
		public static final int PAR1 = 1;
		public static final int PAR2 = 2;
		public static final int PAR3 = 3;
		public static final int PAR4 = 4;
		public static final int PAR5 = 5;
		public static final int PAR6 = 6;


		protected boolean isValid = false;
		protected String usage = "usage not provided";
		protected String result = null;
		protected Environment env = null;

		public void setUsage(String u) {
			usage = u;
		}

		public void setEnv(Environment e) {
			if (e == null) {
				throw new IllegalArgumentException("Environment is null");
			}
			this.env = e;
		}


		protected void init() {
			isValid = false;
			setResult(null);

		}

		public String _DEBUGSHOW() {
			return showUsage();
		}


		public abstract void setArgs(String[] args);

		public abstract Command create(String[] args);

		public abstract void execute();

		protected String showUsage() {
			return usage;
		}



		protected void setResult(String r) {
			result = r;
		}

		public String getResult() {
			return result;
		}
	}
