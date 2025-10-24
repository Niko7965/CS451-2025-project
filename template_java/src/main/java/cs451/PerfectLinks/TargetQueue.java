package cs451.PerfectLinks;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class TargetQueue {
    Queue<PLMessageRegular> q;
    int messageNo;

    public TargetQueue(){
        this.q = new LinkedBlockingQueue<>();
        this.messageNo = Integer.MIN_VALUE;
    }

    public Optional<PLMessageRegular> getCurrent(){
        if(!q.isEmpty()){
            return Optional.of(q.peek());
        }
        return Optional.empty();
    }

    public void enqueueMessage(PLMessageRegular m){
        q.add(m);
    }

    public int getMessageNo(){
        return messageNo;
    }

    public void iterate(){
        q.poll();
        messageNo++;
    }

    public void tryAck(PLAckMessage am){
        if(am.message.getMessageNo() == messageNo){
            iterate();
        }
    }
}
