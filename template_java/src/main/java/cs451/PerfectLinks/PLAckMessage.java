package cs451.PerfectLinks;

import java.util.Arrays;

public class PLAckMessage extends PLMessage {
    public PLMessageRegular message;
    public int hostToAck;
    public int hostThatAcks;

    public PLAckMessage(PLMessageRegular m, int hostToAck, int hostThatAcks){
        this.message = m;
        this.hostToAck = hostToAck;
        this.hostThatAcks = hostThatAcks;
    }

    @Override
    public String toString(){
        System.out.println("Message to ack "+message.toString());
        return "ACK "+this.hostThatAcks + " "+this.hostToAck + " " + message.toString();
    }

    public PLAckMessage(String[] contents){
        this.hostThatAcks = Integer.parseInt(contents[1]);
        this.hostToAck = Integer.parseInt(contents[2]);

        String[] regMessageContents = Arrays.copyOfRange(contents,3,contents.length);
        this.message = (PLMessageRegular) PLMessage.fromStringArr(regMessageContents);

    }

    @Override
    public boolean isAck() {
        return true;
    }
}
