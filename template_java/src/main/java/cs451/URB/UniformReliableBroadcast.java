package cs451.URB;

import cs451.GlobalCfg;
import cs451.Host;
import cs451.PerfectLinks.PLCallback;
import cs451.OutputWriter;
import cs451.PerfectLinks.PLMessageRegular;
import cs451.PerfectLinks.PerfectLink;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class UniformReliableBroadcast extends Thread implements PLCallback {

    Object canSendLock;
    private final ForwardMessages forwardMessages;
    private final Acknowledgements acknowledgements;
    private final Object messageNoLock;
    private int messageNo;
    private final PerfectLink pl;
    int noOfHosts;
    int selfId;
    boolean alive;
    URBCallback callBack;

    public UniformReliableBroadcast(Host selfHost, int noOfHosts, OutputWriter outputWriter, URBCallback callBack) throws SocketException, UnknownHostException {
        this.selfId = selfHost.getId();
        forwardMessages = new ForwardMessages(noOfHosts,selfId);
        acknowledgements = new Acknowledgements(noOfHosts);
        messageNoLock = new Object();
        messageNo = Integer.MIN_VALUE;
        pl = new PerfectLink(selfHost,this,outputWriter);
        this.noOfHosts = noOfHosts;
        this.callBack = callBack;
        alive = true;

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

        while(alive) {

            System.out.println("doing repeat");

            synchronized (forwardMessages) {
                System.out.println("doing forward");
                //For each target update their pl queues:
                for (int i = 0; i < noOfHosts; i++) {
                    if(i == selfId){
                        continue;
                    }
                    forwardMessages.updatePlQueueOfTarget(pl, i);
                }
            }



            synchronized (acknowledgements){
                System.out.println("doing acks");
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
        broadcast(m,selfId);
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
            while(forwardMessages.getNoOfMessagesInQueue(sender-1) > forwardMessages.maxQueueSize()){
                //todo wait here
            }
            forwardMessages.add(urbPayload);
            if(GlobalCfg.URB_ACK_DEBUG){
                System.out.println("dbg b: "+payload+" "+sender);
            }
        }

    }

    @Override
    public void onDeliver(PLMessageRegular m) {
        //PL Has delivered message m
        URBMessage receivedMessage = (URBMessage) m.getPayload();
        int sender = m.getMetadata().getSenderId();

        synchronized (acknowledgements) {
            if(GlobalCfg.URB_ACK_DEBUG){
                System.out.println("a: "+sender+" "+m.getPayload());
            }
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

    public void kill() {
        this.pl.kill();
        this.alive = false;
    }
}
