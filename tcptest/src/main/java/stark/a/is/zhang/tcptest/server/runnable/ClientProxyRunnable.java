package stark.a.is.zhang.tcptest.server.runnable;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import stark.a.is.zhang.tcptest.server.Constants;
import stark.a.is.zhang.tcptest.util.NetworkUtil;

public class ClientProxyRunnable implements Runnable {
    private static String TAG = "ZJTest:CPRunnable";

    private Socket mSocket;
    private Handler mHandler;
    private boolean mQuit;

    public ClientProxyRunnable(Socket socket, Handler handler) {
        mSocket = socket;

        try {
            mSocket.setKeepAlive(false);
            mSocket.setSoTimeout(5000);
        } catch (SocketException e) {
            Log.d(TAG, e.toString());
        }

        mHandler = handler;
    }

    @Override
    public void run() {
        try {
            PrintWriter printWriter = NetworkUtil
                    .getSocketPrintWriter(mSocket);

            int timeOutCount = 0;

            while (!mQuit) {
                String temp = NetworkUtil.getStringFromSocket(mSocket);

                if (temp == null || temp.length() <= 0) {
                    Log.d(TAG, "timeout count: " + timeOutCount);

                    ++timeOutCount;

                    if (timeOutCount >= 3) {
                        Log.d(TAG, "timeout, close the clientProxy");
                        quit();
                    }

                    continue;
                }

                if (temp.equals(NetworkUtil.SYNC)) {
                    mHandler.sendEmptyMessage(
                            Constants.ServerServiceMsg.STOP_BROADCAST);

                    Log.d(TAG, "receive SYNC");

                    printWriter.print(NetworkUtil.ACK);
                    printWriter.flush();

                    Log.d(TAG, "after send ACK");
                }
            }

            printWriter.close();

            Log.d(TAG, "clientProxy finish");
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void quit() {
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }

            mQuit = true;
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }
}