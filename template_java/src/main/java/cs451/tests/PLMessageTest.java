package cs451.tests;

import cs451.PerfectLinks.PLAckMessage;
import cs451.PerfectLinks.PLMessage;
import cs451.PerfectLinks.PLMessageRegular;

import java.io.IOException;

public class PLMessageTest {

    public static void main(String[] a) throws IOException, ClassNotFoundException {
        testRegMessageConversion();
        testAckMessageConversion();
        testPayloadConversion();

    }

    public static void testRegMessageConversion() throws IOException, ClassNotFoundException {
        System.out.println("REGULAR MESSAGE CONVERSION TEST");

        PLMessageRegular m = new PLMessageRegular(1,2,3,"asdf");
        byte[] mBytes = m.toBytes();
        PLMessageRegular m2 = (PLMessageRegular) PLMessage.fromBytes(mBytes);
        System.out.println(m.equals(m2));
    }

    public static void testAckMessageConversion() throws IOException, ClassNotFoundException {
        System.out.println("ACK MESSAGE CONVERSION TEST");

        PLAckMessage m = new PLMessageRegular(1,2,3,"asdf").simpleAck();


        byte[] mBytes = m.toBytes();
        PLAckMessage m2 = (PLAckMessage) PLMessage.fromBytes(mBytes);
        System.out.println(m.equals(m2));
    }

    public static void testPayloadConversion() throws IOException, ClassNotFoundException {
        System.out.println("PAYLOAD CONVERSION TEST");

        String payload = "asdf";
        byte[] pBytes = PLMessageRegular.payloadToBytes(payload);
        Object o = PLMessageRegular.payloadFromBytes(pBytes);
        String payload2 = (String) o;
        System.out.println(payload2.equals(payload));

    }
}
