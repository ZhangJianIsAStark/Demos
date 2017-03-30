package stark.a.is.zhang.tcptest.client.runnable;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import stark.a.is.zhang.tcptest.client.Constants;
import stark.a.is.zhang.tcptest.util.NetworkUtil;

public class ConnectRunnable implements Runnable {
    private static String TAG = "ZJTest: ConRunnable";

    private Handler mHandler;
    private String mServerIp;

    public ConnectRunnable(Handler handler, String serverIp) {
        mHandler = handler;
        mServerIp = serverIp;
    }

    @Override
    public void run() {
        Socket socket = new Socket();

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
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }
}
