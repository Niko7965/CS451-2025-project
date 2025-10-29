package cs451.PerfectLinks;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

//Lock design inspired by:
//https://codingtechroom.com/question/suspend-thread-until-condition


/*
Sliding window q
Maintains that the difference between smallest and largest message no. is <= 100

var smallest messageNo contains the value of the current smallest messageNo

 */

public class TargetQueue {
    int lowestFreeMessageNo;

    Queue<PLMessageRegular> q;
    private int smallestNonAckedMessageNo;


    //Concurrency vars
    final Object queueLock;
    private final int MAX_QUEUE_SIZE = 100;

    public TargetQueue(){
        this.q = new LinkedBlockingQueue<>();
        this.queueLock = new Object();
        this.smallestNonAckedMessageNo = Integer.MIN_VALUE;
        this.lowestFreeMessageNo = Integer.MIN_VALUE;
    }

    public List<PLMessageRegular> getCurrentMessages(){
        synchronized (queueLock) {
            return new ArrayList<>(q);
        }
    }



    public void enqueueMessage(PLMessageRegular m) throws InterruptedException {
        synchronized (queueLock){

            /*
            Require ordered entry
            Since otherwise, we could do:
            [1]
            [1,100]
            [100]
            [100,200]
            And skip values in between
             */
            if(m.getMetadata().getMessageNo() != smallestNonAckedMessageNo + MAX_QUEUE_SIZE){
                //Wait releases the above lock!
                queueLock.wait();
            }

            q.add(m);
        }

    }


    public int getNextMessageNo(){
        synchronized (queueLock) {
            int toReturn = lowestFreeMessageNo;
            lowestFreeMessageNo++;
            return toReturn;
        }
    }



    public Object getQueueLock(){
        return queueLock;
    }

    public void updateSmallestMessageNoInList(){
        synchronized (queueLock){
            Optional<Integer> newMin = q.stream().map(m -> m.getMetadata().getMessageNo()).min(Integer::compare);
            if(newMin.isPresent()){
                smallestNonAckedMessageNo = newMin.get();
            }
        }
    }

    public void ackAnyInQ(PLAckMessage am){
        synchronized (queueLock){
            if(q.isEmpty()){
                return;
            }

            Optional<PLMessageRegular> match = q.stream().
                    filter(m ->
                            m.getMetadata().getMessageNo() ==
                            am.getMetadata().getMessageNo())
                    .findFirst();

            if(match.isPresent()) {
                q.remove(match.get());

                if (match.get().getMetadata().getMessageNo() == smallestNonAckedMessageNo) {
                    updateSmallestMessageNoInList();
                }
            }
        }
    }


}
