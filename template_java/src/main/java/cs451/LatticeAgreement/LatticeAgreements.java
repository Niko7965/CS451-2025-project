package cs451.LatticeAgreement;

import cs451.BEB.BebCallback;
import cs451.BEB.BestEffortBroadcast;
import cs451.GlobalCfg;
import cs451.Host;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class LatticeAgreements extends Thread implements BebCallback{
    //todo - lock on individual agreements

    private static Integer senderId;
    private final int noOfProcesses;
    private static BestEffortBroadcast beb;
    private final LatticeCallback latticeCallback;


    private final HashMap<Integer,LatticeAgreement> agreementForInstanceNo;


    public LatticeAgreements(int selfId, int noOfProcesses, Host selfHost, LatticeCallback latticeCallback) throws SocketException, UnknownHostException {
        this.noOfProcesses = noOfProcesses;
        beb = new BestEffortBroadcast(selfHost,selfId,noOfProcesses,this);
        senderId = selfId;
        agreementForInstanceNo = new HashMap<>();
        this.latticeCallback = latticeCallback;
    }

    public static BestEffortBroadcast getBeb(){
        return beb;
    }

    public static int getSenderId(){
        return senderId;
    }

    @Override
    public void run(){
        try {
            loop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void loop() throws InterruptedException {
        while(true){
            Thread.sleep(1000);
            if(GlobalCfg.LA_LOOPDBG){
                System.out.println("looping");
            }

            synchronized (agreementForInstanceNo){
                if(GlobalCfg.LA_LOOPDBG){
                    System.out.println("got lock");
                }
                for(LatticeAgreement a: agreementForInstanceNo.values()){
                    Optional<Set<Integer>> deliverableSetOption =  a.getDeliverableSet(noOfProcesses);
                    if(deliverableSetOption.isPresent()){
                        LatticeDecision decision = new LatticeDecision(a.getInstanceNo(),deliverableSetOption.get());
                        latticeCallback.onDeliver(decision);
                        //todo deliver; maybe remove from list
                        //todo Actually, can only do this if enough votes have been sent as well
                    }
                    else {
                        a.reBroadcastIfNackedAndSufficientlyVoted(noOfProcesses);
                    }
                }


            }
            if(GlobalCfg.LA_LOOPDBG){
                System.out.println("Released lock");
            }
        }
    }


    public LatticeAgreement ensureExistsAgreementForInstance(int instanceNo){
        synchronized (agreementForInstanceNo) {
            if (agreementForInstanceNo.containsKey(instanceNo)) {
                return agreementForInstanceNo.get(instanceNo);
            }
            LatticeAgreement newAgreement = new LatticeAgreement(instanceNo,senderId);
            agreementForInstanceNo.put(instanceNo, newAgreement);
            return newAgreement;
        }
    }

    public void propose(int instance, Set<Integer> proposalSet) throws InterruptedException {
        LatticeAgreement latticeAgreement = ensureExistsAgreementForInstance(instance);
        synchronized (latticeAgreement) {
            latticeAgreement.propose(proposalSet);
        }

    }

    public void giveVote(LatticeVote vote){
        LatticeAgreement latticeAgreement = ensureExistsAgreementForInstance(vote.instanceNo);

        synchronized (latticeAgreement){
            latticeAgreement.takeVote(vote);
        }
    }

    public void sendVoteForProposal(LatticeProposal proposal) throws InterruptedException {
        LatticeAgreement latticeAgreement = ensureExistsAgreementForInstance(proposal.instanceNo);

        synchronized (latticeAgreement){
            latticeAgreement.getVoter().sendVoteForProposal(proposal);
        }
    }


    @Override
    public void onDeliver(Object o) {

        if(o instanceof LatticeVote){
            giveVote((LatticeVote) o);
            return;
        }
        if(o instanceof LatticeProposal){

            try {
                sendVoteForProposal((LatticeProposal) o);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void kill() {
        beb.kill();
    }
}
