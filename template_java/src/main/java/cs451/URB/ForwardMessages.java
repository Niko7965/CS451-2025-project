package cs451.URB;

import cs451.PerfectLinks.PerfectLink;


import java.util.Collection;
import java.util.Optional;

public class ForwardMessages {

    //note - should be called atomically

    /*
    I should do a q for each broadcast
    Each q should be sorted - i should not relay message x from target t,
    without also having relayed message x-1 from target t

    For each target, we keep track of how far they are in each broadcast
    Whenever a target asks for new messages - we then take a message from the
    q where it has the lowest number of messages sent, where that q has a new message

    The really cool way would be to have a pq of q's for each target.
    But as #targets <= 128 that is excessively complex

    Just an array should do, linear search is not so expensive for n = 128


     */

    int selfNo;
    int noOfTargets;
    int[][] messageNoForBroadcastAndTarget; //[broadcastno][targetno]
    IndexQueue[] messageQueuePerBroadcast;

    public ForwardMessages(int noOfHosts, int selfNo){
        this.selfNo = selfNo;
        this.messageQueuePerBroadcast = new IndexQueue[noOfHosts];
        for(int i = 0; i < noOfHosts; i ++){
            messageQueuePerBroadcast[i] = new IndexQueue();
        }


        this.noOfTargets = noOfHosts;
        this.messageNoForBroadcastAndTarget = new int[noOfHosts][noOfHosts];
        for(int i = 0; i < noOfHosts;i++){
            this.messageNoForBroadcastAndTarget[i] =  new int[noOfHosts];
            for(int j = 0; j < noOfHosts; j++){
                messageNoForBroadcastAndTarget[i][j] = 0;
            }
        }
    }

    public void add(URBMessage message){
        //todo contains check
        int sender = message.originalUrbSender;
        messageQueuePerBroadcast[sender].enqueue(message);
    }

    public void removeMessage(URBMessage message){
        IndexQueue broadcastQ = messageQueuePerBroadcast[message.originalUrbSender];
        this.messageNoForBroadcastAndTarget[message.originalUrbSender] = broadcastQ.removeMessage(message,messageNoForBroadcastAndTarget[message.originalUrbSender]);
    }

    public void removeMessages(Collection<URBMessage> messages){
        for(URBMessage message : messages){
            removeMessage(message);
        }
    }


    /**
     * Call to clean queues, that is if there is a broadcast queue,
     * Where every process has already sent up to message k, we can remove the first k indexes
     * Might be redundant if we clean via acks
     */
    public void cleanQueues(){
        for(int broadcastNo = 0; broadcastNo < noOfTargets; broadcastNo++){
            int[] indexesForBroadcastNo = messageNoForBroadcastAndTarget[broadcastNo];
            int cleanedIndexes = messageQueuePerBroadcast[broadcastNo].clean(indexesForBroadcastNo);

            for(int i = 0; i < noOfTargets; i++){
                indexesForBroadcastNo[i] -= cleanedIndexes;
            }
        }
    }

    private Optional<Integer> getQWithLowestMessageNoForTarget(int target){
        int index = -1;
        int smallestMessageNo = Integer.MAX_VALUE;
        for(int i = 0; i < noOfTargets; i++){

            int targetsIndexForBroadcast = messageNoForBroadcastAndTarget[i][target];
            Optional<URBMessage> message = messageQueuePerBroadcast[i].get(targetsIndexForBroadcast);
            if(message.isEmpty()){
                continue;
            }

            if(targetsIndexForBroadcast < smallestMessageNo){
                smallestMessageNo = targetsIndexForBroadcast;
                index = i;
            }
        }

        if(index != -1){
            return Optional.of(index);
        }
        return Optional.empty();
    }




    public void updatePlQueueOfTarget(PerfectLink pl, int target) throws InterruptedException {
        //todo - could send more than one

        if(!pl.targetIsReadyForAnotherMessage(target)){
            return;
        }

        Optional<Integer> broadcastOpt = getQWithLowestMessageNoForTarget(target);
        if(broadcastOpt.isEmpty()){
            return;
        }

        int broadcastNo = broadcastOpt.get();
        int messageIndex = messageNoForBroadcastAndTarget[broadcastNo][target];

        Optional<URBMessage> nextMessageOpt = messageQueuePerBroadcast[broadcastNo].get(messageIndex);

        if(nextMessageOpt.isEmpty()){
            return;
        }

        URBMessage nextMessage = nextMessageOpt.get();

        pl.sendMessage(nextMessage,selfNo,target);

        messageNoForBroadcastAndTarget[broadcastNo][target] += 1;


    }
}
