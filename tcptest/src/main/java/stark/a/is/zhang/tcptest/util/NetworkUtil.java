package stark.a.is.zhang.tcptest.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

public class NetworkUtil {
    public static final int PORT = 8848;
    public static final String SYC = "sync";
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
}
