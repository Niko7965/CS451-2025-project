package cs451.PerfectLinks;

import cs451.OutputWriter;

import java.io.IOException;

public class PLCallbackLogger implements PLCallback {

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

    public PLCallbackLogger(OutputWriter w){
        this.outputWriter = w;
    }
}
