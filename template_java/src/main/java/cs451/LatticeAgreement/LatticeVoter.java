package cs451.LatticeAgreement;

import cs451.GlobalCfg;
import cs451.Main;



public class LatticeVoter {
    int instanceNo;
    int selfId;
    ImmutableSet acceptedValue;

    public LatticeVoter(int instanceNo, int selfId){
        this.instanceNo = instanceNo;
        this.selfId = selfId;
        this.acceptedValue = new ImmutableSet();
    }

    private synchronized LatticeVote getVoteForProposal(LatticeProposal proposal) {
        ImmutableSet proposalSet = proposal.proposedSet;

        if(GlobalCfg.LA_VOTE_DBG){
            System.out.println();
            System.out.println("Voting for instance no "+instanceNo+":");
            System.out.println("Proposal:");
            Main.printSet(proposalSet.getInner());
            System.out.println("Current:");
            Main.printSet(this.acceptedValue.getInner());
        }

        if (proposalSet.getInner().containsAll(acceptedValue.getInner())) {
            if(GlobalCfg.LA_VOTE_DBG){
                System.out.println("Voted yes");
                System.out.println();

            }
            this.acceptedValue = new ImmutableSet(proposalSet.getInner());
            return LatticeVote.positiveVoteFromProposal(proposal,selfId);


        } else {
            if(GlobalCfg.LA_VOTE_DBG){
                System.out.println("Voted no");
                System.out.println();
            }
            this.acceptedValue = acceptedValue.addAll(proposalSet.getInner());
            return LatticeVote.negativeVoteFromProposal(proposal,selfId,acceptedValue.getInner());
        }
    }

    public void sendVoteForProposal(LatticeProposal proposal) throws InterruptedException {
        LatticeVote vote = getVoteForProposal(proposal);
        LatticeAgreements.getBeb().sendToTarget(vote, proposal.senderId);
    }



}
