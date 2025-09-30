package cs451.PerfectLinks;

import cs451.*;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;


//Each perfect link spawns two threads, one for the listener and sender of stubborn links,
//as each needs to loop forever
//Therefore, we should strive not to create multiple perfect links
public class PerfectLink implements OnDeliverCallBack {

    OnDeliverCallBack callBack;
    StubbornLinkListener stubbornLinkListener;
    StubbornLinkSender stubbornLinkSender;
    ArrayList<Message> delivered; //todo replace with hashset


    public PerfectLink(Host selfHost,OnDeliverCallBack callBack) throws SocketException, UnknownHostException {
        this.stubbornLinkListener = new StubbornLinkListener(selfHost,this);
        this.stubbornLinkListener.start();
        this.callBack = callBack;
        this.stubbornLinkSender = new StubbornLinkSender(selfHost.getId());
    }

    public void sendMessage(String content, Host target) throws IOException {
        System.out.println("b "+content);
        stubbornLinkSender.send(content, target);
    }


    @Override
    public void onDeliver(Message m) {
        if(!delivered.contains(m)){
            delivered.add(m);
            callBack.onDeliver(m);
        }
    }
}
