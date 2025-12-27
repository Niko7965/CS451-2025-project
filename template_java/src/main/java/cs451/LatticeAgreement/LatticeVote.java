package cs451.LatticeAgreement;

import java.io.Serializable;
import java.util.Set;

public class LatticeVote implements Serializable {

    int sender;
    int instanceNo;
    boolean isAck;
    int roundNumber;
    Set<Integer> proposedSet; //note is empty if isAck


    public LatticeVote(int sender, int instanceNo, boolean isAck, int roundNumber, Set<Integer> proposedSet){
        this.sender = sender;
        this.instanceNo = instanceNo;
        this.isAck = isAck;
        this.roundNumber = roundNumber;
        this.proposedSet = proposedSet;
    }

    public static LatticeVote positiveVoteFromProposal(LatticeProposal proposal, int sender){
        return new LatticeVote(sender,proposal.instanceNo,true, proposal.roundNo, Set.of());
    }

    public static LatticeVote negativeVoteFromProposal(LatticeProposal proposal, int sender){
        return new LatticeVote(sender, proposal.instanceNo,false, proposal.roundNo, proposal.proposedSet);
    }


    public String toString(){
        return ("LVote - i = "+instanceNo+ "isAck = "+isAck + " Sender:"+sender);
    }

}
