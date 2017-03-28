package stark.a.is.zhang.tcptest.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import stark.a.is.zhang.tcptest.util.NetworkUtil;

import static stark.a.is.zhang.tcptest.server.Constants.ServerServiceMsg.*;
import static stark.a.is.zhang.tcptest.util.NetworkUtil.PORT;

public class ServerService extends Service {
    private ExecutorService mExecutorService;

    private Messenger mLocalMessenger;

    private LocalHandler mLocalHandler;

    private MulticastSocket mMulticastSocket;

    private boolean mStopBroadcast = false;

    private ServerSocket mServerSocket;

    private Messenger mActivityMessenger;

    private boolean mQuit = false;

    public ServerService() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalHandler = new LocalHandler(this);
        mLocalMessenger = new Messenger(mLocalHandler);

        sendMulticastBroadcast();

        initTcpServer();
    }

    private void sendMulticastBroadcast() {
        try {
            mMulticastSocket = new MulticastSocket();
        } catch (IOException e) {
            Log.d("ZJTest", e.toString());
        }

        if (mMulticastSocket != null) {
            broadcastIpInfo();
        }
    }

    private void broadcastIpInfo() {
        new BroadcastThread().start();
    }

    private class BroadcastThread extends Thread {
        @Override
        public void run() {
            try {
                String ip = NetworkUtil.getWifiIp(getApplicationContext());
                byte[] data = ip.getBytes();

                InetAddress address = InetAddress.getByName(NetworkUtil.LAN_ADDRESS);

                DatagramPacket datagramPacket =
                        new DatagramPacket(data, data.length, address, PORT);

                while (!mStopBroadcast) {
                    mMulticastSocket.send(datagramPacket);
                    Thread.sleep(2000);
                }

                mMulticastSocket.close();
            } catch (IOException e) {
                Log.d("ZJTest", e.toString());
            } catch (InterruptedException e) {
                Log.d("ZJTest", e.toString());
            }
        }
    }

    private void initTcpServer() {
        try {
            mServerSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            Log.d("ZJTest", e.toString());
        }

        if (mServerSocket != null) {
            ServiceThread serviceThread = new ServiceThread();
            serviceThread.start();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mLocalMessenger.getBinder();
    }

    private static class LocalHandler extends Handler {
        private WeakReference<ServerService> mServerService;

        LocalHandler(ServerService serverService) {
            mServerService = new WeakReference<>(serverService);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_ACTIVITY_MESSENGER:
                    mServerService.get().mActivityMessenger =  msg.replyTo;
                    break;

                case STOP_BROADCAST:
                    mServerService.get().mStopBroadcast = true;

                    Message reply = Message.obtain();
                    reply.what = Constants.ServerActivityMsg.CLIENT_CONNECT;

                    try {
                        mServerService.get().mActivityMessenger.send(reply);
                    } catch (RemoteException e) {
                        Log.d("ZJTest", e.toString());
                    }
                    break;

                case QUIT:
                    mServerService.get().mQuit = true;
                    break;
            }
        }
    }

    private class ServiceThread extends Thread {
        @Override
        public void run() {
            while (!mQuit) {
                try {
                    Socket client = mServerSocket.accept();
                    mExecutorService.execute(new ClientProxy(client, mLocalHandler));
                } catch (IOException e) {
                    Log.d("ZJTest", e.toString());
                }
            }
        }
    }
}