package stark.a.is.zhang.zxingtest;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView mImageView;
    private Button mScanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button generate = (Button) findViewById(R.id.generate);
        generate.setOnClickListener(this);

        mScanButton = (Button) findViewById(R.id.scan);
        mScanButton.setOnClickListener(this);
        mScanButton.setEnabled(false);

        mImageView = (ImageView) findViewById(R.id.zxing_code_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.generate:
                generateQRCode();
                break;

            case R.id.scan:
                scanQRCode();
                break;
        }
    }

    private static final int QR_WIDTH = 1000;
    private static final int QR_HEIGHT = 1000;

    private void generateQRCode() {
        int[] pixels = generatePixels();

        if (pixels != null) {
            Bitmap bitmap = Bitmap.createBitmap(
                    QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

            mImageView.setImageBitmap(bitmap);

            mScanButton.setEnabled(true);
        }
    }

    private int[] generatePixels() {
        String data = createData();

        int[] pixels = null;

        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

        try {
            BitMatrix bitMatrix = new QRCodeWriter()
                    .encode(data, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);

            pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; ++y) {
                for (int x = 0; x < QR_WIDTH; ++x) {
                    pixels[y * QR_WIDTH + x] =
                            bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
        } catch (WriterException e) {
            Log.d("ZJTest", e.toString());
        }

        return pixels;
    }

    private String createData() {
        String data = null;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("author", "ZhangJianIsAStark");
            jsonObject.put("url", "http://blog.csdn.net/gaugamela");

            data = jsonObject.toString();
        } catch (JSONException e) {
            Log.d("ZJTest", e.toString());
        }

        return data;
    }

    //may change it later
    private void scanQRCode() {
        Bitmap bitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();

        Matrix matrix = new Matrix();
        matrix.postScale(0.2f, 0.2f);
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        int width = resizeBitmap.getWidth();
        int height = resizeBitmap.getHeight();

        int[] pixels = new int[width * height];
        resizeBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        RGBLuminanceSource rgbLuminanceSource =
                new RGBLuminanceSource(width, height, pixels);

        BinaryBitmap binaryBitmap = new BinaryBitmap(
                new HybridBinarizer(rgbLuminanceSource));

        Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");

        try {
            Result result = new MultiFormatReader().decode(binaryBitmap, hints);

            try {
                JSONObject jsonObject = new JSONObject(result.getText());
                String name = jsonObject.getString("author");
                String url = jsonObject.getString("url");
                Log.d("ZJTest", "name: " + name + ", url: " + url);
            } catch (JSONException e) {
                Log.d("ZJTest", e.toString());
            }
        } catch (NotFoundException e) {
            Log.d("ZJTest", e.toString());
            e.printStackTrace();
        }
    }
}