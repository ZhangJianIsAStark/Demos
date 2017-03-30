package stark.a.is.zhang.tcptest.server.runnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTransferProxy {
    private static ServerTransferProxy sServerTransferProxy;
    private static boolean sIsCreated;

    private ExecutorService mExecutorService;

    public static ServerTransferProxy getInstance() {
        if (sServerTransferProxy == null) {
            sServerTransferProxy = new ServerTransferProxy();
            sIsCreated = true;
        }

        return sServerTransferProxy;
    }

    private ServerTransferProxy() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    public static boolean isCreated() {
        return sIsCreated;
    }

    public void execute(Runnable runnable) {
        mExecutorService.execute(runnable);
    }

    public void dispose() {
        if (mExecutorService != null) {
            mExecutorService.shutdown();
            sServerTransferProxy = null;
            sIsCreated = false;
        }
    }
}
