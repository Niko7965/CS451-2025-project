package cs451.PerfectLinks;


import java.io.IOException;
import java.util.Arrays;


public abstract class PLMessage {




    public abstract PLMessageMetadata getMetadata();

    public abstract Object getPayload();

    public static PLMessage fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        PLMessageMetadata metadata = PLMessageMetadata.fromBytes(bytes);
        if(metadata.isAck()){
            return new PLAckMessage(metadata);
        }

        //Payload is in byte 8..len
        byte[] payloadBytes = Arrays.copyOfRange(bytes,8,bytes.length);
        Object payload = PLMessageRegular.payloadFromBytes(payloadBytes);

        return new PLMessageRegular(metadata,payload);
    }

    public byte[] toBytes() throws IOException {
        PLMessageMetadata metadata = getMetadata();
        byte[] metadataBytes = metadata.asBytes();
        if(metadata.isAck()){
            return metadataBytes;
        }

        Object payload = getPayload();
        byte[] payloadBytes = PLMessageRegular.payloadToBytes(payload);

        byte[] result = new byte[metadataBytes.length + payloadBytes.length];
        System.arraycopy(metadataBytes,0,result,0,metadataBytes.length);
        System.arraycopy(payloadBytes,0,result,metadataBytes.length,payloadBytes.length);
        return result;
    }


    @Override
    public boolean equals(Object o){

        //If its not the right type, false
        if(!(o instanceof PLMessage)){
            return false;
        }

        PLMessage otherMessage = (PLMessage) o;

        //If metadata isn't same, false
        if(!(otherMessage.getMetadata().equals(this.getMetadata()))){
            return false;
        }

        //Finally we require that either both payloads are identical, or
        //That both are null
        if(otherMessage.getPayload() == null && this.getPayload() == null){
            return true;
        }

        if(otherMessage.getPayload() != null && otherMessage.getPayload() != null){
            return otherMessage.getPayload().equals(this.getPayload());
        }

        return false;
    }






    public abstract boolean isAck();
}
