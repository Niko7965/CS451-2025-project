package cs451;

import java.io.IOException;
import java.net.*;

public class StubbornLinkListener extends Thread {

    OnDeliverCallBack callBack;
    DatagramSocket socket;
    InetAddress address;
    int port;

    public StubbornLinkListener(Host host,OnDeliverCallBack callBack) throws SocketException, UnknownHostException {
        port = host.getPort();
        socket = new DatagramSocket(port);
        address = InetAddress.getByName(host.getIp());
        this.callBack = callBack;
    }

    public void receive() throws IOException {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
        while(true){
            socket.receive(packet);
            String content = new String(packet.getData(),0,packet.getLength());

            System.out.println("Received: "+content);
            Message m = messageFromPacket(packet);
            callBack.onDeliver(m);
        }
    }

    public Message messageFromPacket(DatagramPacket packet){
        String packetString = new String(packet.getData(),0,packet.getLength());
        String[] contents = packetString.split(" ");
        int sender = Integer.parseInt(contents[0]);
        int receiver = Integer.parseInt(contents[1]);
        String content = contents[2];

        return new Message(sender,content);
    }

    public void run(){
        try {
            receive();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
