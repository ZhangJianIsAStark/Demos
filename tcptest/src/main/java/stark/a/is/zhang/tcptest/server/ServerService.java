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

import stark.a.is.zhang.tcptest.server.runnable.BroadServerIpRunnable;
import stark.a.is.zhang.tcptest.server.runnable.ServerTransferProxy;

import static stark.a.is.zhang.tcptest.server.Constants.ServerServiceMsg.*;

public class ServerService extends Service {
    private static String TAG = "ZJTest:ServerService";

    private Messenger mLocalMessenger;

    private LocalHandler mLocalHandler;

    private BroadServerIpRunnable mBroadServerIpRunnable;

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
        mBroadServerIpRunnable = new BroadServerIpRunnable(this);
        ServerTransferProxy.getInstance().execute(mBroadServerIpRunnable);
    }

    private void stopMulticastBroadcast() {
        if (mBroadServerIpRunnable != null) {
            mBroadServerIpRunnable.quit();
            mBroadServerIpRunnable = null;
        }
    }

    private void initTcpServer() {
        mServerThread = new ServerThread(this, mLocalHandler);
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
                    serverService.reply(Constants.ServerActivityMsg.CLIENT_CONNECT);
                    break;

                case QUIT:
                    serverService.stopMulticastBroadcast();
                    serverService.dispose();
                    break;
            }
        }
    }

    private void reply(int msgId) {
        Message reply = Message.obtain();
        reply.what = msgId;

        try {
            mActivityMessenger.send(reply);
        } catch (RemoteException e) {
            Log.d(TAG, e.toString());
        }
    }

    private void dispose() {
        mServerThread.quit();

        if (ServerTransferProxy.isCreated()) {
            ServerTransferProxy.getInstance().dispose();
        }

        stopSelf();
    }
}