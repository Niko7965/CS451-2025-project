package cs451.LatticeAgreement;

import java.util.Set;

public class LatticeVote {

    int instanceNo;
    boolean isAck;
    int roundNumber;
    Set<Integer> proposedSet; //note is empty if isAck


    public LatticeVote(int instanceNo, boolean isAck, int roundNumber, Set<Integer> proposedSet){
        this.instanceNo = instanceNo;
        this.isAck = isAck;
        this.roundNumber = roundNumber;
        this.proposedSet = proposedSet;
    }

    public static LatticeVote positiveVoteFromProposal(LatticeProposal proposal){
        return new LatticeVote(proposal.instanceNo,true, proposal.roundNo, Set.of());
    }

    public static LatticeVote negativeVoteFromProposal(LatticeProposal proposal){
        return new LatticeVote(proposal.instanceNo,false, proposal.roundNo, proposal.proposedSet);
    }

}
