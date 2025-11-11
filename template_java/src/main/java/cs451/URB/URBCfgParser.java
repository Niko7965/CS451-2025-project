package cs451.URB;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class URBCfgParser {

    private final int noOfMessages;

    public URBCfgParser(String path) throws FileNotFoundException {
        File f = new File(path);
        Scanner sc = new Scanner(f);

        noOfMessages = sc.nextInt();
        sc.close();
    }

    public int getNoOfMessages(){
        return noOfMessages;
    }

}

