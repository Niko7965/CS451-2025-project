package cs451.LatticeAgreement;

import java.util.Set;

public class LatticeVoter {
    int instanceNo;
    Set<Integer> acceptedValue;

    public LatticeVoter(int instanceNo){
        this.instanceNo = instanceNo;
        this.acceptedValue = Set.of();
    }

    private LatticeVote getVoteForProposal(LatticeProposal proposal) {
        Set<Integer> proposalSet = proposal.proposedSet;
        if (proposalSet.containsAll(acceptedValue)) {
            acceptedValue = proposalSet;
            return LatticeVote.positiveVoteFromProposal(proposal);
        } else {
            acceptedValue.addAll(proposalSet);
            return LatticeVote.negativeVoteFromProposal(proposal);
        }
    }

    public void sendVoteForProposal(LatticeProposal proposal) throws InterruptedException {
        LatticeVote vote = getVoteForProposal(proposal);
        LatticeAgreements.getBeb().broadcast(vote);
    }



}
