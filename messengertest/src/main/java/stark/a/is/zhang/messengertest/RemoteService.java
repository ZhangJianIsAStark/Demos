package stark.a.is.zhang.messengertest;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.util.Log;

public class RemoteService extends Service {
    private Messenger mMessenger;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ZJTest", "Service, pid: " + Process.myPid()
                + ", name: " + Util.getProcessName(this));
        LocalHandler mHandler = new LocalHandler();
        mMessenger = new Messenger(mHandler);
    }

    private static class  LocalHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Log.d("ZJTest", "receive msg: " + msg.what);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
