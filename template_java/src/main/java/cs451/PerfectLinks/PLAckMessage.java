package cs451.PerfectLinks;


public class PLAckMessage extends PLMessage {
    private final PLMessageMetadata metadataForAckedMessage;


    public PLAckMessage(PLMessageMetadata metadata){
        this.metadataForAckedMessage = metadata;
    }

    public PLAckMessage(PLMessageRegular m){
        this.metadataForAckedMessage = m.getMetadata().ackMetaData();
    }

    public PLMessageMetadata getMetadataForAckedMessage(){
        return this.metadataForAckedMessage;
    }


    @Override
    public PLMessageMetadata getMetadata() {
        return metadataForAckedMessage;
    }

    @Override
    public Object getPayload() {
        return null;
    }

    @Override
    public boolean isAck() {
        return true;
    }
}
