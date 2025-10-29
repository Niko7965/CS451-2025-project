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
    final Object queueLock;
    boolean canPush;
    private final int MAX_QUEUE_SIZE = 100;

    public TargetQueue(){
        this.q = new LinkedBlockingQueue<>();
        this.messageNo = Integer.MIN_VALUE;
        this.queueLock = new Object();
        this.canPush = true;
    }

    public Optional<PLMessageRegular> getCurrent(){
        synchronized (queueLock) {
            if (!q.isEmpty()) {
                return Optional.of(q.peek());
            }
            return Optional.empty();
        }
    }

    public void enqueueMessage(PLMessageRegular m) throws InterruptedException {
        synchronized (queueLock){
            if(!canPush){
                //Wait releases the above lock!
                queueLock.wait();
            }
            q.add(m);
            messageNo+=1;

            if(q.size() >= MAX_QUEUE_SIZE){
                canPush = false;
            }
        }

    }


    public int getNextMessageNo(){
        synchronized (queueLock) {
            return messageNo;
        }
    }

    public void iterate(){
        synchronized (queueLock){
            q.poll();
            if(q.size() < MAX_QUEUE_SIZE){
                canPush = true;
                queueLock.notifyAll();
            }
        }

    }

    public void tryAck(PLAckMessage am){
        synchronized (queueLock) {
            //todo change
            Optional<PLMessageRegular> current = getCurrent();
            if(current.isEmpty()){
                return;
            }
            int currentMessageNo = current.get().getMetadata().getMessageNo();

            if (am.getMetadataForAckedMessage().getMessageNo() == currentMessageNo) {
                iterate();
            }
        }
    }
}
