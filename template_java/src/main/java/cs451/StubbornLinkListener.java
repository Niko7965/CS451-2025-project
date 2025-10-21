package cs451;

import cs451.PerfectLinks.AckCallBack;
import cs451.PerfectLinks.PLAckMessage;
import cs451.PerfectLinks.PLMessage;
import cs451.PerfectLinks.PLMessageRegular;

import java.io.IOException;
import java.net.*;

public class StubbornLinkListener extends Thread {

    OnDeliverCallBack deliverCallBack;
    AckCallBack ackCallBack;
    DatagramSocket socket;
    InetAddress address;
    int port;

    public StubbornLinkListener(Host host,OnDeliverCallBack deliverCallBack, AckCallBack ackCallBack) throws SocketException, UnknownHostException {
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
            PLMessage message = PLMessage.fromString(packetString);


            if(message.isAck()){
                ackCallBack.onAcknowledgement((PLAckMessage) message);

                return;
            }
            deliverCallBack.onDeliver((PLMessageRegular) message);
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
