package io.handler;

import java.util.Stack;
import java.util.Random;

public class MessageTest {

	public static void main(String[] args) {

		//stringTest();

		//charTest();

		//queueTest();
		//offsetTest();
		//convertTest();
		//insert();
		//floatTest();
		//receiveTest();
		//sendTest();
		//shortTest();
		stringTest2();
	}


	public static void stringTest2() {

		Message m = Message.getInstance(24);
		m.setString(1,"A2",6);

	}


	public static void shortTest() {

		Message m = Message.getInstance(10);

		short s = -1;
		m.setShort(0,s);
		s = m.getShort(0);
		System.out.println("short = " + s );



	}



	public static void stringTest() {
		Message m = Message.getInstance(50);
		m.setString(3,"anders topper",10);

		String s = m.getString(3);
		System.out.println("Result = " + s);

	}



	public static void charTest() {
		Message m = Message.getInstance(202);
		m.setChar(0,'A');
		m.setChar(2,'B');
		m.setChar(4,'Ø');
		m.setChar(6,'ø');

		System.out.println("Char = " + m.getChar(0) );
		System.out.println("Char = " + m.getChar(2) );
		System.out.println("Char = " + m.getChar(4) );
		System.out.println("Char = " + m.getChar(6) );

	}


	public static void queueTest() {
		Stack s = new Stack();

		Message m = Message.getInstance(202);
		byte[] b = new byte[260];
		b[0] = (byte) 3;
		b[199] = (byte) 7;
		m.setByte(0,   (byte)  11);
		m.setByte(1,   (byte)  12);
		m.setByte(201, (byte) 109);
		m.copy(b,0,b.length,1);

		Message.freeInstance(m);

		m = Message.getInstance(202);
		m.setByte(0,   (byte)  11);
		m.setByte(1,   (byte)  12);
		m.setByte(201, (byte) 109);
		m.copy(b,0,b.length,1);

		s.push(m);
		m = null;

		Message m2 = (Message) s.pop();

		Message.freeInstance(m2);

		m = Message.getInstance(202);
		m.setByte(0,   (byte)  11);
		m.setByte(1,   (byte)  12);
		m.setByte(201, (byte) 109);
		m.copy(b,0,b.length,1);
		System.out.println("index 0 : " + m.getByte(0) );
		System.out.println("index 1 : " + m.getByte(1) );
		System.out.println("index 2 : " + m.getByte(2) );
		System.out.println("index 3 : " + m.getByte(3) );

		System.out.println("index 198 : " + m.getByte(198) );
		System.out.println("index 199 : " + m.getByte(199) );
		System.out.println("index 200 : " + m.getByte(200) );
		System.out.println("index 201 : " + m.getByte(201) );
		System.out.println("index 202 : " + m.getByte(202) );

	}






	public static void offsetTest() {
		byte[] b = new byte[260];
		b[0] = (byte) 3;
		b[199] = (byte) 7;

		Message m = Message.getInstance(202);

		m.setByte(0,   (byte)  11);
		m.setByte(1,   (byte)  12);
		m.setByte(201, (byte) 109);

		m.copy(b,0,b.length,1);

		System.out.println("index 0 : " + m.getByte(0) );
		System.out.println("index 1 : " + m.getByte(1) );
		System.out.println("index 2 : " + m.getByte(2) );
		System.out.println("index 3 : " + m.getByte(3) );

		System.out.println("index 198 : " + m.getByte(198) );
		System.out.println("index 199 : " + m.getByte(199) );
		System.out.println("index 200 : " + m.getByte(200) );
		System.out.println("index 201 : " + m.getByte(201) );
		System.out.println("index 202 : " + m.getByte(202) );

		Message.freeInstance(m);

		m = null;

		m = Message.getInstance(202);


		m.setByte(0,   (byte)  11);
		m.setByte(1,   (byte)  12);
		m.setByte(201, (byte) 109);

		m.copy(b,0,b.length,1);

		System.out.println(m.status() );


		System.out.println("index 0 : " + m.getByte(0) );
		System.out.println("index 1 : " + m.getByte(1) );
		System.out.println("index 2 : " + m.getByte(2) );
		System.out.println("index 3 : " + m.getByte(3) );

		System.out.println("index 198 : " + m.getByte(198) );
		System.out.println("index 199 : " + m.getByte(199) );
		System.out.println("index 200 : " + m.getByte(200) );
		System.out.println("index 201 : " + m.getByte(201) );
		System.out.println("index 202 : " + m.getByte(202) );

		Message.freeInstance(m);

		m = null;


	}



