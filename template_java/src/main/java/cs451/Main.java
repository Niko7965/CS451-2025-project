package cs451;


import cs451.LatticeAgreement.InstanceLocker;
import cs451.LatticeAgreement.LACfgParser;
import cs451.LatticeAgreement.LatticeAgreements;
import cs451.LatticeAgreement.LatticeCallBackSleeper;
import cs451.PerfectLinks.PerfectLink;
import cs451.URB.UniformReliableBroadcast;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;


public class Main {
    static OutputWriter outputWriter;
    static LatticeAgreements LA;

    private static void handleSignal() {
        System.out.println("Immediately stopping network packet processing.");
        try {
            outputWriter.close();
            LA.kill();

        } catch (IOException ignored) {
        }
        //write/flush output file if necessary
        System.out.println("Writing output.");
    }

    private static void initSignalHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                handleSignal();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Parser parser = new Parser(args);
        parser.parse();

        initSignalHandlers();

        // example
        long pid = ProcessHandle.current().pid();
        System.out.println("My PID: " + pid + "\n");
        System.out.println("From a new terminal type `kill -SIGINT " + pid + "` or `kill -SIGTERM " + pid + "` to stop processing packets\n");

        System.out.println("My ID: " + parser.myId() + "\n");
        System.out.println("List of resolved hosts is:");
        System.out.println("==========================");
        for (Host host : parser.hosts()) {
            System.out.println(host.getId());
            System.out.println("Human-readable IP: " + host.getIp());
            System.out.println("Human-readable Port: " + host.getPort());
            System.out.println();
        }
        System.out.println();

        System.out.println("Path to output:");
        System.out.println("===============");
        System.out.println(parser.output() + "\n");

        System.out.println("Path to config:");
        System.out.println("===============");
        System.out.println(parser.config() + "\n");

        System.out.println("Doing some initialization\n");

        System.out.println("Broadcasting and delivering messages...\n");


        System.out.println("Start links");

        outputWriter = new OutputWriter(parser.output());
        Phonebook.init(parser.hosts());



        LACfgParser laCfgParser = new LACfgParser(parser.config());

        int noOfAgreements = laCfgParser.getNoOfProposals();

        doLatticeTask(noOfAgreements, parser, laCfgParser);

        // After a process finishes broadcasting,
        // it waits forever for the delivery of messages.
        while (true) {
            // Sleep for 1 hour
            Thread.sleep(60 * 60 * 1000);
        }

    }

    private static void doPLTest(Parser parser) throws SocketException, UnknownHostException, InterruptedException {

        Host selfHost = parser.hosts().stream().filter(h -> h.getId() == parser.myId()).findFirst().get();
        PLTestCallback tcb = new PLTestCallback();
        PerfectLink pl = new PerfectLink(selfHost,tcb);

        int otherTarget = 1;
        if(selfHost.getId() == 1){
            otherTarget = 2;
        }

        for(int i = 0; i<10; i++){
            pl.sendMessage(i,selfHost.getId(),otherTarget);
        }
    }

    private static void doLatticeTask(int noOfAgreements, Parser parser, LACfgParser laCfgParser) throws IOException, InterruptedException {
        //LatticeCallBackSleeper callback = new LatticeCallBackSleeper(noOfAgreements);
        InstanceLocker locker = new InstanceLocker(outputWriter);
        LatticeAgreements latticeAgreements = new LatticeAgreements(parser.myId(), parser.hosts().size(), Phonebook.hostFromId(parser.myId()), locker);
        locker.giveLA(latticeAgreements);

        latticeAgreements.start();



        System.out.println("my id: "+ parser.myId());


        for (int i = 0; i < noOfAgreements; i++) {
            Set<Integer> proposalSet = laCfgParser.getNextProposalSet();
            locker.propose(i,proposalSet);
        }
    }

    public static String setToString(Set<Integer> set){
        StringBuilder s = new StringBuilder();
        ArrayList<Integer> setList = new ArrayList<>(set);
        setList.sort(Integer::compare);
        for(Integer i : setList){
            s.append(i).append(" ");
        }
        return s.toString();
    }

    public static void printSet(Set<Integer> set){
        System.out.println(setToString(set));
    }


}
