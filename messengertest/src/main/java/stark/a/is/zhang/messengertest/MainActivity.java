package stark.a.is.zhang.messengertest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("ZJTest", "Activity, pid: " + Process.myPid() +
                ", name: " + Util.getProcessName(this));

        startService();
        bindService();

        mButton = (Button) findViewById(R.id.test_button);
        mButton.setEnabled(false);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMessenger != null) {
                    try {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        mMessenger.send(msg);
                    } catch (RemoteException e) {
                        Log.d("ZJTest", e.toString());
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unBindService();
        stopService();
    }

    private Intent mIntent;
    private void startService() {
        mIntent = new Intent(this, RemoteService.class);
        this.startService(mIntent);
    }

    private ServiceConnection mServiceConnection;
    private void bindService() {
        mServiceConnection = new LocalServiceConnection();
        this.bindService(mIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    Messenger mMessenger;
    private class LocalServiceConnection implements android.content.ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            mButton.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mButton.setEnabled(false);
        }
    }

    private void stopService() {
        stopService(mIntent);
    }

    private void unBindService() {
        unbindService(mServiceConnection);
    }
}
