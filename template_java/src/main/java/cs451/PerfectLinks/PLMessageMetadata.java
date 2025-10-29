package cs451.PerfectLinks;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class PLMessageMetadata {
    private final boolean isAck;
    private final int messageNo;
    private final int senderId;
    private final int receiverId;

    public PLMessageMetadata(boolean isAck,int messageNo, int senderId, int receiverId ){
        this.isAck = isAck;
        this.messageNo = messageNo;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }


    public boolean isAck() {
        return isAck;
    }

    public int getMessageNo() {
        return messageNo;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public PLMessageMetadata ackMetaData(){
        return new PLMessageMetadata(true,messageNo,senderId,receiverId);
    }

    public static PLMessageMetadata fromBytes(byte[] bytes){
        boolean isAck = bytes[0] >= 1;
        int senderId = (int) bytes[1] +1;
        int receiverId = (int) bytes[2] +1;

        byte[] messageNoBytes = Arrays.copyOfRange(bytes,3,7);
        int messageNo = bytesToInt(messageNoBytes);
        return new PLMessageMetadata(isAck,messageNo,senderId,receiverId);

    }

    public byte[] asBytes(){
        // NEW IMPROVED FORMAT
        //[0] IsAck (0 -> notAck) (>=1 -> ack)
        //[1] Sender id
        //[2] Receiver id
        //[3-6] Message no (4 Byte -> one 32bit int)
        byte[] bytes = new byte[8];

        //IsAck
        bytes[0] = 0;
        if(isAck){
            bytes[0] = 1;
        }

        //Sender (Stored as one less)
        bytes[1] = (byte) (senderId-1);

        //Receiver (Stored as one less)
        bytes[2] = (byte) (receiverId-1);

        byte[] messageNoBytes = intToBytes(messageNo);

        bytes[3] = messageNoBytes[0];
        bytes[4] = messageNoBytes[1];
        bytes[5] = messageNoBytes[2];
        bytes[6] = messageNoBytes[3];

        return bytes;

    }

    //https://javadeveloperzone.com/java-basic/java-convert-int-to-byte-array/#2_int_to_byte_array
    private static byte[] intToBytes(int i){
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    private static int bytesToInt(byte[] bytes){
        return ByteBuffer.wrap(bytes).getInt();
    }

    public boolean equals(PLMessageMetadata other){
        return this.isAck == other.isAck && this.senderId == other.getSenderId() && this.receiverId == other.receiverId && this.messageNo == other.getMessageNo();
    }
}
