package stark.a.is.zhang.tcptest.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.lang.ref.WeakReference;

import static stark.a.is.zhang.tcptest.server.Constants.ServerServiceMsg.*;

public class ServerService extends Service {
    private static String TAG = "ZJTest:ServerService";

    private Messenger mLocalMessenger;

    private LocalHandler mLocalHandler;

    private BroadcastServerIpThread mBroadcastServerIpThread;

    private Messenger mActivityMessenger;

    private ServerThread mServerThread;

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalHandler = new LocalHandler(this);
        mLocalMessenger = new Messenger(mLocalHandler);

        sendMulticastBroadcast();

        initTcpServer();
    }

    private void sendMulticastBroadcast() {
        mBroadcastServerIpThread = new BroadcastServerIpThread(this);
        mBroadcastServerIpThread.start();
    }

    private void stopMulticastBroadcast() {
        if (mBroadcastServerIpThread != null) {
            mBroadcastServerIpThread.quit();
            mBroadcastServerIpThread = null;
        }
    }

    private void initTcpServer() {
        mServerThread = new ServerThread(mLocalHandler);
        mServerThread.start();
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
            ServerService serverService = mServerService.get();

            switch (msg.what) {
                case SET_ACTIVITY_MESSENGER:
                    serverService.mActivityMessenger =  msg.replyTo;
                    break;

                case STOP_BROADCAST:
                    serverService.stopMulticastBroadcast();

                    Message reply = Message.obtain();
                    reply.what = Constants.ServerActivityMsg.CLIENT_CONNECT;

                    try {
                        serverService.mActivityMessenger.send(reply);
                    } catch (RemoteException e) {
                        Log.d(TAG, e.toString());
                    }
                    break;

                case QUIT:
                    serverService.stopMulticastBroadcast();
                    serverService.mServerThread.quit();
                    serverService.stopSelf();
                    break;
            }
        }
    }
}