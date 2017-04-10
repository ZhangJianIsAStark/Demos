package stark.a.is.zhang.tcptest.client;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import stark.a.is.zhang.tcptest.R;
import stark.a.is.zhang.tcptest.client.runnable.CatchServerIpRunnable;
import stark.a.is.zhang.tcptest.client.runnable.ConnectServerRunnable;
import stark.a.is.zhang.tcptest.client.runnable.ClientTransferProxy;
import stark.a.is.zhang.tcptest.util.NetworkUtil;

public class ClientActivity extends AppCompatActivity implements View.OnClickListener{
    private LocalHandler mLocalHandler;

    private String mServerIp;

    private Button mDownLoadButton;
    private Button mUpLoadButton;

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
            ClientTransferProxy.getInstance().execute(new CatchServerIpRunnable(mLocalHandler));
        } else {
            Toast.makeText(
                    this, "WiFi not connected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_button:
                Intent intent = new Intent(this, DownloadActivity.class);
                intent.putExtra(Constants.SERVER_IP, mServerIp);
                startActivity(intent);
                break;

            case R.id.upload_button:
                //wait
                break;
        }
    }

    private void tryToConnectServer() {
        if (NetworkUtil.isWifiConnected(this)) {
            ClientTransferProxy.getInstance()
                    .execute(new ConnectServerRunnable(mLocalHandler, mServerIp));
        } else {
            Toast.makeText(
                    this, "WiFi not connected", Toast.LENGTH_LONG).show();
        }
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

    @Override
    protected void onDestroy() {
        if (ClientTransferProxy.isCreated()) {
            ClientTransferProxy.getInstance().dispose();
        }

        super.onDestroy();
    }
}