package stark.a.is.zhang.tcptest.client.runnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransferProxy {
    private static final String TAG = "ZJTest:TransProxy";

    private static TransferProxy sTransferProxy;

    private ExecutorService mExecutorService;

    public static TransferProxy getInstance() {
        if (sTransferProxy == null) {
            sTransferProxy = new TransferProxy();
        }

        return sTransferProxy;
    }

    private TransferProxy() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable) {
        mExecutorService.execute(runnable);
    }
}