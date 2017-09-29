
package root;

import java.io.IOException;

import cmd.CommandServer;
import offline.UserVault;
import world.IDDispenser;


public class Server {

	public static void main(String[] args) throws IOException {

		Factory f = new Factory();
		UserVault u = new UserVault();
		u.registerCommands(f,"/u/");

		IDDispenser d = new IDDispenser(0,1000);
		d.registerCommands(f,"/id/");
                

 		CommandServer s = null;
      		try {
  				s = new CommandServer(f,WorldSize.SERVICE_PORT);
  			} catch (IOException e) {
  				System.out.println("Server exiting abnormally:" + e.getMessage() );
  				System.exit(1);
  			}
			s.process();

	}


}