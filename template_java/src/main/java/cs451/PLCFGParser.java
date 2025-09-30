package cs451;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PLCFGParser {

    private final int noOfMessages;
    private final int receiverId;

    public PLCFGParser(String path) throws FileNotFoundException {
        File f = new File(path);
        Scanner sc = new Scanner(f);

        noOfMessages = sc.nextInt();
        receiverId = sc.nextInt();
        sc.close();
    }

    public int getNoOfMessages(){
        return noOfMessages;
    }

    public int getReceiverId(){
        return receiverId;
    }
}
