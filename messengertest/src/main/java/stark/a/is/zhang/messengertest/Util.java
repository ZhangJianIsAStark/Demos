package stark.a.is.zhang.messengertest;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

class Util {
    static String getProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcessInfo:
                am.getRunningAppProcesses()) {
            if (appProcessInfo.pid == pid) {
                return appProcessInfo.processName;
            }
        }

        return null;
    }
}
