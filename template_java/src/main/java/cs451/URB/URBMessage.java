package cs451.URB;

import java.io.Serializable;

public class URBMessage implements Serializable {
    Object payload;
    int originalUrbSender;
    int urbMessageNo;

    public URBMessage(Object payload, int originalUrbSender, int urbMessageNo){
        this.payload = payload;
        this.originalUrbSender = originalUrbSender;
        this.urbMessageNo = urbMessageNo;
    }

    @Override
    public String toString() {
        return "URB: "+"Sender: "+originalUrbSender+" MessageNo: "+urbMessageNo;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof URBMessage)){
            return false;
        }
        URBMessage otherMessage = (URBMessage) o;
        return this.originalUrbSender == otherMessage.originalUrbSender && this.urbMessageNo == otherMessage.urbMessageNo;
    }

    @Override
    public int hashCode(){
        return Integer.hashCode(originalUrbSender) ^ Integer.hashCode(urbMessageNo);
    }
}
