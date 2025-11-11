package cs451.URB;

import java.util.ArrayList;
import java.util.HashMap;

public class Acknowledgements {

    int noOfTargets;

    //todo
    //Should not hash on inner payload, only on sender and message no
    HashMap<URBMessage,MessageAckStatus> ackStatusHashMap;


    public Acknowledgements(int noOfTargets){
        this.noOfTargets = noOfTargets;

    }

    public void deleteMessages(ArrayList<URBMessage> messagesToDelete){
        for(URBMessage message : messagesToDelete){
            ackStatusHashMap.remove(message);
        }
    }

    public void addAck(URBMessage message, int acker){
        if(!ackStatusHashMap.containsKey(message)){

            ackStatusHashMap.put(message,new MessageAckStatus(noOfTargets));

        }
        MessageAckStatus ackStatus = ackStatusHashMap.get(message);
        ackStatus.ack(acker);
    }

    public ArrayList<URBMessage> getDeliverableMessages(){
        ArrayList<URBMessage> deliverables = new ArrayList<>();

        for(URBMessage pl : ackStatusHashMap.keySet()){
            MessageAckStatus status = ackStatusHashMap.get(pl);
            if(!status.hasBeenDelivered() && status.isQuorumAcked(noOfTargets)){
                status.setDelivered();
                deliverables.add(pl);
            }
        }
        return deliverables;
    }


    public ArrayList<URBMessage> getFullyAckedMessages() {
        ArrayList<URBMessage> fullyAckedMessages = new ArrayList<>();
        for(URBMessage pl : ackStatusHashMap.keySet()){
            MessageAckStatus status = ackStatusHashMap.get(pl);
            if(status.isFullyAcked(noOfTargets)){
                status.setDelivered();
                fullyAckedMessages.add(pl);
            }
        }

        return fullyAckedMessages;
    }
}
