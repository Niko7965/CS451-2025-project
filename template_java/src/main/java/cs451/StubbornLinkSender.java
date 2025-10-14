package cs451;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StubbornLinkSender extends Thread{

    List<DatagramPacket> toRepeat;
    DatagramSocket socket;
    int id;



    public StubbornLinkSender(int selfId) throws SocketException {
        socket = new DatagramSocket();
        toRepeat = Collections.synchronizedList(new ArrayList<>());
        this.id = selfId;
    }

    public void send(String message, Host target) throws IOException {
        DatagramPacket packet = makePacket(message, target);
        toRepeat.add(packet);
        sendBySocket(packet);
    }

    private synchronized void sendBySocket(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }

    public void repeat() throws IOException, InterruptedException {
        //hate this todo
        List<DatagramPacket> currentMessagesToRepeat = new ArrayList<>();

        while(true){
            Thread.sleep(1000);
            synchronized (toRepeat) {
                currentMessagesToRepeat = new ArrayList<>(toRepeat);
            }
            for(DatagramPacket p: currentMessagesToRepeat){
                sendBySocket(p);
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
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }








}
