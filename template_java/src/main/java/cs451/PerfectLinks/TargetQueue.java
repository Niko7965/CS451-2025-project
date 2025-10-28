package cs451.PerfectLinks;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

//Lock design inspired by:
//https://codingtechroom.com/question/suspend-thread-until-condition

public class TargetQueue {
    Queue<PLMessageRegular> q;
    int messageNo;

    //Concurrency vars
    final Object pushLock;
    boolean canPush;
    private final int MAX_QUEUE_SIZE = 100;

    public TargetQueue(){
        this.q = new LinkedBlockingQueue<>();
        this.messageNo = Integer.MIN_VALUE;
        this.pushLock = new Object();
        this.canPush = true;
    }

    public Optional<PLMessageRegular> getCurrent(){
        if(!q.isEmpty()){
            return Optional.of(q.peek());
        }
        return Optional.empty();
    }

    public void enqueueMessage(PLMessageRegular m) throws InterruptedException {
        synchronized (pushLock){
            if(!canPush){
                //Wait releases the above lock!
                pushLock.wait();
            }
            q.add(m);

            if(q.size() >= MAX_QUEUE_SIZE){
                canPush = false;
            }
        }

    }

    public int getMessageNo(){
        return messageNo;
    }

    public void iterate(){
        synchronized (pushLock){
            q.poll();
            messageNo++;
            if(q.size() < MAX_QUEUE_SIZE){
                canPush = true;
                pushLock.notifyAll();
            }
        }

    }

    public void tryAck(PLAckMessage am){
        if(am.message.getMessageNo() == messageNo){
            iterate();
        }
    }
}
