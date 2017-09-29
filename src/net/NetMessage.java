package net;

import area.Area;
import area.KeyArea;
import area.WarpArea;

public final class NetMessage {

	//private static final char END_CHAR = '|';
	//private static final int STRING_MAX = 100;

	private static final int FREE_POOL_SIZE = 40; 		// Free pool capacity.
	private static final int DEFAULT_CAPACITY = 500;	// Default size of data

    private static final NetMessage[] freeStack = new NetMessage[FREE_POOL_SIZE];
    private static int countFree;
	private static int numberOfConstructed;


	/*
	* Returns a NetMessage with byte array size capacity.
	*/

    public static NetMessage getInstance(int capacity) {
        NetMessage result;
        if (countFree == 0) {
            result = new NetMessage(capacity);
			numberOfConstructed++;
			//System.out.println("Constructed new message, # " + numberOfConstructed);
        } else {
            result = freeStack[--countFree];
			result.setSize(capacity);
			//System.out.println("NetMessage found in pool");
        }
		return result;
	}

	/*
	* Returns a NetMessage with a default byte array size.
	*/

	public static NetMessage getInstance() {
		return getInstance(DEFAULT_CAPACITY);
	}

	/*
	* Assumes NetMessage m is free, and reuses it.
	*/
    public static void freeInstance(NetMessage m) {
        if (countFree < FREE_POOL_SIZE) {
			//System.out.println("Messaged released and pooled");
            m.setSocket(-1L);
            freeStack[countFree++] = m;
        }
    }


	protected byte[] data;
	protected int length;		//De facto length of contained array

	protected long socket;	//To or from socket address

