package cs451.PerfectLinks;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PlCfgParser {

    private final int noOfMessages;
    private final int receiverId;

    public PlCfgParser(String path) throws FileNotFoundException {
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
