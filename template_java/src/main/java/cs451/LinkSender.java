package cs451;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class LinkSender extends Thread{
    ArrayList<Host> targets;
    DatagramSocket socket;
    int id;

    public LinkSender(ArrayList<Host> targets,int id) throws SocketException {
        this.targets = targets;
        socket = new DatagramSocket();
        this.id = id;
    }

    public void heartbeat() throws IOException, InterruptedException {
        while (true) {
            String message = "" + id;
            byte[] buffer = message.getBytes();
            for (Host target : targets) {
                InetAddress address = InetAddress.getByName(target.getIp());
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length, address, target.getPort());
                socket.send(packet);
            }

            Thread.sleep(1000*3);
        }
    }

    public void run(){

        try {
            heartbeat();
        } catch (Exception e) {
            System.out.println("Heartbeat failure :(");
        }
    }


}
