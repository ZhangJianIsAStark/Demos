package stark.a.is.zhang.tcptest.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkUtil {
    public static String TAG = "ZJTest: NetworkUtil";

    public static final int PORT = 8848;

    public static final String SYNC = "sync";

    public static final String ACK = "ack";

    public static final String LAN_ADDRESS = "224.0.0.1";

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo;

        if (Build.VERSION.SDK_INT >= 23) {
            Network network = cm.getActiveNetwork();
            networkInfo = cm.getNetworkInfo(network);
        } else {
            networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }

        return (networkInfo != null) &&
                (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                && networkInfo.isConnected();
    }

    public static String getWifiIp(Context context) {
        WifiManager wm = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wm.getConnectionInfo();

        int ipAddress = wifiInfo.getIpAddress();

        return int2IpString(ipAddress);
    }

    private static String int2IpString(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF)
                + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    public static PrintWriter getSocketPrintWriter(Socket socket) throws IOException {
        OutputStreamWriter outputStreamWriter =
                new OutputStreamWriter(socket.getOutputStream());

        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        return new PrintWriter(bufferedWriter);
    }

    public static String getStringFromSocket(Socket socket) {
        String rst = null;

        try {
            InputStream dataInputStream = socket.getInputStream();

            byte[] buffer = new byte[1024];
            int len = dataInputStream.read(buffer, 0, 1024);

            if (len > 0) {
                rst = new String(buffer, 0, len);
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }

        return rst;
    }
}
