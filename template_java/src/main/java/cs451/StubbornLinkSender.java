package cs451;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StubbornLinkSender extends Thread{

    final List<Message> toAck;
    final List<Message> messagedThatHaveBeenAckedByOther;
    final List<Message> toRepeat;
    final DatagramSocket socket;
    int id;



    public StubbornLinkSender(int selfId) throws SocketException {
        socket = new DatagramSocket();
        toRepeat = Collections.synchronizedList(new ArrayList<>());
        toAck = Collections.synchronizedList(new ArrayList<>());
        messagedThatHaveBeenAckedByOther = Collections.synchronizedList(new ArrayList<>());
        this.id = selfId;
    }

    public void sendMessage(Message message) {
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

    public void sendAck(Message m){
        synchronized (toAck){
            toAck.add(m);
        }
    }

    public void receiveAck(Message ackedMessage){
        synchronized (messagedThatHaveBeenAckedByOther){
            messagedThatHaveBeenAckedByOther.add(ackedMessage);
        }
    }



    public void repeat() throws IOException, InterruptedException {
        while(true){
            Thread.sleep(1000); //todo, should this delay be here?

            synchronized (toRepeat) {
                for (Message m : toRepeat) {
                    DatagramPacket p = makePacket(m,false);
                    synchronized (socket) {
                        socket.send(p);
                    }
                }
            }
            synchronized (toAck){
                for (Message m : toAck){
                    DatagramPacket p = makePacket(m, true);
                    synchronized (socket){
                        socket.send(p);
                        System.out.println("Sent ack for "+ m.content);
                    }
                }
                toAck.clear();
            }

            synchronized (messagedThatHaveBeenAckedByOther){
                synchronized (toRepeat){
                    for(Message m: messagedThatHaveBeenAckedByOther){
                        toRepeat.remove(m);
                        System.out.println("Removed "+ m.content);
                    }
                }
            }



        }
    }

    private DatagramPacket makePacket(Message message, boolean isAck) throws UnknownHostException {
        Host target = Phonebook.hostFromId(message.target);
        String type = "SEND";
        if(isAck){
            type = "ACK";
        }
        String toSend = type+" "+this.id+" "+target.getId()+" "+message.content;
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
