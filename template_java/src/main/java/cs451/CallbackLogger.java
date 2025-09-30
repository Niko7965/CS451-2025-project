package cs451;

public class CallbackLogger implements OnDeliverCallBack {
    @Override
    public void onDeliver(Message m) {
        System.out.println("d "+m.sender+" "+m.content);
    }

    public CallbackLogger(){}
}
