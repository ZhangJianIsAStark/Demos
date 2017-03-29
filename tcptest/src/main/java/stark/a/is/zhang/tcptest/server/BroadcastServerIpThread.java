package stark.a.is.zhang.tcptest.server;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import stark.a.is.zhang.tcptest.util.NetworkUtil;

import static stark.a.is.zhang.tcptest.util.NetworkUtil.PORT;

class BroadcastServerIpThread extends Thread {
    private static String TAG = "ZJTest:BroadIp";

    private Context mContext;
    private boolean mQuit;

    BroadcastServerIpThread(Context context) {
        mContext = context;
    }

    void quit() {
        mQuit = true;
    }

    @Override
    public void run() {
        MulticastSocket mMulticastSocket = null;

        try {
            mMulticastSocket = new MulticastSocket();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }

        if (mMulticastSocket != null) {
            try {
                String ip = NetworkUtil.getWifiIp(mContext);
                byte[] data = ip.getBytes();

                InetAddress address = InetAddress.getByName(NetworkUtil.LAN_ADDRESS);

                DatagramPacket datagramPacket =
                        new DatagramPacket(data, data.length, address, PORT);

                while (!mQuit) {
                    Log.d(TAG, "broadcasting");
                    mMulticastSocket.send(datagramPacket);
                    Thread.sleep(2000);
                }

                Log.d(TAG, "finish sending broadcast");

                mMulticastSocket.close();
            } catch (IOException | InterruptedException e) {
                Log.d(TAG, e.toString());
            }
        }
    }
}
