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
    private static String TAG = "ZJTest:CpRunnable";

    private Socket mSocket;

    private Handler mHandler;

    private boolean mQuit;

    private int mTimeOutCount = 0;

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

            while (!mQuit) {
                String msg = NetworkUtil.getStringFromSocket(mSocket);

                if (msg == null || msg.length() <= 0) {
                    judgeForQuit();
                    continue;
                }

                mTimeOutCount = 0;

                switch (msg) {
                    case NetworkUtil.SYNC:
                        Log.d(TAG, "receive SYNC");

                        sendMsgToService(Constants.ServerServiceMsg.STOP_BROADCAST);
                        sendMsgToClient(printWriter, NetworkUtil.ACK);
                        break;
                }
            }

            printWriter.close();

            Log.d(TAG, "clientProxy finish");
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    private void judgeForQuit() {
        Log.d(TAG, "timeout count: " + mTimeOutCount);

        ++mTimeOutCount;

        if (mTimeOutCount >= 3) {
            Log.d(TAG, "timeout, close the clientProxy");
            quit();
        }
    }

    private void sendMsgToService(int msgId) {
        mHandler.sendEmptyMessage(msgId);
    }

    private void sendMsgToClient(PrintWriter printWriter, String msg) {
        printWriter.print(msg);
        printWriter.flush();
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