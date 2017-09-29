package io.handler;
import net.ObjectArrayByteArrayConverter;


public final class Message {

	private static final char END_CHAR = '|';
	private static final int STRING_MAX = 100;

	private static final int FREE_POOL_SIZE = 40; 		// Free pool capacity.
	private static final int DEFAULT_CAPACITY = 500;	// Default size of data


    private static final Message[] freeStack = new Message[FREE_POOL_SIZE];
    private static int countFree;
	private static int numberOfConstructed;


	/*
	* Returns a Message with byte array size capacity.
	*/
    public static synchronized Message getInstance(int capacity) {
        Message result;
        if (countFree == 0) {
            result = new Message(capacity);
			numberOfConstructed++;
			//System.out.println("Constructed new message, # " + numberOfConstructed);
        } else {
            result = freeStack[--countFree];
			result.setSize(capacity);
			//System.out.println("Message found in pool");
        }
		return result;
	}

	/*
	* Returns a Message with a default byte array size.
	*/
	public static Message getInstance() {
		return getInstance(DEFAULT_CAPACITY);
	}

	/*
	* Assumes Message m is free, and reuses it.
	*/
    public static synchronized void freeInstance(Message m) {
        if (countFree < FREE_POOL_SIZE) {
			//System.out.println("Messaged released and pooled");
            freeStack[countFree++] = m;
        }
    }


	protected byte[] data;
	protected int length;		//De facto length of contained array

