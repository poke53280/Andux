package net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.io.OptionalDataException;


/**
 * Converts between arrays of bytes and arrays of any serializable
 * Object(s), also of mixed type.
 * <P>
 * This process is slow, but the ObjectStream is very handy early in projects.
 * <P>
 * This converter catches every possible exception, returning a complaint to the
 * log and null to the caller if an exception is triggered.
 *
 * @author Anders E. Topper
 * @since 100999
 */


public class ObjectArrayByteArrayConverter {

        private static final String OUTNAME = "ObjectArrayByteArrayConverter.toByteArray()";
        private static final String IN_NAME = "ObjectArrayByteArrayConverter.toObjectArray()";


        public static byte[] toByteArray(Object[] value) {

        ObjectOutputStream s = null;

        if (value == null || value.length == 0) {
             System.out.println(OUTNAME + ":  data is null or 0");
             return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();


        try {

             s = new ObjectOutputStream(out);

        } catch(IOException e) {
            System.out.println(OUTNAME +
                "construction: Cast by underlying stream:" + e.getMessage() );
            return null;

        }

        try {

           s.writeObject(value);

        } catch(InvalidClassException ic) {
            System.out.println(OUTNAME +
                "writeObject: Something is wrong with a class used by serialization " +
            ic.getMessage() );
            return null;
        } catch(NotSerializableException ns) {
            System.out.println(OUTNAME +
            "writeObject: object does not implement the "
                + "java.io.Serializable interface. " + ns.getMessage() );
            return null;
        } catch(IOException ie) {
             System.out.println(OUTNAME +
                "writeObject: An exception was thrown by the underlying OutputStream: " +
                ie.getMessage() );
             return null;
        }

        try {

            s.flush();

        } catch(IOException e) {
             System.out.println(OUTNAME +
                 "flush: An I/O error has occurred. " + e.getMessage() );
            return null;
        }


        try {

           s.close();

      } catch(IOException e) {
            System.out.println(OUTNAME +
                 "close: An I/O error has occurred. " + e.getMessage() );
            return null;
      }

     return out.toByteArray();


    }

    public static Object[] toObjectArray(byte[] in) {

        Object[] data = null;
        ObjectInputStream s = null;

        if (in == null || in.length == 0) {
            return null;

        }

        ByteArrayInputStream inStream = new ByteArrayInputStream(in);


        try {

       	     s = new ObjectInputStream(inStream);



        } catch (StreamCorruptedException se) {
            System.out.println(IN_NAME + "new stream: The version or magic number are incorrect");
            se.printStackTrace();
            return null;


        } catch (IOException e) {
            System.out.println(IN_NAME + "new stream: Exception occured in underlying stream");
            e.printStackTrace();
            return null;

        }


      	  try {

              data = (Object[])s.readObject();



         } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage() );
                System.out.println(IN_NAME + "readObject(): Class of a serialized object cannot be found.");
                e.printStackTrace();
                return null;

        } catch (InvalidClassException e) {
                System.out.println(e.getMessage() );
                System.out.println(IN_NAME + "readObject(): Something is wrong with a class used by serialization.");
                e.printStackTrace();
                return null;

         } catch (StreamCorruptedException e) {
                System.out.println(e.getMessage() );
                System.out.println(IN_NAME + "readObject(): Control information in the stream is inconsistent.");
                e.printStackTrace();
                return null;

         } catch (OptionalDataException e) {
                System.out.println(e.getMessage() );
                System.out.println(IN_NAME + "readObject(): Primitive data was found in the stream instead of objects.");
                e.printStackTrace();
                return null;


         }  catch (IOException ioe) {
           	    ioe.printStackTrace();
                System.out.println(IN_NAME + "readObject(): Any of the usual Input/Output related exceptions.");
                return null;
        	}

         try {
              s.close();
        } catch (IOException ce) {
              ce.printStackTrace();
              System.out.println(IN_NAME + "close(): I/O-Error while closing");
              return null;

        }

       return data;


    }


}
