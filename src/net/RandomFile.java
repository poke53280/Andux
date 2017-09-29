package net;

import java.io.RandomAccessFile;
import java.io.IOException;


public class RandomFile {

   private static final int MAX_RECORDS = 30;  //0..29
   private static final int RECORD_SIZE = 1024; //bytearray 0.99

   private RandomAccessFile file;

   public RandomFile (String filename) {

		try {
			file = new RandomAccessFile(filename, "rw");

		} catch (IOException ie) {
				System.out.println("RandomFile : IOException "
					+ filename);
		} catch (SecurityException se) {
				System.out.println("RandomFile: Not allowed to access: "
						+ filename);
		}

  }

	public RandomFile (String filename, boolean readOnly) throws IOException, SecurityException {

			file = new RandomAccessFile(filename, "r");
	}

	public void format(byte blank) {
		byte[] b = new byte[RECORD_SIZE];
		for(int i = 0; i < RECORD_SIZE; i++) {
			b[i] = blank;
		}
		try {
			file.seek(0);
		} catch (IOException ie) {
			//
		}
		for (int j = 0; j < MAX_RECORDS; j++) {
			try {
				file.write(b);
			} catch (IOException ie) {
				//
			}
		}
	}

	public String readLine() {
		String s = null;
		try {
			s = file.readLine();
		} catch (IOException ie) {
			//
		}
		return s;
	}


	public void read(long index, byte[] b) {
		try {
			file.seek(index*RECORD_SIZE);
			file.read(b);
		} catch (IOException ie) {
			//
		}

	}

	public void write(long index, byte[] b) {
		try {
			file.seek(index*RECORD_SIZE);
			file.write(b);
		} catch(IOException ie) {
			//
		}

	}

	public void close() {
		try {
			file.close();
		} catch (IOException ie) {
			//
		}

	}

}