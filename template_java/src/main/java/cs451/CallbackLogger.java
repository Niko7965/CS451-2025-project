package cs451;

import cs451.PerfectLinks.PLMessageRegular;

import java.io.IOException;

public class CallbackLogger implements OnDeliverCallBack {

    OutputWriter outputWriter;

    @Override
    public void onDeliver(PLMessageRegular m) {
        System.out.println("d "+m.sender+" "+m.payload);
        try {
            outputWriter.write("d "+m.sender+" "+m.payload +"\n");
        } catch (IOException ignored) {
            System.out.println("PROBLEM WITH LOGGING :(");
        }
    }

    @Override
    public void onShouldAck(PLMessageRegular m) {
    }

    public CallbackLogger(OutputWriter w){
        this.outputWriter = w;
    }
}
