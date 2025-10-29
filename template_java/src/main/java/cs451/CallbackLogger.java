package cs451;

import cs451.PerfectLinks.PLMessageRegular;

import java.io.IOException;

public class CallbackLogger implements OnDeliverCallBack {

    OutputWriter outputWriter;

    @Override
    public void onDeliver(PLMessageRegular m) {
        String payloadString = payloadToString(m.getPayload());

        System.out.println("d "+m.getMetadata().getSenderId()+" "+payloadString);
        try {
            outputWriter.write("d "+m.getMetadata().getSenderId()+" "+payloadString +"\n");
        } catch (IOException ignored) {
            System.out.println("PROBLEM WITH LOGGING :(");
        }
    }

    private String payloadToString(Object payload){
        int payloadInt = (Integer) payload;
        return ""+payloadInt;
    }



    @Override
    public void onShouldAck(PLMessageRegular m) {
    }

    public CallbackLogger(OutputWriter w){
        this.outputWriter = w;
    }
}
