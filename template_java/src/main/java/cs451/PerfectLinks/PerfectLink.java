package cs451.PerfectLinks;

import cs451.*;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;


//Each perfect link spawns two threads, one for the listener and sender of stubborn links,
//as each needs to loop forever
//Therefore, we should strive not to create multiple perfect links
public class PerfectLink implements OnDeliverCallBack, AckCallBack {

    OnDeliverCallBack callBack;
    StubbornLinkListener stubbornLinkListener;
    StubbornLinkSender stubbornLinkSender;
    ArrayList<PLMessageRegular> delivered; //todo replace with hashset
    OutputWriter outputWriter;

    boolean paused;

    public PerfectLink(Host selfHost,OnDeliverCallBack callBack, OutputWriter w) throws SocketException, UnknownHostException {
        this.stubbornLinkListener = new StubbornLinkListener(selfHost,this,this);
        this.stubbornLinkListener.start();
        this.callBack = callBack;
        this.stubbornLinkSender = new StubbornLinkSender(selfHost.getId());
        this.stubbornLinkSender.start();
        this.delivered = new ArrayList<>();
        this.outputWriter = w;
        paused = false;
    }

    public void sendMessage(PLMessageRegular m) throws IOException {
        System.out.println("b "+m.payload);
        outputWriter.write("b "+m.payload +"\n");
        stubbornLinkSender.sendMessage(m);
    }


    @Override
    public void onDeliver(PLMessageRegular m) {
        if(!delivered.contains(m)){
            delivered.add(m);
            stubbornLinkSender.sendAck(m.simpleAck());
            callBack.onDeliver(m);
        }
    }

    @Override
    public void onAcknowledgement(PLAckMessage m) {
        stubbornLinkSender.receiveAck(m);
    }
}
