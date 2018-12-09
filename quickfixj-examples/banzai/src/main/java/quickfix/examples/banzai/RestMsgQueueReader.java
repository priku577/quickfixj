package quickfix.examples.banzai;

public class RestMsgQueueReader implements Runnable{

    BanzaiApplication fixApp;

    public RestMsgQueueReader(BanzaiApplication fixApp) {
        this.fixApp = fixApp;
    }

    @Override
    public void run() {
        fixApp.listenToRestMsgQueue();
    }
}
