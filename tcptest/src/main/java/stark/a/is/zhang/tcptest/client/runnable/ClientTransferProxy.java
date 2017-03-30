package stark.a.is.zhang.tcptest.client.runnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTransferProxy {
    private static ClientTransferProxy sTransferProxy;

    private static boolean sIsCreated;

    private ExecutorService mExecutorService;

    public static ClientTransferProxy getInstance() {
        if (sTransferProxy == null) {
            sTransferProxy = new ClientTransferProxy();
            sIsCreated = true;
        }

        return sTransferProxy;
    }

    private ClientTransferProxy() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable) {
        mExecutorService.execute(runnable);
    }

    public static boolean isCreated() {
        return sIsCreated;
    }

    public void dispose() {
        if (mExecutorService != null) {
            mExecutorService.shutdown();
            sTransferProxy = null;
            sIsCreated = false;
        }
    }
}