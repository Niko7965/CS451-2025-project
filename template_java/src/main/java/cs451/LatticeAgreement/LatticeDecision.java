package cs451.LatticeAgreement;

import java.util.Set;

public class LatticeDecision {
    int instanceNo;
    Set<Integer> decidedSet;

    public LatticeDecision(int instanceNo, Set<Integer> decidedSet){
        this.instanceNo = instanceNo;
        this.decidedSet = decidedSet;
    }
}

