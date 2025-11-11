package cs451.URB;

import java.util.BitSet;

public class MessageAckStatus {
    int noOfAcks;
    BitSet acks;
    boolean hasBeenDelivered;


    public MessageAckStatus(int noOfProcesses){
        this.noOfAcks = 0;
        this.acks = new BitSet(noOfProcesses);
    }

    public void ack(int acker){
        if(!acks.get(acker)){
            noOfAcks += 1;
            acks.set(acker);
        }
    }

    public boolean hasBeenDelivered(){
        return this.hasBeenDelivered;
    }

    public void setDelivered(){
        this.hasBeenDelivered = true;
    }

    public boolean isFullyAcked(int noOfProcesses){
        return this.noOfAcks == noOfProcesses;
    }

    public boolean isQuorumAcked(int noOfProcesses){
        return false; //todo
    }
}
