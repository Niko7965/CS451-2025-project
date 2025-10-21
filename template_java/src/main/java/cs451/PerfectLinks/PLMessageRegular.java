package cs451.PerfectLinks;


public class PLMessageRegular extends PLMessage {
    public int sender;
    public String payload;
    public int receiver;


    public PLMessageRegular(int sender, String payload, int receiver){
        this.sender = sender;
        this.payload = payload;
        this.receiver = receiver;
    }

    public PLMessageRegular(String[] contents){
        this.sender = Integer.parseInt(contents[1]);
        this.receiver = Integer.parseInt(contents[2]);
        this.payload = contents[3];
    }


    /*
    Sends an ack that the receiver of this message, has recieved this message
    From the sender of this message
     */
    public PLAckMessage simpleAck(){
        return new PLAckMessage(this,this.sender,this.receiver);
    }

    public boolean equals(PLMessageRegular other){
        return other.sender == this.sender && this.receiver == other.receiver && other.payload.equals(this.payload);
    }

    public String toString(){
        return sender+ " "+ payload +" "+ receiver;
    }


    @Override
    public boolean isAck() {
        return false;
    }
}
