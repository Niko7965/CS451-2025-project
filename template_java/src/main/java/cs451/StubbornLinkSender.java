package cs451;

import cs451.PerfectLinks.PLAckMessage;
import cs451.PerfectLinks.PLMessageRegular;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StubbornLinkSender extends Thread{

    final List<PLAckMessage> toAck;
    final List<PLAckMessage> messagedThatHaveBeenAckedByOther;
    final List<PLMessageRegular> toRepeat;
    final DatagramSocket socket;
    int id;



    public StubbornLinkSender(int selfId) throws SocketException {
        socket = new DatagramSocket();
        toRepeat = Collections.synchronizedList(new ArrayList<>());
        toAck = Collections.synchronizedList(new ArrayList<>());
        messagedThatHaveBeenAckedByOther = Collections.synchronizedList(new ArrayList<>());
        this.id = selfId;
    }

    public void sendMessage(PLMessageRegular message) {
        synchronized (toRepeat){
            toRepeat.add(message);
        }
    }



    //todo
    /*
    There is something here about later
    we might have that p1 sends to p2 a message.
    But the message is that p3 sends to p4
    In other words I have some clutter around what a sender and receiver is
     */

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



    public void repeat() throws IOException, InterruptedException {
        while(true){
            Thread.sleep(1000); //todo, should this delay be here?


            //Send messages that have not yet been acked
            synchronized (toRepeat) {
                for (PLMessageRegular m : toRepeat) {
                    DatagramPacket p = makePacket(m);
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
                        System.out.println("Sent ack for "+ m.message.payload);
                    }
                }
                toAck.clear();
            }


            //Remove acked messages from "sending" list
            synchronized (messagedThatHaveBeenAckedByOther){
                synchronized (toRepeat){
                    for(PLAckMessage m: messagedThatHaveBeenAckedByOther){
                        toRepeat.remove(m.message);
                        System.out.println("Removed "+ m.message.payload);
                    }
                }
            }



        }
    }
    private DatagramPacket makePacketForAck(PLAckMessage ackMessage) throws UnknownHostException {
        Host target = Phonebook.hostFromId(ackMessage.hostToAck);
        //ACK + ack-sender + ack-receiver + message sender + message content + message receiver
        String toSend = "ACK "+ackMessage.hostThatAcks + " "+ackMessage.hostToAck + " " + ackMessage.message.sender +" "+ackMessage.message.payload +" "+ackMessage.message.receiver;
        byte[] buffer = toSend.getBytes();
        InetAddress address = InetAddress.getByName(target.getIp());
        return new DatagramPacket(buffer, 0, buffer.length, address, target.getPort());
    }

    private DatagramPacket makePacket(PLMessageRegular message) throws UnknownHostException {
        Host target = Phonebook.hostFromId(message.receiver);
        String toSend = "SEND "+message.sender+" "+target.getId()+" "+message.payload;
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
