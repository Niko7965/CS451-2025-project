package cs451;


public class Message {
    int sender;
    public String content;
    int target;
    boolean isAck;


    public Message(int sender, String content, int target, boolean isAck){
        this.sender = sender;
        this.content = content;
        this.target = target;
        this.isAck = isAck;
    }

    //TODO NOTE - Currently matches even if one is ack, and other is not
    public boolean equals(Message other){
        return other.sender == this.sender && this.target == other.target && other.content.equals(this.content);
    }


}
