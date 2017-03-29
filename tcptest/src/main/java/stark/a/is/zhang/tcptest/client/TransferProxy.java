package stark.a.is.zhang.tcptest.client;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import stark.a.is.zhang.tcptest.util.NetworkUtil;

class TransferProxy {
    private static final String TAG = "ZJTest:TransProxy";

    private Handler mHandler;
    private String mServerIp;
    private ExecutorService mExecutorService;
    private List<Socket> mCurrentSocket;

    TransferProxy(Handler handler, String serverIp) {
        mHandler = handler;
        mServerIp = serverIp;
        mExecutorService = Executors.newCachedThreadPool();
        mCurrentSocket = new ArrayList<>();
    }

    void init() {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                mCurrentSocket.add(socket);

                try {
                    InetAddress inetAddress = InetAddress.getByName(mServerIp);
                    SocketAddress socketAddress = new InetSocketAddress(
                            inetAddress, NetworkUtil.PORT);

                    socket.connect(socketAddress);

                    Log.d(TAG, "connect successful");

                    PrintWriter printWriter = NetworkUtil.getSocketPrintWriter(socket);
                    printWriter.print(NetworkUtil.SYNC);
                    printWriter.flush();

                    Log.d(TAG, "after send SYNC");

                    String data = NetworkUtil.getStringFromSocket(socket);

                    Log.d(TAG, "receive data: " + data);

                    if (data != null && data.equals(NetworkUtil.ACK)) {
                        mHandler.sendEmptyMessage(Constants.CONNECT_SUCCESSFUL);
                    }

                    printWriter.close();

                    socket.close();
                    mCurrentSocket.remove(socket);
                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                }
            }
        });
    }

    void quit() {
        try {
            if (mCurrentSocket.size() > 0) {
                for (Socket socket : mCurrentSocket) {
                    if (!socket.isClosed()) {
                        socket.close();
                    }
                }

                mCurrentSocket.clear();
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }

        mExecutorService.shutdown();
    }
}