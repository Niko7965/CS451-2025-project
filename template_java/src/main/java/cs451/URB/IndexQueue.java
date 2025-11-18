package cs451.URB;

import java.util.ArrayList;
import java.util.Optional;

public class IndexQueue {

    /*
    Simply a q that you can index into

    Should be able to clean when all indexes pass a threshold

     */

    private ArrayList<URBMessage> list;
    private Integer largestMessageNoSeen;

    public IndexQueue(){
        this.largestMessageNoSeen = null;
        this.list = new ArrayList<>();
    }

    /**
     * Enqueue, ensures that we do not add a message previously added
     * Assumes that messages are enqueued in perfect order
     */
    public void enqueue(URBMessage value){
        if(largestMessageNoSeen == null || value.urbMessageNo > largestMessageNoSeen){
            largestMessageNoSeen = value.urbMessageNo;
            list.add(value);
        }


    }

    public Optional<URBMessage> get(int index){
        if(index < list.size()){
            return Optional.of(list.get(index));
        }

        return Optional.empty();
    }

    /**
     * Removes message from queue, and updates indexes
     * Such that any index pointing to a later message, is shifted one back
     * ensuring that it points to the same message after removal
     * @param message - message to remove
     * @param indexes - indexes for each viewer
     * @return indexes after removal
     */
    public int[] removeMessage(URBMessage message, int[] indexes){
        int messageIndex = -1;
        for(int i = 0; i < list.size(); i++){
            if(message.equals(list.get(i))){
                messageIndex = i;
                break;
            }
        }

        if(messageIndex == -1){
            return indexes;
        }

        for(int i = 0; i < indexes.length; i++){
            if(indexes[i] > messageIndex){
                indexes[i] -= 1;
            }
        }

        list.remove(messageIndex);
        return indexes;
    }

    /**
     * This function will clean the q up to the lowest index in the array
     * It will return the number of values removed, such that each index
     * can be adjusted accordingly
     * @param indexes is the indexes of the q

     */
    //todo - consider only cleaning when gap is large enough
    public int clean(int[] indexes){
        int minIndex = indexes[0];
        for(int i = 0; i < indexes.length; i++){
            minIndex = Math.min(minIndex,indexes[i]);
        }

        ArrayList<URBMessage> newList = new ArrayList<>(list.size() - minIndex);
        for(int i = minIndex; i < list.size(); i++){
            newList.add(list.get(i));
        }

        list = newList;

        return minIndex;
    }

    public int size() {
        return list.size();
    }
}
