package stark.a.is.zhang.tcptest.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.util.HashMap;
import java.util.Map;

public class FileUtil {
    public static Map<String, String> getPictureInfoMap(Context context) {
        Map<String, String> infoMap = new HashMap<>();

        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (file != null && file.exists()) {
            getInfo(file, infoMap);
        }

        return infoMap;
    }

    private static void getInfo(File file, Map<String, String> infoMap) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();

                for (File child : files) {
                    getInfo(child, infoMap);
                }
            } else {
                if(file.getName().endsWith(".jpg")) {
                    infoMap.put(file.getAbsolutePath(), file.getName());
                }
            }
        }
    }

    public static byte[] getPictureThumbnail(String path) {
        Bitmap bitmap = getScaledBitmap(path, 100, 100);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);

        return outputStream.toByteArray();
    }

    private static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }
}