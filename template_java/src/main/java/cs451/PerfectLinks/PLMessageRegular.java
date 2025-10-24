package cs451.PerfectLinks;


public class PLMessageRegular extends PLMessage {
    public int sender;
    public String payload;
    public int receiver;
    private int messageNo;


    public PLMessageRegular(int sender, String payload, int receiver){
        this.sender = sender;
        this.payload = payload;
        this.receiver = receiver;
    }

    public PLMessageRegular(String[] contents){
        this.messageNo = Integer.parseInt(contents[1]);
        this.sender = Integer.parseInt(contents[2]);
        this.receiver = Integer.parseInt(contents[3]);
        this.payload = contents[4];
    }

    public void setMessageNo(int messageNo){
        this.messageNo = messageNo;
    }

    public int getMessageNo(){
        return this.messageNo;
    }


    /*
    Sends an ack that the receiver of this message, has recieved this message
    From the sender of this message
     */
    public PLAckMessage simpleAck(){
        return new PLAckMessage(this,this.sender,this.receiver);
    }

    public boolean equals(PLMessageRegular other){
        return this.messageNo == other.messageNo && this.sender == other.sender && this.receiver == other.receiver && other.payload.equals(this.payload);
    }

    public String toString(){
        /*0*/   String s = "SEND ";
        /*1*/   s += " "+ this.getMessageNo();
        /*2*/   s += " "+this.sender;
        /*3*/   s += " "+this.receiver;
        /*4*/   s += " "+this.payload;
        return s;
    }


    @Override
    public boolean isAck() {
        return false;
    }
}
