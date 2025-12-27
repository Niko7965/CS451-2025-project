package cs451.LatticeAgreement;

import cs451.GlobalCfg;
import cs451.Main;

import java.util.Set;

public class LatticeVoter {
    int instanceNo;
    int selfId;
    Set<Integer> acceptedValue;

    public LatticeVoter(int instanceNo, int selfId){
        this.instanceNo = instanceNo;
        this.selfId = selfId;
        this.acceptedValue = Set.of();
    }

    private LatticeVote getVoteForProposal(LatticeProposal proposal) {
        Set<Integer> proposalSet = proposal.proposedSet;

        if(GlobalCfg.LA_VOTE_DBG){
            System.out.println("Voting for instance no "+instanceNo+":");
            System.out.println("Proposal:");
            Main.printSet(proposalSet);
            System.out.println("Current:");
            Main.printSet(this.acceptedValue);
        }

        if (proposalSet.containsAll(acceptedValue)) {
            if(GlobalCfg.LA_VOTE_DBG){
                System.out.println("Voted yes");
            }

            acceptedValue = proposalSet;
            return LatticeVote.positiveVoteFromProposal(proposal,selfId);
        } else {
            if(GlobalCfg.LA_VOTE_DBG){
                System.out.println("Voted no");
            }

            acceptedValue.addAll(proposalSet);
            return LatticeVote.negativeVoteFromProposal(proposal,selfId,acceptedValue);
        }
    }

    public void sendVoteForProposal(LatticeProposal proposal) throws InterruptedException {
        LatticeVote vote = getVoteForProposal(proposal);
        LatticeAgreements.getBeb().sendToTarget(vote, proposal.senderId);
    }



}
