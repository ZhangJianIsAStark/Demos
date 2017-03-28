package stark.a.is.zhang.tcptest.server;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import stark.a.is.zhang.tcptest.R;
import stark.a.is.zhang.tcptest.util.NetworkUtil;

public class ServerActivity extends AppCompatActivity {
    private Messenger mLocalMessenger;

    private BroadcastReceiver mBroadcastReceiver;

    private boolean mHasStartService = false;

    private Messenger mServiceMessenger;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        mLocalMessenger = new Messenger(new LocalHandler(this));

        mTextView = (TextView) findViewById(R.id.log_text);

        registerBroadcastReceiver();

        startServiceIfWifiConnected();
    }

    private static class LocalHandler extends Handler {
        private WeakReference<ServerActivity> mServerActivity;

        LocalHandler(ServerActivity serverActivity) {
            mServerActivity = new WeakReference<>(serverActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ServerActivityMsg.CLIENT_CONNECT:
                    mServerActivity.get().appendLog("client connect...");
                    break;
            }
        }
    }

    private void appendLog(String str) {
        mTextView.append(str);
    }

    private void registerBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int wifiState = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_DISABLED);

                if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                    startServiceIfWifiConnected();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

        this.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void startServiceIfWifiConnected() {
        if (NetworkUtil.isWifiConnected(this)) {
            startAndBindService();
        } else {
            Toast.makeText(this,
                    "Please connect the Wifi!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void startAndBindService() {
        if (!mHasStartService) {
            Intent intent = new Intent(this, ServerService.class);

            startService(intent);
            bindService(intent, new LocalServiceConnection(), BIND_AUTO_CREATE);

            mHasStartService = true;
        }
    }

    private class LocalServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceMessenger = new Messenger(service);

            sendMsgToService(
                    Constants.ServerServiceMsg.SET_ACTIVITY_MESSENGER,
                    mLocalMessenger);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceMessenger = null;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterBroadcastReceiver();

        sendMsgToService(Constants.ServerServiceMsg.QUIT, null);

        super.onDestroy();
    }

    private void sendMsgToService(int msgId, Messenger replyTo) {
        Message msg = Message.obtain();
        msg.what = msgId;
        msg.replyTo = replyTo;

        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            Log.d("ZJTest", e.toString());
        }
    }

    public void unregisterBroadcastReceiver() {
        if (mBroadcastReceiver != null) {
            this.unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
    }
}