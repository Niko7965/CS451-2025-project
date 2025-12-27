package cs451.BEB;

import cs451.GlobalCfg;
import cs451.Host;
import cs451.PerfectLinks.PLCallback;
import cs451.PerfectLinks.PLMessageRegular;
import cs451.PerfectLinks.PerfectLink;

import java.net.SocketException;
import java.net.UnknownHostException;

public class BestEffortBroadcast implements PLCallback {

    PerfectLink pl;
    BebCallback bebCallback;
    int selfId;
    int noOfTargets;

    public BestEffortBroadcast(Host selfHost,int selfId, int noOfTargets, BebCallback bebCallback) throws SocketException, UnknownHostException {
        this.pl = new PerfectLink(selfHost,this);
        this.bebCallback = bebCallback;
        this.selfId = selfId;
        this.noOfTargets = noOfTargets;
    }

    public void sendToTarget(Object payload, int targetNo) throws InterruptedException {
        System.out.println("Pl sending vote");
        pl.sendMessage(payload,selfId,targetNo);
    }


    public void broadcast(Object payload) throws InterruptedException {

        if(GlobalCfg.BEB_DBG){
            System.out.println("BEB Broadcasting: ");
            System.out.println(payload);
        }

        for(int i = 1; i <= noOfTargets; i++){
            if(i == selfId){
                continue;
            }
            pl.sendMessage(payload,selfId,i);
        }
        bebCallback.onDeliver(payload);
    }

    @Override
    public void onDeliver(PLMessageRegular m) {
        if(GlobalCfg.BEB_DBG){
            System.out.println("BEB Delivered "+m.getPayload()+" from: "+m.getMetadata().getSenderId());
        }
        bebCallback.onDeliver(m.getPayload());
    }

    @Override
    public void onShouldAck(PLMessageRegular m) {
        pl.onShouldAck(m);
    }
}
