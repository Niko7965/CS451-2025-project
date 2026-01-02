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
    private ImmutableSet proposedSet;



    public LatticeAgreement(int instanceNo,int selfId){
        this.instanceNo = instanceNo;
        this.active = false;
        this.ackCount = 0;
        this.nackCount = 0;
        this.roundNo = 0;
        this.proposedSet = new ImmutableSet();
        this.voter = new LatticeVoter(instanceNo,selfId);
    }

    //Should only be called once
    public void propose(Set<Integer> proposal) throws InterruptedException {
        proposedSet = new ImmutableSet(proposal);
        active = true;
        roundNo++;
        ackCount = 0;
        nackCount = 0;

        if(GlobalCfg.LA_DBG){
            System.out.println("Proposed");
            Main.printSet(proposal);
        }

        LatticeProposal proposalMessage = new LatticeProposal(instanceNo,proposedSet.getInner(), roundNo, LatticeAgreements.getSenderId());
        LatticeAgreements.getBeb().broadcast(proposalMessage);
    }

    public int getInstanceNo(){
        return instanceNo;
    }

    public LatticeVoter getVoter(){
        return voter;
    }

    public void takeVote(LatticeVote vote){
        if(vote.instanceNo != this.instanceNo || vote.roundNumber != this.roundNo || !active){
            return;
        }

        if(GlobalCfg.LA_DBG){
            System.out.println(vote);
        }


        if(vote.isAck){
            ackCount++;
            if(GlobalCfg.LA_VOTE_DBG) {
                System.out.println("Instance no: "+instanceNo+" Ack count: " + ackCount);
            }
        }
        else {
            nackCount++;
            this.proposedSet = this.proposedSet.addAll(vote.proposedSet.getInner());
            if(GlobalCfg.LA_DBG) {
                System.out.println("Nacked, new set:");
                Main.printSet(vote.proposedSet.getInner());
                Main.printSet(proposedSet.getInner());
            }

        }
    }

    public Optional<Set<Integer>> getDeliverableSet(int noOfProcesses){

        if(active) {
            System.out.println("Checking");
        }

        if(active && ackCount > noOfProcesses / 2){
            System.out.println("Got it");
            active = false;
            return Optional.of(proposedSet.getInner());
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

            LatticeProposal proposalMessage = new LatticeProposal(instanceNo, proposedSet.getInner(), roundNo, LatticeAgreements.getSenderId());
            LatticeAgreements.getBeb().broadcast(proposalMessage);
        }
    }
}
