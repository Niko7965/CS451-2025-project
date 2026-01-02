package cs451.LatticeAgreement;

import cs451.GlobalCfg;
import cs451.OutputWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static cs451.Main.*;

public class InstanceLocker implements LatticeCallback {
    private final LatticeAgreements LA;
    private int noOfActiveInstances;
    private int nextInstanceNoToDeliver;
    private final Object proposeLock;
    private final Object decideLock;
    private final ArrayList<LatticeDecision> decisionQueue; //todo priority queue
    private final OutputWriter outputWriter;



    //todo change
    public static final int maxNoOfActiveInstances = 1;


    public InstanceLocker(LatticeAgreements LA, OutputWriter outputWriter){
        this.LA = LA;
        this.proposeLock = new Object();
        this.decideLock = new Object();
        this.noOfActiveInstances = 0;
        this.nextInstanceNoToDeliver = 0;
        this.decisionQueue = new ArrayList<>();
        this.outputWriter = outputWriter;
    }

    public void propose(int instance, Set<Integer> proposalSet) throws InterruptedException {
        while (noOfActiveInstances > maxNoOfActiveInstances){
            proposeLock.wait();
        }
        LA.propose(instance,proposalSet);
        noOfActiveInstances++;
    }

    public void tryDeliverFromQueue() throws IOException {
        while(decisionQueue.stream().anyMatch(d -> d.instanceNo == nextInstanceNoToDeliver)){
            LatticeDecision toDeliver = decisionQueue.stream().
                    filter(d -> d.instanceNo == nextInstanceNoToDeliver)
                            .findFirst().get();
            deliver(toDeliver);
            nextInstanceNoToDeliver++;
            noOfActiveInstances--;
            if(noOfActiveInstances < maxNoOfActiveInstances){
                proposeLock.notifyAll();
            }
        }
    }

    public void deliver(LatticeDecision ld) throws IOException {
        Set<Integer> decisionSet = ld.decidedSet;

        if(GlobalCfg.LA_DBG || GlobalCfg.LA_SPARSE_DBG || GlobalCfg.MAIN_OUT_DEBUG){
            System.out.println("Decided on set for instance "+ld.instanceNo+":");

        }
        outputWriter.write(setToString(decisionSet)+"\n");
        printSet(decisionSet);
    }

    @Override
    public void onDeliver(LatticeDecision decision) {
        synchronized (decideLock) {

            decisionQueue.add(decision);

            try {
                tryDeliverFromQueue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
