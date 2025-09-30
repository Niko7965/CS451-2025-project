package cs451;

import java.net.DatagramPacket;

public class Message {
    int sender;
    String content;

    public Message(int sender, String content){
        this.sender = sender;
        this.content = content;
    }

    public boolean equals(Message other){
        return other.sender == this.sender && other.content.equals(this.content);
    }
}
