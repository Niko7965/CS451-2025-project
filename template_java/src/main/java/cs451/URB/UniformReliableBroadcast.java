package cs451.URB;

import cs451.GlobalCfg;
import cs451.Host;
import cs451.PerfectLinks.PLCallback;
import cs451.OutputWriter;
import cs451.PerfectLinks.PLMessageRegular;
import cs451.PerfectLinks.PerfectLink;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class UniformReliableBroadcast extends Thread implements PLCallback {

    private final ForwardMessages forwardMessages;
    private final Acknowledgements acknowledgements;
    private final Object messageNoLock;
    private int messageNo;
    private final PerfectLink pl;
    int noOfHosts;
    int selfId;
    OutputWriter outputWriter;
    boolean alive;
    URBCallback callBack;

    public UniformReliableBroadcast(Host selfHost, int noOfHosts, OutputWriter outputWriter, URBCallback callBack) throws SocketException, UnknownHostException {
        this.selfId = selfHost.getId();
        forwardMessages = new ForwardMessages(noOfHosts,selfId);
        acknowledgements = new Acknowledgements(noOfHosts);
        messageNoLock = new Object();
        messageNo = 0;
        pl = new PerfectLink(selfHost,this,outputWriter);

        this.noOfHosts = noOfHosts;
        this.callBack = callBack;
        this.outputWriter = outputWriter;
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
            synchronized (forwardMessages) {
                //For each target update their pl queues:
                for (int i = 0; i < noOfHosts; i++) {
                    if(i == selfId-1){
                        continue;
                    }
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


    public void broadcastInt(int m) throws IOException {
        broadcast(m,selfId);
    }

    public void broadcast(Object payload, int sender) throws IOException {
        URBMessage urbPayload;
        synchronized (messageNoLock) {
            //todo
            //lock if size of queue gets too large
            urbPayload = new URBMessage(payload, sender,messageNo);
            messageNo += 1;

        }
        synchronized (forwardMessages){

            forwardMessages.add(urbPayload);
            if(GlobalCfg.URB_ACK_DEBUG){
                System.out.println("dbg b: "+payload+" "+sender);
            }

            System.out.println("b "+payload);
            outputWriter.write("b "+payload +"\n");
        }

    }

    @Override
    public void onDeliver(PLMessageRegular m) {
        if(GlobalCfg.URB_PL_DEL_DEBUG){
            System.out.println("pldel: "+m.getPayload()+" "+m.getMetadata().getSenderId());
        }


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

        if(GlobalCfg.URB_DEADLOCK_BUG_DEBUG){
            System.out.println("Awaiting forward messages lock");
        }

        synchronized (forwardMessages) {
            //note, assumes perfect order of inputs
            forwardMessages.add(receivedMessage);
        }

        if(GlobalCfg.URB_DEADLOCK_BUG_DEBUG) {
            System.out.println("Finished deliver");
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
