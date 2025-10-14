package cs451;

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
            Message m = messageFromPacket(packet);
            if(m.isAck){
                ackCallBack.onAcknowledgement(m);
                return;
            }
            deliverCallBack.onDeliver(m);
        }
    }

    public Message messageFromPacket(DatagramPacket packet){
        String packetString = new String(packet.getData(),0,packet.getLength());
        String[] contents = packetString.split(" ");
        String type = contents[0];
        boolean isAck = type.equals("ACK");
        int sender = Integer.parseInt(contents[1]);
        int receiver = Integer.parseInt(contents[2]);
        String content = contents[3];

        return new Message(sender,content,receiver,isAck);
    }

    public void run(){
        try {
            receive();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
