package cs451.PerfectLinks;

import cs451.Host;
import cs451.OnDeliverCallBack;

import java.io.IOException;
import java.net.*;

public class StubbornLinkListener extends Thread {

    DeliveredMessageTracker deliveredMessageTracker;
    OnDeliverCallBack deliverCallBack;
    AckCallBack ackCallBack;
    DatagramSocket socket;
    InetAddress address;
    int port;
    boolean killed;
    final Object killLock;

    public StubbornLinkListener(Host host, OnDeliverCallBack deliverCallBack, AckCallBack ackCallBack) throws SocketException, UnknownHostException {
        deliveredMessageTracker = new DeliveredMessageTracker();
        port = host.getPort();
        socket = new DatagramSocket(port);
        address = InetAddress.getByName(host.getIp());
        this.deliverCallBack = deliverCallBack;
        this.ackCallBack = ackCallBack;
        this.killed = false;
        this.killLock = new Object();
    }

    public void kill(){
        synchronized (killLock){
            this.killed = true;
        }
    }

    public void receive() throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[6400];
        DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
        while(true){
            synchronized (killLock){
                if (killed){
                    return;
                }
            }
            socket.receive(packet);


            PLMessage message = PLMessage.fromBytes(packet.getData());


            if(message.isAck()){
                System.out.println("Heard ack for: "+message.getMetadata().getMessageNo());
                ackCallBack.onAcknowledgement((PLAckMessage) message);
                continue;
            }

            PLMessageRegular regularMessage = (PLMessageRegular) message;
            if(deliveredMessageTracker.shouldDeliver(regularMessage)){
                deliverCallBack.onDeliver(regularMessage);
            }
            if(deliveredMessageTracker.hasReceived(regularMessage)){
                deliverCallBack.onShouldAck(regularMessage);
            }


        }
    }




    public void run(){
        try {
            receive();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
