
package root;

import java.awt.Dimension;
import java.awt.Point;

public interface WorldSize {

	public static final int TIMEOUT = 1;		//Block for this many ms in IP recv.

	public static final int NETWORK_PORT = 7701;
        
        
        // XXX BEGIN
        public static final int SERVICE_PORT = 7601;
        public static final int ITEM_AVERAGE = 1000;
        public static final int MAX_FOCA = 12;
        public static final float NO_FORCE_POS = 0.3f;
        public static final float NO_FORCE_CONST = 0.3f;
        public static final float NO_FORCE_SPEED = 0.3f;
        
        public static final int WORLD_WIDTH = 10000;
        public static final int WORLD_HEIGHT = 10000;
        
        public static final Dimension WORLD_SIZE = new Dimension(WORLD_WIDTH, WORLD_HEIGHT);

        public static final int ID_RANGE = 3400;
        
        
        public static final Dimension DEFAULT_WINDOW = new Dimension(800, 600);
        
        
        public static final float MIN_SPEED = 0.01f;
        
        public static final float X_EXILE = 1000.f;
        public static final float Y_EXILE = 1000.f;

        public static final Dimension EXILED_SIZE = new Dimension(800, 600);
        
        public static final Point EXILED_FOCUS = new Point(800, 600);

        public static final String CONFIGFILE = "pam.txt";
        
        
        public static final int X_LOW = 0;
        
               
        // XXX END
        
        

	public static final int AGENCY_LAUNCH_FREQ = 40; //each tick to launch new agent

	public static final float MAX_SPEED = 0f;	//Max node speed

	public static final int PACKET_SPLIT = 100;	//max number of messages in datagram


	public static final long AGENT_TIMEOUT = 60; //ticks to wait until agent
													//marked as lost

	/*
		ProxyManager will try ports up to
		NETWORK_PORT + PORT_COUNT for local socket
		connection.
	*/
	public static final int PORT_COUNT = 1000;

	//Drop this many out of 1000 packets out.
	public static final int DROP_PACKETS = 25;

	public static final String VERSION = "1.0.3";

	public static final String TITLE = "nDux";



	public static final int FOCUS_THRESHOLD = 10;


	public static int POS_DEVIATION = 50;

	public static float DENSITY = 0.5f;	//nodes/Mpixel

}
