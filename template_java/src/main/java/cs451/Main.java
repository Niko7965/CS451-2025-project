package cs451;

import cs451.PerfectLinks.PerfectLink;

import java.io.IOException;


public class Main {
    static OutputWriter outputWriter;
    static PerfectLink pl;

    private static void handleSignal() {
        System.out.println("Immediately stopping network packet processing.");
        pl.kill();
        try {
            outputWriter.close();

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
    for (Host host: parser.hosts()) {
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


    PerfectLink perfectLink = new PerfectLink(hostFromId(parser.myId(),parser),new CallbackLogger(outputWriter),outputWriter);
    pl = perfectLink;
    PLCFGParser taskParser = new PLCFGParser(parser.config());

    Phonebook.init(parser.hosts());
    int myId = parser.myId();


    if(!(myId== taskParser.getReceiverId())){
        Host receiverHost = hostFromId(taskParser.getReceiverId(),parser);

        for(int i = 1; i <= taskParser.getNoOfMessages();i++){
            perfectLink.sendIntMessage(i,myId, receiverHost.getId());
        }
    }

    // After a process finishes broadcasting,
    // it waits forever for the delivery of messages.
    while (true) {
        // Sleep for 1 hour
        Thread.sleep(60 * 60 * 1000);
    }

}

public static Host hostFromId(int id, Parser parser){
    return parser.hosts().stream().filter(h -> h.getId() == id).findFirst().get();
}

}
