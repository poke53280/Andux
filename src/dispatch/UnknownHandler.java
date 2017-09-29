
package dispatch;

import net.NetMessage;
import java.util.Observable;
import java.util.Observer;
import net.IPString;

public class UnknownHandler implements Observer {


		public UnknownHandler() {

		}

		public void update(Observable o, Object arg) {

			Dispatcher d = (Dispatcher) arg;

			System.out.println("UnknownHandler.update()" +
							" configuring new dispatcher");
		}

	}
