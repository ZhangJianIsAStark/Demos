package stark.a.is.zhang.tcptest.server;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import stark.a.is.zhang.tcptest.server.runnable.ClientProxyRunnable;
import stark.a.is.zhang.tcptest.server.runnable.ServerTransferProxy;

import static stark.a.is.zhang.tcptest.util.NetworkUtil.PORT;

class ServerThread extends Thread {
    private static String TAG = "ZJTest:ServerThread";

    private Handler mHandler;

    private ServerSocket mServerSocket;

    private boolean mQuit;

    private List<ClientProxyRunnable> mClientProxies;

    private Context mContext;

    ServerThread(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;

        try {
            mServerSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }

        if (mServerSocket == null) {
            mQuit = true;
        }

        mClientProxies = new ArrayList<>();
    }

    void quit() {
        mQuit = true;

        for (ClientProxyRunnable clientProxy : mClientProxies) {
            clientProxy.quit();
        }

        mClientProxies.clear();

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

                ClientProxyRunnable clientProxy =
                        new ClientProxyRunnable(client, mHandler, mContext);
                mClientProxies.add(clientProxy);

                ServerTransferProxy.getInstance().execute(clientProxy);
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }
        }
    }
}
