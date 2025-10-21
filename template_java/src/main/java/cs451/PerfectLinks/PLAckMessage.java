package cs451.PerfectLinks;

public class PLAckMessage extends PLMessage {
    public PLMessageRegular message;
    public int hostToAck;
    public int hostThatAcks;

    public PLAckMessage(PLMessageRegular m, int hostToAck, int hostThatAcks){
        this.message = m;
        this.hostToAck = hostToAck;
        this.hostThatAcks = hostThatAcks;
    }

    public PLAckMessage(String[] contents){
        this.hostThatAcks = Integer.parseInt(contents[1]);
        this.hostToAck = Integer.parseInt(contents[2]);

        int messageSender = Integer.parseInt(contents[3]);
        String messagePayload = contents[4];
        int messageReceiver = Integer.parseInt(contents[5]);

        this.message = new PLMessageRegular(messageSender,messagePayload,messageReceiver);

    }

    @Override
    public boolean isAck() {
        return true;
    }
}
