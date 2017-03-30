package stark.a.is.zhang.tcptest.client.runnable;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import stark.a.is.zhang.tcptest.client.Constants;
import stark.a.is.zhang.tcptest.util.NetworkUtil;

public class CatchServerIpRunnable implements Runnable {
    private static final String TAG = "ZJTest:catchIp";

    private Handler mHandler;

    public CatchServerIpRunnable(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void run() {
        try {
            MulticastSocket socket = new MulticastSocket(NetworkUtil.PORT);

            InetAddress address = InetAddress.getByName(NetworkUtil.LAN_ADDRESS);
            socket.joinGroup(address);

            byte[] buf = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf, 1024);

            socket.receive(dp);

            String data = new String(buf, 0 , dp.getLength());

            Log.d(TAG, "receive IP from server: " + data);

            socket.close();

            mHandler.sendMessage(mHandler.obtainMessage(Constants.GET_SERVER_IP, data));
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }
}