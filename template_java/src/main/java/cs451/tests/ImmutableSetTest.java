package cs451.tests;

import cs451.LatticeAgreement.ImmutableSet;
import cs451.Main;

import java.util.HashSet;
import java.util.Set;

public class ImmutableSetTest {


    public static void main(String[] a)  {
        testImmutSet();
    }

    public static void testImmutSet(){

        Set<Integer> a1 = Set.of(1,2,3);
        Set<Integer> a2 = Set.of(4,5,6);
        Set<Integer> a3 = Set.of(7,8,9);


        ImmutableSet ai = new ImmutableSet(a1);



        Set<Integer> aUnion = new HashSet<>(Set.copyOf(a1));
        aUnion.addAll(a2);
        aUnion.addAll(a3);

        ai = ai.addAll(a2);
        ai = ai.addAll(a3);


        if(!ai.getInner().containsAll(aUnion)){
            System.out.println("Failure, did not update properly");
            Main.printSet(ai.getInner());
        }
        else {
            System.out.println("Success!");
        }




    }
}
