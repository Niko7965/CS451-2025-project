package cs451.PerfectLinks;

import cs451.GlobalCfg;
import cs451.Host;
import cs451.Phonebook;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class StubbornLinkSender extends Thread{

    final List<PLAckMessage> toAck;
    final List<PLAckMessage> messagesThatHaveBeenAckedByOther;
    final HashMap<Integer,TargetQueue> toSend;
    final DatagramSocket socket;
    int id;
    private final Object killLock;
    boolean killed;



    public StubbornLinkSender(int selfId) throws SocketException {
        socket = new DatagramSocket();
        toAck = Collections.synchronizedList(new ArrayList<>());
        messagesThatHaveBeenAckedByOther = Collections.synchronizedList(new ArrayList<>());
        this.toSend = new HashMap<>();
        this.id = selfId;
        this.killed = false;
        this.killLock = new Object();
    }

    public int getNextMessageNoForTarget(int target){
        synchronized (toSend) {
            if (!toSend.containsKey(target)) {
                toSend.put(target, new TargetQueue());
            }
            TargetQueue q = toSend.get(target);
            return q.getNextMessageNo();
        }

    }

    public boolean targetIsReadyForMoreMessages(int target){
        Object canSendLock;

        synchronized (toSend){
            if(!toSend.containsKey(target)){
                toSend.put(target,new TargetQueue());
            }
            TargetQueue tq = toSend.get(target);
            return tq.canPush;
        }

    }

    public void sendMessage(PLMessageRegular message) throws InterruptedException {
        Object canSendLock;

        synchronized (toSend){
            if(!toSend.containsKey(message.getMetadata().getReceiverId())){
                toSend.put(message.getMetadata().getReceiverId(),new TargetQueue());
            }
            TargetQueue tq = toSend.get(message.getMetadata().getReceiverId());
            canSendLock = tq.getQueueLock();
        }

        synchronized (canSendLock){
            toSend.get(message.getMetadata().getReceiverId()).enqueueMessage(message);
        }

    }


    public void sendAck(PLAckMessage m){
        synchronized (toAck){
            toAck.add(m);
        }
    }

    public void receiveAck(PLAckMessage ackedMessage){
        synchronized (messagesThatHaveBeenAckedByOther) {
            if (GlobalCfg.PL_ACK_DEBUG){
                System.out.println("added ack to list of acks");
            }
            messagesThatHaveBeenAckedByOther.add(ackedMessage);
        }
    }


    public void kill(){
        this.killed = true;
    }


    public void repeat() throws IOException, InterruptedException {
        while(true){
            synchronized (killLock){
                if(killed){
                    return;
                }
            }


            Thread.sleep(10);


            //Send messages that have not yet been acked
            synchronized (toSend) {

                for(TargetQueue t : toSend.values()){
                    Optional<PLMessageRegular> mOpt = t.getCurrent();
                    if(mOpt.isEmpty()){
                        continue;
                    }
                    PLMessageRegular m = mOpt.get();

                    DatagramPacket p = makePacketForReg(m);
                    synchronized (socket) {
                        if(GlobalCfg.STUBBORN_SEND_DEBUG) {
                            System.out.println("Stubborn sending " + m);
                        }
                        socket.send(p);
                    }
                }


            }

            //Send acks for messages received
            synchronized (toAck){
                for (PLAckMessage m : toAck){
                    if(GlobalCfg.PL_ACK_DEBUG) {
                        System.out.println("Acking: " + m.getMetadata().getMessageNo() +" from: "+m.getMetadata().getSenderId());
                    }
                    DatagramPacket p = makePacketForAck(m);
                    synchronized (socket){
                        socket.send(p);
                    }
                }
                toAck.clear();
            }


            //Handle received acks
            synchronized (messagesThatHaveBeenAckedByOther){
                synchronized (toSend){
                    for(PLAckMessage m: messagesThatHaveBeenAckedByOther){
                        int target = m.getMetadataForAckedMessage().getReceiverId();
                        if(!toSend.containsKey(target)){
                            System.out.println("ERROR - ACK FROM UNKNOWN HOST");
                            return;
                        }
                        toSend.get(target).tryAck(m);
                    }
                }
            }



        }
    }




    private DatagramPacket makePacketForAck(PLAckMessage ackMessage) throws IOException {
        Host target = Phonebook.hostFromId(ackMessage.getMetadataForAckedMessage().getSenderId());
        byte[] buffer = ackMessage.toBytes();
        InetAddress address = InetAddress.getByName(target.getIp());
        return new DatagramPacket(buffer, 0, buffer.length, address, target.getPort());
    }

    private DatagramPacket makePacketForReg(PLMessageRegular message) throws IOException {
        Host target = Phonebook.hostFromId(message.getMetadata().getReceiverId());
        byte[] buffer = message.toBytes();
        InetAddress address = InetAddress.getByName(target.getIp());
        return new DatagramPacket(buffer, 0, buffer.length, address, target.getPort());
    }

    public void run(){
        try {
            repeat();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }








}
