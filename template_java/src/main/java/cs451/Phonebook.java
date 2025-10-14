package cs451;

import java.util.HashMap;
import java.util.List;

public class Phonebook {


    private static final HashMap<Integer,Host> idToHostMap = new HashMap<>();

    public static void init(List<Host> hosts){
        for(Host h: hosts){
            idToHostMap.put(h.getId(),h);
        }
    }

    public static Host hostFromId(int id){
        return idToHostMap.get(id);
    }



}
