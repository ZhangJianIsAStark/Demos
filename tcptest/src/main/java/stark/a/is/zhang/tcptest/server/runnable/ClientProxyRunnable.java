package stark.a.is.zhang.tcptest.server.runnable;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.Set;

import stark.a.is.zhang.tcptest.server.Constants;
import stark.a.is.zhang.tcptest.util.FileUtil;
import stark.a.is.zhang.tcptest.util.NetworkUtil;

public class ClientProxyRunnable implements Runnable {
    private static String TAG = "ZJTest:CpRunnable";

    private Socket mSocket;

    private Handler mHandler;

    private boolean mQuit;

    private int mTimeOutCount = 0;

    private Context mContext;

    private Map<String, String> mPictureInfo;

    public ClientProxyRunnable(Socket socket, Handler handler, Context context) {
        mSocket = socket;

        try {
            mSocket.setKeepAlive(false);
            mSocket.setSoTimeout(5000);
        } catch (SocketException e) {
            Log.d(TAG, e.toString());
        }

        mHandler = handler;

        mContext = context;
    }

    @Override
    public void run() {
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
                    replySync();
                    break;

                case NetworkUtil.BEGIN_DOWN_LOAD:
                    Log.d(TAG, "receive download request");
                    replyPicNum();
                    break;

                case NetworkUtil.GET_THUMB_NAIL:
                    Log.d(TAG, "receive getThumbNail request");
                    transferDataToClient();
                    break;

                default:
                    Log.d(TAG, "invalid msg: " + msg);
            }

            break;
        }

        Log.d(TAG, "clientProxy finish");

        if (!mQuit) {
            quit();
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

    private void replySync() {
        try {
            PrintWriter printWriter = NetworkUtil
                    .getSocketPrintWriter(mSocket);

            sendMsgToService(Constants.ServerServiceMsg.STOP_BROADCAST);
            sendMsgToClient(printWriter, NetworkUtil.ACK);

            printWriter.close();
        } catch (IOException ioe) {
            Log.d(TAG, ioe.toString());
        }
    }

    private void replyPicNum() {
        try {
            PrintWriter printWriter = NetworkUtil
                    .getSocketPrintWriter(mSocket);

            mPictureInfo = FileUtil.getPictureInfoMap(mContext);
            sendMsgToClient(printWriter, "" + mPictureInfo.size());

            printWriter.close();
        } catch (IOException ioe) {
            Log.d(TAG, ioe.toString());
        }
    }

    private void sendMsgToService(int msgId) {
        mHandler.sendEmptyMessage(msgId);
    }

    private void sendMsgToClient(PrintWriter printWriter, String msg) {
        printWriter.print(msg);
        printWriter.flush();
    }

    private void transferDataToClient() {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(mSocket.getOutputStream());

            Set<String> pathList = mPictureInfo.keySet();

            for (String path : pathList) {
                byte[] data = FileUtil.getPictureThumbnail(path);

                if (data != null) {
                    dataOutputStream.writeInt(data.length);

                    dataOutputStream.write(data);

                    dataOutputStream.writeUTF(mPictureInfo.get(path));

                    dataOutputStream.flush();
                }
            }

            dataOutputStream.close();
        } catch (IOException ioe) {
            Log.d(TAG, ioe.toString());
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