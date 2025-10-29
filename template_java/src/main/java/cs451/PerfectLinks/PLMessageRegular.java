package cs451.PerfectLinks;


import java.io.*;

public class PLMessageRegular extends PLMessage {
    private final PLMessageMetadata metadata;
    private final Object payload;


    public PLMessageRegular(int sender, int receiver, int messageNo, Object payload){
        this.metadata = new PLMessageMetadata(false,messageNo,sender,receiver);
        this.payload = payload;
    }

    public PLMessageRegular(PLMessageMetadata metadata,Object payload){
        this.metadata = metadata;
        this.payload = payload;
    }

    public Object getPayload(){
        return payload;
    }

    public PLMessageMetadata getMetadata(){
        return this.metadata;
    }

    /*
    Sends an ack that the receiver of this message, has recieved this message
    From the sender of this message
     */
    public PLAckMessage simpleAck(){
        return new PLAckMessage(this);
    }

    //https://www.baeldung.com/object-to-byte-array
    public static byte[] payloadToBytes(Object payload) throws IOException {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);
        objectOutStream.writeObject(payload);
        objectOutStream.flush();
        return byteOutStream.toByteArray();
    }

    public static Object payloadFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInStream = new ObjectInputStream(byteArrayInStream);
        return objectInStream.readObject();
    }



    @Override
    public boolean isAck() {
        return false;
    }
}
