package cs451.PerfectLinks;

import cs451.*;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;


//Each perfect link spawns two threads, one for the listener and sender of stubborn links,
//as each needs to loop forever
//Therefore, we should strive not to create multiple perfect links
public class PerfectLink implements OnDeliverCallBack, AckCallBack {

    OnDeliverCallBack callBack;
    StubbornLinkListener stubbornLinkListener;
    StubbornLinkSender stubbornLinkSender;
    OutputWriter outputWriter;

    boolean paused;

    public PerfectLink(Host selfHost,OnDeliverCallBack callBack, OutputWriter w) throws SocketException, UnknownHostException {
        this.stubbornLinkListener = new StubbornLinkListener(selfHost,this,this);
        this.stubbornLinkListener.start();
        this.callBack = callBack;
        this.stubbornLinkSender = new StubbornLinkSender(selfHost.getId());
        this.stubbornLinkSender.start();
        this.outputWriter = w;
        paused = false;
    }

    public void sendIntMessage(int payload, int sender, int receiver) throws IOException, InterruptedException {
        sendMessage(payload,sender,receiver);
        System.out.println("b "+payload);
        outputWriter.write("b "+payload +"\n");
    }

    public void sendMessage(Object payload, int sender, int receiver) throws  InterruptedException {
        int messageNo = stubbornLinkSender.getNextMessageNoForTarget(receiver);
        PLMessageRegular message = new PLMessageRegular(sender,receiver,messageNo,payload);
        stubbornLinkSender.sendMessage(message);
    }

    public void kill(){
        stubbornLinkSender.kill();
        stubbornLinkListener.kill();
    }

    //Is called every time a new unique message is received
    @Override
    public void onDeliver(PLMessageRegular m) {
        callBack.onDeliver(m);
    }

    //Is called every time a message, which has been delivered, is received
    @Override
    public void onShouldAck(PLMessageRegular m) {
        stubbornLinkSender.sendAck(m.simpleAck());

    }

    @Override
    public void onAcknowledgement(PLAckMessage m) {stubbornLinkSender.receiveAck(m);
    }
}
