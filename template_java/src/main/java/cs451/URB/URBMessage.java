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
}
