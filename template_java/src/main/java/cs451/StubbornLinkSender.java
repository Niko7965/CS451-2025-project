package cs451;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class StubbornLinkSender extends Thread{

    ArrayList<DatagramPacket> toRepeat;
    DatagramSocket socket;
    int id;

    public StubbornLinkSender(int selfId) throws SocketException {
        socket = new DatagramSocket();
        this.id = selfId;
    }

    public void send(String message, Host target) throws IOException {
        DatagramPacket packet = makePacket(message,target);
        toRepeat.add(packet);
        socket.send(packet);
    }

    public void repeat() throws IOException {
        //hate this todo
        while(true){
            for(DatagramPacket p: toRepeat){
                socket.send(p);
            }
        }
    }

    private DatagramPacket makePacket(String message, Host target) throws UnknownHostException {
        String toSend = this.id+" "+target.getId()+" "+message;
        byte[] buffer = toSend.getBytes();
        InetAddress address = InetAddress.getByName(target.getIp());
        return new DatagramPacket(buffer, 0, buffer.length, address, target.getPort());
    }

    public void run(){
        try {
            repeat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }








}
