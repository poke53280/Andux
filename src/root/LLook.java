
package root;

import java.net.*;

public class LLook {


public static void main(String[] args)  {

	InetAddress local = null;
	InetAddress[] addr = null;

	try {

		local = java.net.InetAddress.getLocalHost();
		addr = java.net.InetAddress.getAllByName( local.getHostName() );
	} catch (Exception e) {
		System.out.println("Exception caught");
	}


	if (addr == null) {
		return;
	}

	for(int i = 0;i < addr.length; i++) {
		System.out.println("addr=" + addr[i].toString() );
	}

}

}
