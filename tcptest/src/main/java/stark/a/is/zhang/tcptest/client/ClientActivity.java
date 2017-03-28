package stark.a.is.zhang.tcptest.client;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketAddress;

import stark.a.is.zhang.tcptest.R;
import stark.a.is.zhang.tcptest.util.NetworkUtil;

public class ClientActivity extends AppCompatActivity {
    private LocalHandler mLocalHandler;

    private String mServerIp;

    private static final int GET_SERVER_IP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        mLocalHandler = new LocalHandler(this);
        tryCatchServerAddress();
    }

    private static class LocalHandler extends Handler {
        private WeakReference<ClientActivity> mClientActivity;

        LocalHandler(ClientActivity clientActivity) {
            mClientActivity = new WeakReference<>(clientActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_SERVER_IP:
                    mClientActivity.get().mServerIp = (String) msg.obj;
                    mClientActivity.get().tryToConnectServer();
                    break;
            }
        }
    }

    private void tryCatchServerAddress() {
        if (NetworkUtil.isWifiConnected(this)) {
            new CatchIpThread(mLocalHandler).start();
        } else {
            Log.d("ZJTest", "wifi not connected");
        }
    }

    private class CatchIpThread extends Thread {
        private Handler mHandler;

        CatchIpThread(Handler handler) {
            mHandler = handler;
        }

        @Override
        public void run() {
            try {
                MulticastSocket socket = new MulticastSocket(NetworkUtil.PORT);
                InetAddress address = InetAddress.getByName("224.0.0.1");
                socket.joinGroup(address);

                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, 1024);
                socket.receive(dp);
                String data = new String(buf, 0 , dp.getLength());

                socket.close();

                mHandler.sendMessage(mHandler.obtainMessage(GET_SERVER_IP, data));
            } catch (IOException e) {
                Log.d("ZJTest", e.toString());
            }
        }
    }

    private void tryToConnectServer() {
        if (NetworkUtil.isWifiConnected(this)) {
            new LocalThread().start();
        } else {
            Log.d("ZJTest", "wifi not connected");
        }
    }

    private class LocalThread extends Thread {
        @Override
        public void run() {
            Socket socket = new Socket();

            try {
                InetAddress inetAddress = InetAddress.getByName(mServerIp);
                SocketAddress socketAddress = new InetSocketAddress(
                        inetAddress, NetworkUtil.PORT);

                socket.connect(socketAddress);

                OutputStreamWriter outputStreamWriter =
                        new OutputStreamWriter(socket.getOutputStream());

                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                PrintWriter printWriter = new PrintWriter(bufferedWriter);
                printWriter.print(NetworkUtil.SYC);
                printWriter.flush();
                printWriter.close();

                socket.close();
            } catch (IOException e) {
                Log.d("ZJTest", e.toString());
            }
        }
    }
}