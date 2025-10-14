package cs451;

import java.io.IOException;

public class CallbackLogger implements OnDeliverCallBack {

    OutputWriter outputWriter;

    @Override
    public void onDeliver(Message m) {
        System.out.println("d "+m.sender+" "+m.content);
        try {
            outputWriter.write("d "+m.sender+" "+m.content+"\n");
        } catch (IOException ignored) {

        }
    }

    public CallbackLogger(OutputWriter w){
        this.outputWriter = w;
    }
}
