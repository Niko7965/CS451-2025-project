package cs451.LatticeAgreement;

import cs451.GlobalCfg;
import cs451.Main;

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

        if(GlobalCfg.LA_DBG){
            System.out.println("Voting:");
            System.out.println("Proposal:");
            Main.printSet(proposalSet);
            System.out.println("Current:");
            Main.printSet(this.acceptedValue);
        }

        if (proposalSet.containsAll(acceptedValue)) {
            if(GlobalCfg.LA_DBG){
                System.out.println("Voted yes");
            }

            acceptedValue = proposalSet;
            return LatticeVote.positiveVoteFromProposal(proposal);
        } else {
            if(GlobalCfg.LA_DBG){
                System.out.println("Voted no");
            }

            acceptedValue.addAll(proposalSet);
            return LatticeVote.negativeVoteFromProposal(proposal);
        }
    }

    public void sendVoteForProposal(LatticeProposal proposal) throws InterruptedException {
        LatticeVote vote = getVoteForProposal(proposal);
        LatticeAgreements.getBeb().sendToTarget(vote, proposal.senderId);
    }



}
