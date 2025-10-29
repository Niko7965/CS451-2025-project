package cs451.tests;

import cs451.PerfectLinks.PLMessageMetadata;

public class PLMessageMetadataTest {

    public static void main(String[] a){
        PLMessageMetadata m1 = new PLMessageMetadata(true,10,28,95);
        byte[] b1 = m1.asBytes();
        PLMessageMetadata m2 = PLMessageMetadata.fromBytes(b1);
        System.out.println(m1.equals(m2));
        System.out.println(m1.isAck() +" "+m2.isAck());
        System.out.println(m1.getMessageNo() +" "+m2.getMessageNo());
        System.out.println(m1.getReceiverId() +" "+m2.getReceiverId());
        System.out.println(m1.getSenderId() +" "+m2.getSenderId());
    }
}
