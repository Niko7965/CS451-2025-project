package cs451;

//Made with guide to UDP in java:
//https://www.baeldung.com/udp-in-java

import java.io.IOException;
import java.net.*;

public class ShittyLink extends Thread {

    DatagramSocket socket;
    InetAddress address;
    int id;
    int port;

    public ShittyLink(Host host) throws SocketException, UnknownHostException {
        port = host.getPort();
        socket = new DatagramSocket(port);
        address = InetAddress.getByName(host.getIp());
        id = host.getId();
    }

    public void run(){
        try {
            sendAndReceive();
        } catch (IOException e) {
            System.out.println(":(");
            throw new RuntimeException(e);
        }
    }

    public void sendAndReceive() throws IOException {
        while (true) {
            String message = "hello";
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length,address, port);
            socket.send(packet);
            System.out.println("Sent "+message+" to "+ id);

            socket.receive(packet);
            String received = new String(packet.getData(),packet.getOffset(), packet.getLength());
            System.out.println("Received: "+received +"From: "+id);

        }
    }
}