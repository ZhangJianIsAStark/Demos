package stark.a.is.zhang.tcptest.server;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import stark.a.is.zhang.tcptest.util.NetworkUtil;

class ClientProxy implements Runnable {
    private Socket mSocket;
    private Handler mHandler;

    ClientProxy(Socket socket, Handler handler) {
        mSocket = socket;
        mHandler = handler;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = mSocket.getInputStream();

            byte[] buffer = new byte[1024];
            int count;

            while ((count = inputStream.read(buffer, 0, 1024)) != -1) {
                String tmp = new String(buffer, 0, count);

                if (tmp.equals(NetworkUtil.SYC)) {
                    mHandler.sendEmptyMessage(
                            Constants.ServerServiceMsg.STOP_BROADCAST);
                }
            }

            mSocket.close();
        } catch (IOException e) {
            Log.d("ZJTest", e.toString());
        }
    }
}
