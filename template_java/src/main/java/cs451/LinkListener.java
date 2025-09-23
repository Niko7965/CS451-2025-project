package cs451;

import java.io.IOException;
import java.net.*;

public class LinkListener extends Thread {

    DatagramSocket socket;
    InetAddress address;
    int port;

    public LinkListener(Host host) throws SocketException, UnknownHostException {
        port = host.getPort();
        socket = new DatagramSocket(port);
        address = InetAddress.getByName(host.getIp());
    }

    public void receive() throws IOException {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
        while(true){
            socket.receive(packet);
            String message = new String(packet.getData(),0,packet.getLength());
            System.out.println("Received: "+message);
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
