package stark.a.is.zhang.tcptest.client;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import stark.a.is.zhang.tcptest.R;
import stark.a.is.zhang.tcptest.util.NetworkUtil;

public class ClientActivity extends AppCompatActivity implements View.OnClickListener{
    private LocalHandler mLocalHandler;

    private String mServerIp;

    private Button mDownLoadButton;
    private Button mUpLoadButton;

    private TransferProxy mTransferProxy;

    private CatchServerIpThread mCatchServerIpThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        findChildViews();
        configChildViews();

        mLocalHandler = new LocalHandler(this);

        tryCatchServerAddress();
    }

    private void findChildViews() {
        mDownLoadButton = (Button) findViewById(R.id.download_button);
        mUpLoadButton = (Button) findViewById(R.id.upload_button);
    }

    private void configChildViews() {
        mDownLoadButton.setOnClickListener(this);
        mUpLoadButton.setOnClickListener(this);
        toggleButton(false);
    }

    private void toggleButton(boolean enable) {
        mDownLoadButton.setEnabled(enable);
        mUpLoadButton.setEnabled(enable);
    }

    private void tryCatchServerAddress() {
        if (NetworkUtil.isWifiConnected(this)) {
            mCatchServerIpThread = new CatchServerIpThread(mLocalHandler);
            mCatchServerIpThread.start();
        } else {
            Toast.makeText(
                    this, "WiFi not connected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_button:

                break;

            case R.id.upload_button:

                break;
        }
    }

    private void tryToConnectServer() {
        if (NetworkUtil.isWifiConnected(this)) {
            mTransferProxy = new TransferProxy(mLocalHandler, mServerIp);
            mTransferProxy.init();
        } else {
            Toast.makeText(
                    this, "WiFi not connected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (mCatchServerIpThread != null) {
            mCatchServerIpThread.quit();
        }

        if (mTransferProxy != null) {
            mTransferProxy.quit();
        }

        super.onDestroy();
    }

    private static class LocalHandler extends Handler {
        private WeakReference<ClientActivity> mClientActivity;

        LocalHandler(ClientActivity clientActivity) {
            mClientActivity = new WeakReference<>(clientActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.GET_SERVER_IP:
                    mClientActivity.get().mServerIp = (String) msg.obj;
                    mClientActivity.get().tryToConnectServer();
                    break;
                case Constants.CONNECT_SUCCESSFUL:
                    mClientActivity.get().toggleButton(true);
                    break;
            }
        }
    }
}