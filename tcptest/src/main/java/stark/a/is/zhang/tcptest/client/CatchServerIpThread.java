package stark.a.is.zhang.tcptest.client;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import stark.a.is.zhang.tcptest.util.NetworkUtil;

class CatchServerIpThread extends Thread {
    private static final String TAG = "ZJTest:catchIp";

    private Handler mHandler;
    private MulticastSocket mSocket;

    CatchServerIpThread(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void run() {
        try {
            mSocket = new MulticastSocket(NetworkUtil.PORT);
            InetAddress address = InetAddress.getByName(NetworkUtil.LAN_ADDRESS);
            mSocket.joinGroup(address);

            byte[] buf = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf, 1024);

            mSocket.receive(dp);

            String data = new String(buf, 0 , dp.getLength());

            Log.d(TAG, "receive IP from server: " + data);

            mSocket.close();
            mSocket = null;

            mHandler.sendMessage(mHandler.obtainMessage(Constants.GET_SERVER_IP, data));
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    void quit() {
        if (mSocket != null) {
            mSocket.close();
        }
    }
}