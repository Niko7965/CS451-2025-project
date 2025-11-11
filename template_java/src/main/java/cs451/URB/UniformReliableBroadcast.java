package cs451.URB;

import cs451.Host;
import cs451.PerfectLinks.PLCallback;
import cs451.OutputWriter;
import cs451.PerfectLinks.PLMessageRegular;
import cs451.PerfectLinks.PerfectLink;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class UniformReliableBroadcast extends Thread implements PLCallback {

    private final ForwardMessages forwardMessages;
    private final Acknowledgements acknowledgements;
    private final Object messageNoLock;
    private int messageNo;
    private final PerfectLink pl;
    int noOfTargets;
    int selfId;
    URBCallback callBack;

    public UniformReliableBroadcast(Host selfHost, int noOfTargets, OutputWriter outputWriter, URBCallback callBack) throws SocketException, UnknownHostException {
        this.selfId = selfHost.getId();
        forwardMessages = new ForwardMessages(noOfTargets,selfId);
        acknowledgements = new Acknowledgements(noOfTargets);
        messageNoLock = new Object();
        messageNo = Integer.MIN_VALUE;
        pl = new PerfectLink(selfHost,this,outputWriter);
        this.noOfTargets = noOfTargets;
        this.callBack = callBack;

    }


    public void run(){
        try {
            repeat();
        } catch (InterruptedException e) {
            System.out.println("URB loop crashed :(");
            throw new RuntimeException(e);
        }
    }

    public void repeat() throws InterruptedException {

        while(true) {

            synchronized (forwardMessages) {
                //For each target update their pl queues:
                for (int i = 0; i < noOfTargets; i++) {
                    forwardMessages.updatePlQueueOfTarget(pl, i);
                }
            }


            synchronized (acknowledgements){
                //Check if for any message in forward, that we have enough acks to deliver
                ArrayList<URBMessage> deliverables = acknowledgements.getDeliverableMessages();
                for (URBMessage payload : deliverables) {
                    callBack.onDeliver(payload);
                }


                synchronized (forwardMessages){
                    ArrayList<URBMessage> fullyAckedMessages = acknowledgements.getFullyAckedMessages();
                    forwardMessages.removeMessages(fullyAckedMessages);
                    acknowledgements.deleteMessages(fullyAckedMessages);
                }
            }


        }

    }


    public void broadcastInt(int m){

    }

    public void broadcast(Object payload, int sender){
        URBMessage urbPayload;
        synchronized (messageNoLock) {
            //todo
            //lock if size of queue gets too large
            urbPayload = new URBMessage(payload, sender,messageNo);
            messageNo += 1;

        }
        synchronized (forwardMessages){
            forwardMessages.add(urbPayload);
        }

    }

    @Override
    public void onDeliver(PLMessageRegular m) {
        //PL Has delivered message m
        URBMessage receivedMessage = (URBMessage) m.getPayload();
        int sender = m.getMetadata().getSenderId();

        synchronized (acknowledgements) {

            acknowledgements.addAck(receivedMessage, selfId);
            acknowledgements.addAck(receivedMessage, sender);
        }

        synchronized (forwardMessages) {
            //note, assumes perfect order of inputs
            forwardMessages.add(receivedMessage);
        }



    }



    @Override
    public void onShouldAck(PLMessageRegular m) {
        //Will never call
    }
}
