package cs451.URB;

import cs451.OutputWriter;


import java.io.IOException;

public class URBCallbackLogger implements URBCallback {

    OutputWriter outputWriter;

    @Override
    public void onDeliver(URBMessage m) {

        String payloadString = payloadToString(m.payload);

        System.out.println("d "+m.originalUrbSender+" "+payloadString);
        try {
            outputWriter.write("d "+m.originalUrbSender+" "+payloadString +"\n");
        } catch (IOException ignored) {
            System.out.println("PROBLEM WITH LOGGING :(");
        }
    }

    private String payloadToString(Object payload){
        int payloadInt = (Integer) payload;
        return ""+payloadInt;
    }


    public URBCallbackLogger(OutputWriter w){
        this.outputWriter = w;
    }
}
