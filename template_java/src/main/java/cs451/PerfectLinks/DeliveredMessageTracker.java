package cs451.PerfectLinks;

import java.util.HashMap;

public class DeliveredMessageTracker {
    HashMap<Integer,Integer> messageNoForSender;

    public DeliveredMessageTracker(){
        messageNoForSender = new HashMap<>();
    }


    /*
    Accept messages iff their message number is exactly one higher
    than previous message (or is first message)
     */
    public boolean shouldDeliver(PLMessageRegular m){
        int sender = m.getMetadata().getSenderId();
        int messageNo = m.getMetadata().getMessageNo();

        if(!messageNoForSender.containsKey(sender)){
            messageNoForSender.put(sender,messageNo);
            return true;
        }

        int previousMessageNo = messageNoForSender.get(sender);
        if(previousMessageNo + 1 == messageNo){
            messageNoForSender.put(sender,messageNo);
            return true;
        }
        return false;
    }

    public boolean hasReceived(PLMessageRegular m){
        int sender = m.getMetadata().getSenderId();
        int messageNo = m.getMetadata().getMessageNo();

        if(!messageNoForSender.containsKey(sender)){
            return false;
        }

        int previousMessageNo = messageNoForSender.get(sender);
        return previousMessageNo >= messageNo;
    }
}
