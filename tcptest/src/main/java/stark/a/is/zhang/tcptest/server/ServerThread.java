package stark.a.is.zhang.tcptest.server;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static stark.a.is.zhang.tcptest.util.NetworkUtil.PORT;

class ServerThread extends Thread {
    private static String TAG = "ZJTest:ServerThread";

    private Handler mHandler;

    private ServerSocket mServerSocket;

    private boolean mQuit;

    private ExecutorService mExecutorService;

    private List<ClientProxy> mClientProxies;

    ServerThread(Handler handler) {
        mHandler = handler;

        try {
            mServerSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }

        if (mServerSocket == null) {
            mQuit = true;
        }

        mExecutorService = Executors.newCachedThreadPool();
        mClientProxies = new ArrayList<>();
    }

    void quit() {
        mQuit = true;

        for (ClientProxy clientProxy : mClientProxies) {
            clientProxy.quit();
        }
        mClientProxies.clear();

        mExecutorService.shutdown();

        try {
            if (mServerSocket != null) {
                mServerSocket.close();
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void run() {
        while (!mQuit) {
            try {
                Socket client = mServerSocket.accept();

                ClientProxy clientProxy = new ClientProxy(client, mHandler);
                mClientProxies.add(clientProxy);

                mExecutorService.execute(clientProxy);
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }
        }
    }
}
