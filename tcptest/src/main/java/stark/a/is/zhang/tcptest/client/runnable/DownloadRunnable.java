package stark.a.is.zhang.tcptest.client.runnable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import stark.a.is.zhang.tcptest.client.Constants;
import stark.a.is.zhang.tcptest.model.ViewModel;
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
                downloadThumbnail(printWriter, socket, size);
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

    private void downloadThumbnail(PrintWriter printWriter, Socket socket, int num) {
        printWriter.write(NetworkUtil.GET_THUMB_NAIL);
        printWriter.flush();

        for (int i = 0; i < num; ++i) {
            try {
                DataInputStream is = new DataInputStream(socket.getInputStream());

                int size = is.readInt();
                byte[] buffer = new byte[size];

                int len = 0;
                while (len < size) {
                    len += is.read(buffer, len, size - len);
                }

                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

                String name = is.readUTF();

                ViewModel viewModel = new ViewModel();
                viewModel.setBitmap(bitmap);
                viewModel.setTitle(name);

                Message msg = mHandler.obtainMessage(Constants.ADD_VIEW_MODEL, viewModel);
                mHandler.sendMessage(msg);
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }
        }

        mHandler.sendEmptyMessage(Constants.ADD_VIEW_MODEL_DOWN);
    }
}
