package cmd;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;


public class RandomFile {

   private static final int MAX_RECORDS = 30;  //0..29
   private static final int RECORD_SIZE = 1024; //bytearray 0.99

   private RandomAccessFile file;

	public RandomFile (String filename, boolean readOnly)
								throws FileNotFoundException,
											SecurityException {

		if (readOnly) {
			file = new RandomAccessFile(filename, "r");
		} else {
			file = new RandomAccessFile(filename, "rw");
		}
	}

	public void format(byte blank) {
		byte[] b = new byte[RECORD_SIZE];
		for(int i = 0; i < RECORD_SIZE; i++) {
			b[i] = blank;
		}
		try {
			file.seek(0);
		} catch (IOException ie) {
			System.out.println(ie.getMessage() );
		}
		for (int j = 0; j < MAX_RECORDS; j++) {
			try {
				file.write(b);
			} catch (IOException ie) {
				System.out.println(ie.getMessage() );
			}
		}
	}

	public String readLine() {
		String s = null;
		try {
			s = file.readLine();
			//System.out.println("RandomFile: line read");
		} catch (IOException ie) {
			System.out.println("RandomFile.readLine: " + ie.getMessage() );
		}
		return s;
	}


	public void read(long index, byte[] b) {
		try {
			file.seek(index*RECORD_SIZE);
			file.read(b);
		} catch (IOException ie) {
			System.out.println(ie.getMessage() );
		}

	}

	public void write(long index, byte[] b) {
		try {
			file.seek(index*RECORD_SIZE);
			file.write(b);
		} catch(IOException ie) {
			System.out.println(ie.getMessage() );
			//
		}

	}

	public void close() {
		try {
			file.close();
		} catch (IOException ie) {
			System.out.println(ie.getMessage() );
			//
		}

	}

}