	private Message(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("capacity <= 0");
		}
		data = new byte[capacity];
		//System.out.println("Message constructed");
		length = capacity;
	}

	/**
	* Sets/resets internal size.
	*/
	public void setSize(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("size <= 0");
		}

		if (size > data.length) {
			byte[] dnew = new byte[size];
			System.arraycopy(data,0,dnew,0,data.length);
			data = dnew;
			//System.out.println("Message rebuilt");
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
	* Copy all of d into Message
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


	public void setDouble(int i, double value) {
		long t = Double.doubleToLongBits(value);
		setLong(i, t);
	}


	public double getDouble(int i) {
	     long t = getLong(i);
	     return Double.longBitsToDouble(t);
	}


	public void setLong(int i, long v) {
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


	public long getLong(int i) {
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


	public void setFloat(int i, float value) {
		int val = Float.floatToIntBits(value);
		setInteger(i, val);
	}


	public float getFloat(int i) {
		int val = getInteger(i);
		return Float.intBitsToFloat(val);
	}


	public void setInteger(int i, int value) {
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

	/*
	*
	* i : start index
	* value: String
	* size: max size to be used in chars. (Each char - two bytes)
	* char END_CHAR always inserterted at end.
	* String trimmed if too long.
	*/

	public void setString(int i, String value, int size) {

		if (value == null || size < 1 ) {
			System.out.println("No string or no size");
			setChar(i,END_CHAR);
			return;
		}

		if (length > (i + 2*size -1) ) {
			int l;
			char c;
			for (l = 0; l < size -1 && l< value.length();l++) {
				c = value.charAt(l);
				//System.out.println("index " + (i + 2*l) + ": " +c);
				setChar(i+2*l, c);

			}
			//System.out.println("index " + (i + 2*l) + ": |");
			setChar(i+2*l, END_CHAR);

		} else {
			throw new IndexOutOfBoundsException(" Index out of range");
		}
	}

	public String getString(int i) {

		if (length > i + 3) {
			if (getChar(i) == END_CHAR) {
				System.out.println("Message: Got null string, returning null");
				return null;
			}

			StringBuffer b = new StringBuffer(30);
			int myLength = 0;
			char c = ' ';
			int l;
			for (l = i; length > l + 1 ; l += 2) {
				myLength++;
				c = getChar(l);
				//System.out.println("index: " + l + ": " + c);

				if (myLength > STRING_MAX) {
					throw new IndexOutOfBoundsException("String not terminated");
				}

				if (c == END_CHAR ) {
					break;
				}
				b.append(c);

			}
			if (c != END_CHAR) {
				throw new IndexOutOfBoundsException("String too long or not terminated");
			}

			return b.toString();
		}	else {
			throw new IndexOutOfBoundsException(" Index out of range");
		}
	}

	public void setChar(int i, char value) {
		if (length > i + 1) {
			data[i + 0] = (byte) ((value & 0xff00 ) >> 8);
			data[i + 1] = (byte) (value & 0xff);
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}

	}


	public char getChar(int i) {
		if (length > i + 1) {
			int low = data[i+1] & 0xff;
			int high = data[i+0];
			return (char)(high << 8 | low);
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}
	}


	public short getShort(int i) {
		if (length > i + 1) {
			int low = data[i+1] & 0xff;
			int high = data[i+0] & 0xff;
			return (short)(high << 8 | low);

		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}
	}


	public void setShort(int i, short value) {
		if (length > i + 1) {
			data[i + 0] = (byte) ((value & 0xff00 ) >> 8);
			data[i + 1] = (byte) (value & 0xff);
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}

	}


	public void setBoolean(int i, boolean b) {
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

	public boolean getBoolean(int i) {
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
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}

	}


	public byte getByte(int i) {
		if (length > i) {
			return data[i];
		} else {
			throw new IndexOutOfBoundsException(" Index out of range : " + i);
		}
	}


	public String status() {
		StringBuffer b = new StringBuffer(100);
		b.append("Array length : " + data.length);
		b.append("Data length  : " + length);
		return b.toString();
	}

	public static Message toMessage(Object[] d) {

		if (d == null) {
			return null;
		}

		byte[] out = ObjectArrayByteArrayConverter.toByteArray(d );

        if (out == null || out.length == 0) {
             System.out.println("Wire.push: Got 0 byte array, dropped");
             return null;
        }

		Message m = Message.getInstance();
		m.copy(out);
		m.setSize(out.length);
		return m;

	}

	public static Object[] toObjects(Message m) {
		if (m == null) {
			return null;
		}

		Object[] o = ObjectArrayByteArrayConverter.toObjectArray(m.getData() );
		if (o == null) {
			return null;
		} else {
			return o;
		}
	}

/*
short readShortLittleEndian()
{
// 2 bytes
int low = readByte() & 0xff;
int high = readByte() & 0xff;
return (short)(high << 8 | low);
}
Or if you want to get clever and puzzle your readers, you can avoid one mask since the high bits will later be shaved off by conversion back to short.
short readShortLittleEndian()
{
// 2 bytes
int low = readByte() & 0xff;
int high = readByte();
// avoid masking here
return (short)(high << 8 | low);
}


--------------------------------------------------------------------------------
Longs are a little more complicated:
--------------------------------------------------------------------------------


long readLongLittleEndian()
{
// 8 bytes
long accum = 0;
for ( int shiftBy = 0; shiftBy < 64; shiftBy+=8 )
{
// must cast to long or shift done modulo 32
accum |= (long)(readByte() & 0xff) << shiftBy;
}
return accum;
}


--------------------------------------------------------------------------------
In a similar way we handle char and int.
--------------------------------------------------------------------------------


char readCharLittleEndian()
{
// 2 bytes
int low = readByte() & 0xff;
int high = readByte();
return (char)(high << 8 | low);
}


--------------------------------------------------------------------------------


int readIntLittleEndian()
{
// 4 bytes
int accum = 0;
for ( int shiftBy = 0; shiftBy < 32; shiftBy+=8 )
{
accum |= (readByte() & 0xff) << shiftBy;
}
return accum;
}


--------------------------------------------------------------------------------
Floating point is a little trickier. Presuming your data is in IEEE little-endian format, you need something like this:
--------------------------------------------------------------------------------


double readDoubleLittleEndian()
{
long accum = 0;
for ( int shiftBy = 0; shiftBy < 64; shiftBy+=8 )
{
// must cast to long or shift done modulo 32
accum |= ((long)(readByte() & 0xff)) << shiftBy;
}
return Double.longBitsToDouble(accum);
}


--------------------------------------------------------------------------------


float readFloatLittleEndian()
{
int accum = 0;
for ( int shiftBy = 0; shiftBy < 32; shiftBy+=8 )
{
accum |= (readByte() & 0xff) << shiftBy;
}
return Float.intBitsToFloat(accum);
}


--------------------------------------------------------------------------------
You don't need a readByteLittleEndian since the code would be identical to readByte, though you might create one just for consistency:
--------------------------------------------------------------------------------


byte readByteLittleEndian()
{
// 1 byte
return readByte();
}
*/

}