	public static void floatTest() {
		float f1 = 0.2332f;
		float f2 = -11231.2f;
		float f3 = 231.0f;
		int i = 3232;

		Message m = Message.getInstance(15);

		m.setFloat(0, f1);
		m.setFloat(4, f2);
		m.setFloat(8, f3);

		byte[] b = new byte[260];
		b[0] = (byte) 3;
		b[199] = (byte) 7;

		m.copy(b,0,b.length,13);

		System.out.println("f2= " + m.getFloat(4) );
		Message.freeInstance(m);
	}

/*
	public static void receiveOne() {

		Message m = Message.getInstance(1000);

		DatagramPacket p = new DatagramPacket(m.getData(), m.getLength() );

		receive(p); //blocking

		int port = p.getPort();
		InetAddress i = p.getAddress();

		long socketID = Convert.getID(i, port);

		plex.push(socketID,m);


	}
*/


	public static void receiveTest() {

		Random r = new Random();
		float t;
		float t0;
		boolean ok = true;

		byte[] buf = new byte[1000];

		long start = System.currentTimeMillis();


		Message m = null;

		for (int i = 0; i< 100000; i++) {

			buf[0]   = (byte) 1;
			buf[999] = (byte) 2;

			m = Message.getInstance();
			m.copy(buf);
			Message.freeInstance(m);
			m = null;
		}

		long stop = System.currentTimeMillis();

		System.out.println("receiveTest : " + (stop - start) + " ms");

	}

	public static void sendTest() {
		Random r = new Random();
		byte[] buf;

		long start = System.currentTimeMillis();


		Message m = null;

		for (int i = 0; i< 100000; i++) {

			m = Message.getInstance(1000);
			m.setFloat(0, r.nextFloat() );

			buf = m.getData();

			Message.freeInstance(m);
			m = null;
		}

		long stop = System.currentTimeMillis();

		System.out.println("sendTest : " + (stop - start) + " ms");



	}



	public static void insert() {

		byte[] b = new byte[5];
		b[0] = (byte) 110;
		b[1] = (byte) 111;
		b[2] = (byte) 112;
		b[3] = (byte) 113;
		b[4] = (byte) 114;

		Message m = Message.getInstance(100);

		System.out.println(m.status() );

		m.copy(b, 0, b.length);

		System.out.println(m.status() );
		System.out.println("0= " + m.getByte(0) );
		System.out.println("1= " + m.getByte(1) );
		System.out.println("2= " + m.getByte(2) );
		System.out.println("3= " + m.getByte(3) );
		System.out.println("4= " + m.getByte(4) );

		Message.freeInstance(m);

	}

	public static void convertTest() {
		Message m = Message.getInstance(20);

		Random r = new Random();

		short t;
		short t0;
		boolean ok = true;
		/*
		for (int i = 0; i< 10000; i++) {
			t = r.nextShort();
			t0 = t;
			m.setShort(0, t);
			t = m.getShort(0);
			if (t != t0 ) {
				System.out.println("t  = " + t);
 				System.out.println("t0 = " + t0);
				break;
			}
		}
		*/

		if (ok) {
			System.out.println("Short Test OK");
		} else {
			System.out.println("Short Test failed");
		}

		Message.freeInstance(m);
	}


}