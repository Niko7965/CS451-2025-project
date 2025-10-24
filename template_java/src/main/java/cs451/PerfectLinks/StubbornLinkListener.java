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

    public StubbornLinkListener(Host host, OnDeliverCallBack deliverCallBack, AckCallBack ackCallBack) throws SocketException, UnknownHostException {
        deliveredMessageTracker = new DeliveredMessageTracker();
        port = host.getPort();
        socket = new DatagramSocket(port);
        address = InetAddress.getByName(host.getIp());
        this.deliverCallBack = deliverCallBack;
        this.ackCallBack = ackCallBack;
    }

    public void receive() throws IOException {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
        while(true){
            socket.receive(packet);

            String packetString = new String(packet.getData(),0,packet.getLength());
            System.out.println("Received package: "+packetString);

            PLMessage message = PLMessage.fromString(packetString);


            if(message.isAck()){
                ackCallBack.onAcknowledgement((PLAckMessage) message);
                return;
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
