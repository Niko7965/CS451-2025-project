package cs451;

import cs451.PerfectLinks.PLCallback;
import cs451.PerfectLinks.PLMessageRegular;

public class PLTestCallback implements PLCallback {


    @Override
    public void onDeliver(PLMessageRegular m) {
        System.out.println("PLRec: "+m);
    }

    @Override
    public void onShouldAck(PLMessageRegular m) {

    }
}
