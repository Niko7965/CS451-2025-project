package cs451.LatticeAgreement;

import java.util.ArrayList;
import java.util.Set;

public class LatticeCallBackSleeper implements LatticeCallback {


    private final ArrayList<Set<Integer>> decisions;
    private Object lock;

    public LatticeCallBackSleeper(int noOfAgreements){
        decisions = new ArrayList<>(noOfAgreements);
        for(int i = 0; i < noOfAgreements; i++){
            decisions.add(null);
        }
    }

    public Set<Integer> getDecision(int index) throws InterruptedException {
        synchronized (decisions){
            while (decisions.get(index) == null){
                lock.wait();
            }
            return decisions.get(index);
        }
    }


    @Override
    public void onDeliver(LatticeDecision decision) {
        synchronized (decisions){
            decisions.set(decision.instanceNo,decision.decidedSet);
            lock.notifyAll();
        }
    }
}
