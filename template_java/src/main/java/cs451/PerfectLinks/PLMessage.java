package cs451.PerfectLinks;

public abstract class PLMessage {

    public static PLMessage fromString(String s){
        String[] contents = s.split(" ");
        String type = contents[0];
        boolean isAck = type.equals("ACK");

        if (isAck){
            return new PLAckMessage(contents);
        }
        return new PLMessageRegular(contents);

    }

    public abstract boolean isAck();
}
