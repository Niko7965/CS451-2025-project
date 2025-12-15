package cs451.LatticeAgreement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class LACfgParser {



    int noOfProposals;
    int maxElementsPerProposal;
    int numberOfDistinctElements;

    Scanner sc;

    public LACfgParser(String path) throws FileNotFoundException {
        File f = new File(path);
        sc = new Scanner(f);

        noOfProposals = sc.nextInt();
        maxElementsPerProposal = sc.nextInt();
        numberOfDistinctElements = sc.nextInt();
        sc.nextLine();
    }

    public Set<Integer> getNextProposalSet(){
        String line = sc.nextLine();
        return Arrays.stream(line.split(" ")).map(Integer::parseInt).collect(Collectors.toSet());
    }

    public int getNoOfProposals() {
        return noOfProposals;
    }

    public int getMaxElementsPerProposal() {
        return maxElementsPerProposal;
    }

    public int getNumberOfDistinctElements() {
        return numberOfDistinctElements;
    }


}
