package cs451.PerfectLinks;

import cs451.Host;
import cs451.Phonebook;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class StubbornLinkSender extends Thread{

    final List<PLAckMessage> toAck;
    final List<PLAckMessage> messagedThatHaveBeenAckedByOther;
    final HashMap<Integer,TargetQueue> toSend;
    final DatagramSocket socket;
    int id;
    private final Object killLock;
    boolean killed;



    public StubbornLinkSender(int selfId) throws SocketException {
        socket = new DatagramSocket();
        toAck = Collections.synchronizedList(new ArrayList<>());
        messagedThatHaveBeenAckedByOther = Collections.synchronizedList(new ArrayList<>());
        this.toSend = new HashMap<>();
        this.id = selfId;
        this.killed = false;
        this.killLock = new Object();
    }

    public void sendMessage(PLMessageRegular message) {
        synchronized (toSend){
            if(!toSend.containsKey(message.receiver)){
                toSend.put(message.receiver,new TargetQueue());
            }
            toSend.get(message.receiver).enqueueMessage(message);
        }
    }


    public void sendAck(PLAckMessage m){
        synchronized (toAck){
            toAck.add(m);
        }
    }

    public void receiveAck(PLAckMessage ackedMessage){
        synchronized (messagedThatHaveBeenAckedByOther){
            messagedThatHaveBeenAckedByOther.add(ackedMessage);
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

            //noinspection BusyWait
            Thread.sleep(100); //todo, should this delay be here?


            //Send messages that have not yet been acked
            synchronized (toSend) {

                for(TargetQueue t : toSend.values()){
                    Optional<PLMessageRegular> mOpt = t.getCurrent();
                    if(mOpt.isEmpty()){
                        continue;
                    }
                    PLMessageRegular m = mOpt.get();
                    int messageNo = t.getMessageNo();
                    m.setMessageNo(messageNo);
                    DatagramPacket p = makePacketForReg(m);
                    synchronized (socket) {
                        socket.send(p);
                    }
                }


            }

            //Send acks for messages received
            synchronized (toAck){
                for (PLAckMessage m : toAck){
                    DatagramPacket p = makePacketForAck(m);
                    synchronized (socket){
                        socket.send(p);
                    }
                }
                toAck.clear();
            }


            //Remove acked messages from "sending" list
            synchronized (messagedThatHaveBeenAckedByOther){
                synchronized (toSend){
                    for(PLAckMessage m: messagedThatHaveBeenAckedByOther){
                        int target = m.hostThatAcks;
                        if(!toSend.containsKey(target)){
                            System.out.println("ERROR - ACK FROM UNKOWN HOST");
                            return;
                        }
                        toSend.get(target).tryAck(m);
                    }
                }
            }



        }
    }
    private DatagramPacket makePacketForAck(PLAckMessage ackMessage) throws UnknownHostException {
        Host target = Phonebook.hostFromId(ackMessage.hostToAck);
        String toSend = ackMessage.toString();
        byte[] buffer = toSend.getBytes();
        InetAddress address = InetAddress.getByName(target.getIp());
        return new DatagramPacket(buffer, 0, buffer.length, address, target.getPort());
    }

    private DatagramPacket makePacketForReg(PLMessageRegular message) throws UnknownHostException {
        Host target = Phonebook.hostFromId(message.receiver);
        String toSend = message.toString();
        byte[] buffer = toSend.getBytes();
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
