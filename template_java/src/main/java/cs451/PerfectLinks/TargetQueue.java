package cs451.PerfectLinks;

import cs451.GlobalCfg;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

//Lock design inspired by:
//https://codingtechroom.com/question/suspend-thread-until-condition

public class TargetQueue {
    Queue<PLMessageRegular> q;
    int nextMessageNo;

    //Concurrency vars
    final Object queueLock;
    boolean canPush;
    private final int MAX_QUEUE_SIZE = 100;

    public TargetQueue(){
        this.q = new LinkedBlockingQueue<>();
        this.nextMessageNo = Integer.MIN_VALUE;
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
            nextMessageNo +=1;

            if(q.size() >= MAX_QUEUE_SIZE){
                canPush = false;
            }
        }

    }


    public int getNextMessageNo(){
        synchronized (queueLock) {
            return nextMessageNo;
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

    public Object getQueueLock(){
        return queueLock;
    }

    public void tryAck(PLAckMessage am){
        synchronized (queueLock) {
            //todo change
            Optional<PLMessageRegular> current = getCurrent();
            if(current.isEmpty()){
                if(GlobalCfg.PL_ACK_DEBUG) {
                    System.out.println("Acked, but queue was empty");
                }
                return;
            }
            int currentMessageNo = current.get().getMetadata().getMessageNo();
            int incomingMessageNo = am.getMetadataForAckedMessage().getMessageNo();

            if(GlobalCfg.PL_ACK_DEBUG) {
                System.out.println(incomingMessageNo + " " + currentMessageNo);
            }

            if (incomingMessageNo == currentMessageNo) {
                if(GlobalCfg.PL_ACK_DEBUG) {
                    System.out.println("iterated!");
                }
                iterate();
            }
        }
    }
}
