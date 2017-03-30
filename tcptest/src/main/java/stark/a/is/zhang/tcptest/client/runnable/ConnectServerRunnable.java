package stark.a.is.zhang.tcptest.client.runnable;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

import stark.a.is.zhang.tcptest.client.Constants;
import stark.a.is.zhang.tcptest.util.NetworkUtil;

public class ConnectServerRunnable implements Runnable {
    private static String TAG = "ZJTest: ConRunnable";

    private Handler mHandler;
    private String mServerIp;

    public ConnectServerRunnable(Handler handler, String serverIp) {
        mHandler = handler;
        mServerIp = serverIp;
    }

    @Override
    public void run() {
        SocketAddress socketAddress = NetworkUtil.createTcpSocketAddress(mServerIp);

        if (socketAddress == null) {
            Log.d(TAG, "bad address: " + mServerIp);
            return;
        }

        Socket socket = null;
        PrintWriter printWriter = null;
        try {
            socket = new Socket();
            socket.setSoTimeout(50000);

            socket.connect(socketAddress);

            Log.d(TAG, "connect successful");

            printWriter = NetworkUtil.getSocketPrintWriter(socket);
            printWriter.print(NetworkUtil.SYNC);
            printWriter.flush();

            Log.d(TAG, "after send SYNC");

            String data = NetworkUtil.getStringFromSocket(socket);

            Log.d(TAG, "receive data: " + data);

            if (data != null && data.equals(NetworkUtil.ACK)) {
                mHandler.sendEmptyMessage(Constants.CONNECT_SUCCESSFUL);
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }
}
