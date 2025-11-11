package cs451.PerfectLinks;

public interface PLCallback {
    void onDeliver(PLMessageRegular m);
    void onShouldAck(PLMessageRegular m);
}
