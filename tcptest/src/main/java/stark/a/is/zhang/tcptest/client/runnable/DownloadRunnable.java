package stark.a.is.zhang.tcptest.client.runnable;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import stark.a.is.zhang.tcptest.client.Constants;
import stark.a.is.zhang.tcptest.util.NetworkUtil;

public class DownloadRunnable implements Runnable {
    private static String TAG = "ZJTest: downRunnable";

    private Handler mHandler;
    private String mServerIp;

    public DownloadRunnable(Handler handler, String serverIp) {
        mHandler = handler;
        mServerIp = serverIp;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        PrintWriter printWriter = null;
        try {
            socket.connect(NetworkUtil.createTcpSocketAddress(mServerIp));

            printWriter = NetworkUtil.getSocketPrintWriter(socket);

            printWriter.write(NetworkUtil.BEGIN_DOWN_LOAD);
            printWriter.flush();

            String pictureSize = NetworkUtil.getStringFromSocket(socket);
            int size = Integer.parseInt(pictureSize);

            if (size > 0) {
                Message msg = Message.obtain();
                msg.what = Constants.GET_PICTURE_SIZE;
                msg.obj = size;
                mHandler.sendMessage(msg);

                downloadThumbnail(printWriter, socket);
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }

            try {
                socket.close();
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    private void downloadThumbnail(PrintWriter printWriter, Socket socket) {
        printWriter.write(NetworkUtil.GET_THUMB_NAIL);
        printWriter.flush();

        //wait to be continued
    }
}
