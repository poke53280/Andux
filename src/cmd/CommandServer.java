
package cmd;

import java.net.Socket;
import java.io.IOException;

import root.ParseUtil;
import root.Factory;

public class CommandServer extends AbstractServer {

	Factory f;

	public CommandServer(Factory f, int p)  throws IOException {
            super(p);
			this.f = f;
	}

	protected Service getService() throws IOException {
		return new CommandService(serverSocket.accept(),f);
	}


	public class CommandService extends Service {

		protected Factory f;

  		public CommandService(Socket socket, Factory f) {
			super(socket);
			this.f = f;
	}

		protected String runCommand(String in) {

	 		String[] args = ParseUtil.getArgsAsArray(in);
			String s = f.exec(args);
			s = s.replace('\n','|');
	 		return s;
		}

	}

}