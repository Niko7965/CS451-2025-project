package cs451.LatticeAgreement;

import java.io.Serializable;
import java.util.Set;

public class LatticeProposal implements Serializable {

    int instanceNo;
    Set<Integer> proposedSet;
    int roundNo;
    int senderId;

    public LatticeProposal(int instanceNo, Set<Integer> proposedSet, int roundNo, int senderId){
        this.instanceNo = instanceNo;
        this.proposedSet = proposedSet;
        this.roundNo = roundNo;
        this.senderId = senderId;
    }


    public String toString(){
        return "LProposal - i = "+instanceNo;
    }

}
