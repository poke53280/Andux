package net;



/**
 * Simple container holding a byte array and a socket
 * associated with the data byte array.<P>
 * The association may indicate that this byta array
 * was received from the specified socket, is to be
 * sent to the specified socket - or any other.
 * <P>
 * This class does not check its content for validity or null.
 *
 * <P>
 * an AddressedPacket may well be closely associated with a
 * datagram packet received, or to be sent.
 *
 * @author Anders E. Topper
 * @since 080999
 * @see SocketAddress
 */


public class AddressedPacket  {

    byte[] packet = null;
    private SocketAddress socketAddress = null;


    public AddressedPacket(byte[] p, SocketAddress a)  {
        packet = p;
        socketAddress = a;


    }
    /**
     * @return Returns the SocketAddress
     */

    public SocketAddress getAddress() {
        return socketAddress;
    }

   /**
     * @return Returns the value of byte array.
     */

    public byte[] getData() {
        return packet;
    }

}

