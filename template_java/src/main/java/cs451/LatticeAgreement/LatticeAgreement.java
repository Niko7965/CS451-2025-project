package cs451.LatticeAgreement;


import cs451.GlobalCfg;
import cs451.Main;

import java.util.Optional;
import java.util.Set;

public class LatticeAgreement {
    private final LatticeVoter voter;

    private final int instanceNo;
    private boolean active;
    private int ackCount;
    private int nackCount;
    private int roundNo;
    private Set<Integer> proposedSet;



    public LatticeAgreement(int instanceNo,int selfId){
        this.instanceNo = instanceNo;
        this.active = false;
        this.ackCount = 0;
        this.nackCount = 0;
        this.roundNo = 0;
        this.proposedSet = Set.of();
        this.voter = new LatticeVoter(instanceNo,selfId);
    }

    //Should only be called once
    public void propose(Set<Integer> proposal) throws InterruptedException {
        proposedSet = proposal;
        active = true;
        roundNo++;
        ackCount = 0;
        nackCount = 0;

        if(GlobalCfg.LA_DBG){
            System.out.println("Proposed");
            Main.printSet(proposal);
        }

        LatticeProposal proposalMessage = new LatticeProposal(instanceNo,proposedSet, roundNo, LatticeAgreements.getSenderId());
        LatticeAgreements.getBeb().broadcast(proposalMessage);
    }

    public int getInstanceNo(){
        return instanceNo;
    }

    public LatticeVoter getVoter(){
        return voter;
    }

    public void takeVote(LatticeVote vote){
        if(vote.instanceNo != this.instanceNo || vote.roundNumber != this.roundNo){
            return;
        }

        if(GlobalCfg.LA_DBG){
            System.out.println(vote);
        }

        if(!active){
            System.out.println("ERROR - SOMEHOW GOT VOTE WHILE INACTIVE "+ instanceNo);
        }

        if(vote.isAck){
            ackCount++;
            if(GlobalCfg.LA_VOTE_DBG) {
                System.out.println("Instance no: "+instanceNo+" Ack count: " + ackCount);
            }
        }
        else {
            nackCount++;
            this.proposedSet.addAll(vote.proposedSet);
            if(GlobalCfg.LA_DBG) {
                System.out.println("Nacked, new set:");
                Main.printSet(vote.proposedSet);
                Main.printSet(proposedSet);
            }

        }
    }

    public Optional<Set<Integer>> getDeliverableSet(int noOfProcesses){
        if(active && ackCount > noOfProcesses / 2){
            active = false;
            return Optional.of(proposedSet);
        }
        return Optional.empty();
    }

    public void reBroadcastIfNackedAndSufficientlyVoted(int noOfProcesses) throws InterruptedException {
        if(active && nackCount > 0 && ackCount+nackCount > noOfProcesses/2) {
            roundNo++;
            ackCount = 0;
            nackCount = 0;

            if (GlobalCfg.LA_DBG) {
                System.out.println("rebroadcasting, round:" + roundNo);
            }

            LatticeProposal proposalMessage = new LatticeProposal(instanceNo, proposedSet, roundNo, LatticeAgreements.getSenderId());
            LatticeAgreements.getBeb().broadcast(proposalMessage);
        }
    }
}