	private NetMessage(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("capacity <= 0");
		}
		data = new byte[capacity];
		//System.out.println("NetMessage constructed");
		length = capacity;
	}


	/**
	* Sets size to 1 and sets the port in first position(0)
	*/
	public void init(int port) {
		setSize(1);
		setByte(0,(byte) port);
	}

	public void initPacked(int length) {
		setSize(4);
		setInteger(0,length);

	}


	/**
	* Sets the socket address concerning this message (in or out bound)
	*/

	public void setSocket(long s) {
		socket = s;
	}

	/**
	* Returns the socket address concerning this message (in or out bound)
	*/
	public long getSocket() {
		return socket;
	}

	/**
	* Sets/resets internal size.
	*/
	public void setSize(int size) {
		//This method is used a lot.
		//The conditional checks are time-consuming.
		if (size <= 0) {
			throw new IllegalArgumentException("size <= 0");
		}

		if (size > data.length) {
			byte[] dnew = new byte[size];
			System.arraycopy(data,0,dnew,0,data.length);
			data = dnew;
			//System.out.println("NetMessage rebuilt");
		}
		length = size;

	}

	/**
	* Returns length of data array
	*/
	public int getSize() {
		return length;
	}


	/**
	* Returns reference to internal data. Real data size
	* is given by getSize(), not length of this array.
	*/
	public byte[] getData() {
		return data;
	}



	/**
	* Copy all of d into NetMessage
	*/
	public void copy(byte[] d) {
		copy(d, 0, d.length, 0);
	}


	/**
	* Copy from d starting from and including
	* element at index offset.
	*/
	public void copy(byte[] d, int offset) {
		copy(d, offset, d.length, 0);

	}


	/**
	* Copy starting from and including index offset in byte[] d.
	* ending at and excluding index stop. (i.e. last included is
	* index (stop -1)
	*/
	public void copy (byte[] d, int offset, int stop) {
		copy(d, offset, stop, 0);

	}

	public void copy(byte[] d, int offset, int stop, int destOffset) {
		if (d == null) {
			throw new NullPointerException("input is null");
		}

		int copyLength = stop - offset;

		if (copyLength <= 0) {
			throw new IllegalArgumentException("<=0 elements to copy");
		}

		if (stop > d.length) {
			throw new IllegalArgumentException("Stop arg out of bounds");
		}

		if (destOffset < 0) {
			throw new IllegalArgumentException("destOffset < 0");
		}

		setSize(copyLength + destOffset);
		System.arraycopy(d,offset,data,destOffset,copyLength);

	}

	/*
	* Copies message data into d. Starting from and including offset in message data, and 0 in d.
	*/

	public void getCopy(byte[] d, int offset) {
		if (offset > length || offset < 0) {
			throw new IllegalArgumentException("offset arg too big or small: " + offset);
		}
		System.arraycopy(data, offset, d,0,length -offset);
	}


	//Add one to the length of message
	public void increase() {
		setSize(length +1);
	}

	//Set message one shorter
	public void decrease() {
		setSize(length -1);
	}

	/*
	* Copies contents of m into end of message, also increasing local capacity to hold it.
	*/
	public void addMessage(NetMessage m) {
		byte[] d = m.getData();
		int s = m.getSize();

		//System.out.println("NetMessage.addMessage: start index = " + s);

		copy(d,0,s, length);
	}


	/*
	* Copies the last l bytes of message into m.
	* Then cut those bytes from this message so that only the message beginning is left.
	*/

	public void cutTo(NetMessage m, int l) {
		if (l > length) {
			throw new IllegalArgumentException("offset bigger than size");
		}

		m.setSize(l);				//Adjust size of receiving message
		byte[] d = m.getData();
		getCopy(d,length - l);		//Copy local message end to it.
		setSize(length - l);		//Adjust local data size.

	}


	public void append(int i) {
		int pos = length;
		setSize(length + 4);
		setInteger(pos,i);
	}


	public void append(float f) {
		int pos = length;
		setSize(length + 4);
		setFloat(pos,f);
	}


	public void append(int[] n, int max) {
		if (max > n.length) throw new IllegalStateException("value too large");

		//Not very effective. Instead, reset size once and fill after. (unroll)
		for (int i = 0; i < max; i++) {
			append(n[i]);
		}
		append(max);	//The length signal
	}

	public void append(long l) {
		int pos = length;
		setSize(length +8);
		setLong(pos,l);
	}

	public void append(char c) {
		int pos = length;
		setSize(length +2);
		setChar(pos,c);
	}


	public void append(long[] n, int max) {
		if (max > n.length) throw new IllegalStateException("value too large");

		for (int i = 0; i < max; i++) {
			append(n[i]);
		}
		append(max);	//The length signal
	}

	public void append(String s) {
		if (s == null || s.length() < 1)
			throw new IllegalArgumentException("string is empty");

		int mymax = s.length() -1;
		for(int i = mymax; i >= 0; i--) {
			char c = s.charAt(i);
			append(c);
		}
		append(s.length());

	}

	public String chopString() {
		if (length < 2 + 2) throw new IllegalStateException("Message to small");

		int stringSize = chopInt();
		if (stringSize < 1) throw new IllegalStateException("stringsize=" + stringSize);

		StringBuffer b = new StringBuffer(stringSize);
		for(int i = 0; i < stringSize; i++) {
			char c = chopChar();
			b.append(c);
		}
		return b.toString();
	}



	public int chopInt() {
		if (length < 4) throw new IllegalStateException("Message to small");

		int i = getInteger(length-4);
		setSize(length-4);
		return i;
	}

	public char chopChar() {
		if (length < 2) throw new IllegalStateException("Message to small");

		char c = getChar(length-2);
		setSize(length-2);
		return c;

	}



	public float chopFloat() {
		if (length < 4) throw new IllegalStateException("Message to small");

		float f = getFloat(length-4);
		setSize(length-4);
		return f;
	}


	public int peekLastInt() {
		if (length < 4) throw new IllegalStateException("Message to small");
		int i = getInteger(length-4);
		return i;

	}


	//Chop array from message into array. Returns number of elements in array
	public int chopArray(int[] n) {

		if(length < (1+4) ) throw new IllegalStateException("No room for zero array");

		int arraySize = chopInt();


		if (arraySize == 0) {
			//System.out.println("Found zero element int array in message");
			return 0;
		}

		//System.out.println("Found non-zero int array in message of size " + arraySize);

		//size(4 bytes) is now already chopped
		if(length < (1+4) ) throw new IllegalStateException("No room for non-zero int array, size=" + arraySize);


		if (arraySize > (1 + length*4) ) throw new IllegalStateException("Array too big");
		if (n.length < arraySize) throw new IllegalArgumentException("input array too small");


		for (int i = arraySize-1; i >=0; i--) {
			n[i] = chopInt();	//Takes from back, fills in back.
		}

		return arraySize;

	}


	public long chopLong() {
		if (length < 8) throw new IllegalStateException("Message to small");
		long l = getLong(length-8);
		setSize(length-8);
		return l;

	}

	//Chop array from message into array. Returns number of elements in array
	public int chopArray(long[] n) {


		if(length < (1+4) ) throw new IllegalStateException("No room for zero array");

		int arraySize = chopInt();

		if (arraySize == 0) {
			//System.out.println("Found zero element long array in message");
			return 0;
		}

		//size(4 bytes) is now already chopped
		if(length < (1+8) ) throw new IllegalStateException("No room for non-zero long array, size=" + arraySize);


		//System.out.println("Found non-zero long array in message of size " + arraySize);

		if (arraySize > (1 + length*8) ) throw new IllegalStateException("Array too big");
		if (n.length < arraySize) throw new IllegalArgumentException("input array too small");


		for (int i = arraySize-1; i >=0; i--) {
			n[i] = chopLong();	//Takes from back, fills in back.
		}

		return arraySize;

	}


	private void setDouble(int i, double value) {
		long t = Double.doubleToLongBits(value);
		setLong(i, t);
	}


	private double getDouble(int i) {
	     long t = getLong(i);
	     return Double.longBitsToDouble(t);
	}


	private void setLong(int i, long v) {
		if (length > i + 7) {
			data[i + 0] = (byte) v;
		    data[i + 1] = (byte)(v >> 8);
	        data[i + 2] = (byte)(v >> 16);
	        data[i + 3] = (byte)(v >> 24);
		    data[i + 4] = (byte)(v >> 32);
		    data[i + 5] = (byte)(v >> 40);
		    data[i + 6] = (byte)(v >> 48);
	        data[i + 7] = (byte)(v >> 56);
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}
	}


	private long getLong(int i) {
		if (length > i + 7) {
			return
		      (long)(data[i + 7])      << 56 |
		      (long)(data[i + 6]&0xff) << 48 |
		      (long)(data[i + 5]&0xff) << 40 |
		      (long)(data[i + 4]&0xff) << 32 |
		      (long)(data[i + 3]&0xff) << 24 |
		      (long)(data[i + 2]&0xff) << 16 |
		      (long)(data[i + 1]&0xff) <<  8 |
		      (long)(data[i + 0]&0xff);
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}

	}


	private void setFloat(int i, float value) {
		int val = Float.floatToIntBits(value);
		setInteger(i, val);
	}


	private float getFloat(int i) {
		int val = getInteger(i);
		return Float.intBitsToFloat(val);
	}


	private void setInteger(int i, int value) {
		if (length > i + 3) {
			data[i + 0] = (byte) ((value & 0xff000000) >> 24);
			data[i + 1] = (byte) ((value & 0xff0000) >> 16);
			data[i + 2] = (byte) ((value & 0xff00 ) >> 8);
			data[i + 3] = (byte) (value & 0xff);
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}

	}

	public int getInteger(int i) {
		if (length > i + 3) {
			return
				(((int) (data[i + 0] & 0xFF)) << 24) |
				(((int) (data[i + 1] & 0xFF)) << 16) |
				(((int) (data[i + 2] & 0xFF)) << 8)  |
			 	  (int) (data[i + 3] & 0xFF);

		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}
	}


	private void setChar(int i, char value) {
		if (length > i + 1) {
			data[i + 0] = (byte) ((value & 0xff00 ) >> 8);
			data[i + 1] = (byte) (value & 0xff);
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}

	}


	private char getChar(int i) {
		if (length > i + 1) {
			int low = data[i+1] & 0xff;
			int high = data[i+0];
			return (char)(high << 8 | low);
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}
	}


	private short getShort(int i) {
		if (length > i + 1) {
			int low = data[i+1] & 0xff;
			int high = data[i+0] & 0xff;
			return (short)(high << 8 | low);

		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}
	}


	private void setShort(int i, short value) {
		if (length > i + 1) {
			data[i + 0] = (byte) ((value & 0xff00 ) >> 8);
			data[i + 1] = (byte) (value & 0xff);
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}

	}


	private void setBoolean(int i, boolean b) {
		if (length > i) {
			if (b) {
				data[i] = (byte) 1;
			} else {
				data[i] = (byte) 0;
			}
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}
	}

	private boolean getBoolean(int i) {
		if (length > i) {
			if (data[i] == (byte) 1 ) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}

	}


	public void setByte(int i, byte value) {
		if (length > i) {
			data[i] = value;
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i + ", length = " + length);
		}

	}


	public byte getByte(int i) {
		if (length > i) {
			return data[i];
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i + ", length = " + length);
		}
	}

	public int getByteAsInt(int i) {
		if (length > i) {
			return (int) data[i] & 0xFF;
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i + ", length = " + length);
		}

	}


	public String status() {
		StringBuffer b = new StringBuffer(100);
		b.append("data length: " + length);
		b.append(", array length: " + data.length);
		return b.toString();
	}

	public String list() {
		StringBuffer b = new StringBuffer(3*length);
		for (int i = 0; i < length; i++) {
			b.append('\n');
			b.append(i);
			b.append(':');
			b.append(getByteAsInt(i) );
		}
		return b.toString();
	}

	//Resurrect an area
	public Area chopArea() {
		int y = chopInt();
		int x = chopInt();
		return new Area(x,y);
	}

	//Pack area to message
	public void append(Area a) {
		append(a.getX() );
		append(a.getY() );
	}

	//Resurrect a keyarea
	public KeyArea chopKeyArea() {
		int y = chopInt();
		int x = chopInt();
		long key = chopLong();
		return new KeyArea(key,x,y);
	}

	//Pack KeyArea to message
	public void append(KeyArea a) {
		long key = a.getKey();
		append(key);
		append(a.getX() );
		append(a.getY() );
	}

	//Pack a WarpArea to message
	public void append(WarpArea a) {
		float x = a.getX();
		float y = a.getY();
		float h = a.getH();
		append(x);
		append(y);
		append(h);
	}

	public WarpArea chopWarpArea() {
		float h = chopFloat();
		float y = chopFloat();
		float x = chopFloat();
		return new WarpArea(x,y,h);
	}

}
