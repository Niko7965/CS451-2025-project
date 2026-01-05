package cs451.LatticeAgreement;

import cs451.GlobalCfg;
import cs451.OutputWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Set;

import static cs451.Main.*;

public class InstanceLocker implements LatticeCallback {
    private LatticeAgreements LA;
    private int noOfActiveInstances;
    private int nextInstanceNoToDeliver;
    private final Object proposeLock;
    private final PriorityQueue<LatticeDecision> decisionQueue;
    private final OutputWriter outputWriter;



    //todo change
    public static final int maxNoOfActiveInstances = 5;


    public InstanceLocker(OutputWriter outputWriter){
        this.proposeLock = new Object();
        this.noOfActiveInstances = 0;
        this.nextInstanceNoToDeliver = 0;
        this.decisionQueue = new PriorityQueue<>(Comparator.comparingInt(ld -> ld.instanceNo));
        this.outputWriter = outputWriter;
    }

    public void giveLA(LatticeAgreements LA){
        this.LA = LA;

    }

    public void propose(int instance, Set<Integer> proposalSet) throws InterruptedException {
        synchronized (proposeLock) {
            while (noOfActiveInstances >= maxNoOfActiveInstances) {
                proposeLock.wait();
            }
            LA.propose(instance, proposalSet);
            noOfActiveInstances++;
        }
    }

    public void tryDeliverFromQueue() throws IOException {
        while (!decisionQueue.isEmpty() && decisionQueue.peek().instanceNo == nextInstanceNoToDeliver){
            LatticeDecision toDeliver = decisionQueue.poll();
            deliver(toDeliver);
            nextInstanceNoToDeliver++;
            noOfActiveInstances--;

            if(noOfActiveInstances < maxNoOfActiveInstances){
                proposeLock.notify();
            }
        }





    }

    public void deliver(LatticeDecision ld) throws IOException {
        Set<Integer> decisionSet = ld.decidedSet;

        if (GlobalCfg.LA_DBG || GlobalCfg.LA_SPARSE_DBG || GlobalCfg.MAIN_OUT_DEBUG) {

            System.out.println();
            System.out.println("############################################");
            System.out.println("Decided on set for instance " + ld.instanceNo + ":");
            System.out.println("############################################");


        }
        outputWriter.write(setToString(decisionSet) + "\n");
        printSet(decisionSet);

        if (GlobalCfg.LA_DBG || GlobalCfg.LA_SPARSE_DBG || GlobalCfg.MAIN_OUT_DEBUG) {
            System.out.println();
        }
    }

    @Override
    public void onDeliver(LatticeDecision decision) {
        synchronized (proposeLock) {

            if(GlobalCfg.LOCKER_DEBUG){
                System.out.println("Got a decision to queue");
            }

            decisionQueue.add(decision);

            try {
                tryDeliverFromQueue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
