package cs451.PerfectLinks;

public abstract class PLMessage {

    public static PLMessage fromString(String s){
        System.out.println(s);
        String[] contents = s.split(" ");
        return fromStringArr(contents);
    }

    public static PLMessage fromStringArr(String[] contents){
        String type = contents[0];
        boolean isAck = type.equals("ACK");

        if (isAck){
            return new PLAckMessage(contents);
        }
        return new PLMessageRegular(contents);
    }



    public abstract boolean isAck();
}
