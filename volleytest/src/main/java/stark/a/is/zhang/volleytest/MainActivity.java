package stark.a.is.zhang.volleytest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{
    RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        findChildView();
        configChildView();
    }

    Button mStringRequestButton;
    Button mJsonRequestButton;
    Button mImageRequestButton;
    Button mImageLoaderButton;
    Button mNetworkImageViewButton;
    Button mCustomizeButton;

    TextView mTextView;
    ImageView mImageView;
    NetworkImageView mNetworkImageView;

    private void findChildView() {
        mStringRequestButton = (Button) findViewById(R.id.string_request_button);
        mJsonRequestButton = (Button) findViewById(R.id.json_request_button);
        mImageRequestButton = (Button) findViewById(R.id.image_request_button);
        mImageLoaderButton = (Button) findViewById(R.id.image_loader_button);
        mNetworkImageViewButton = (Button) findViewById(R.id.network_image_view_button);
        mCustomizeButton = (Button) findViewById(R.id.custom_button);

        mTextView = (TextView) findViewById(R.id.result_text);
        mImageView = (ImageView) findViewById(R.id.image_result);
        mNetworkImageView = (NetworkImageView) findViewById(R.id.network_image_view);
    }

    private void configChildView() {
        mStringRequestButton.setOnClickListener(this);
        mJsonRequestButton.setOnClickListener(this);
        mImageRequestButton.setOnClickListener(this);
        mImageLoaderButton.setOnClickListener(this);
        mNetworkImageViewButton.setOnClickListener(this);
        mCustomizeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.string_request_button:
                makeStringRequest();
                break;

            case R.id.json_request_button:
                makeJsonRequest();
                break;

            case R.id.image_request_button:
                makeImageRequest();
                break;

            case R.id.image_loader_button:
                loadImage();
                break;

            case R.id.network_image_view_button:
                refreshNetworkImageView();
                break;

            case R.id.custom_button:
                makeXmlRequest();
                break;
        }
    }

    private void makeStringRequest() {
        StringRequest stringRequest = new StringRequest(
                "https://www.baidu.com/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mTextView.setText(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText(error.toString());
                    }
                });
        mRequestQueue.add(stringRequest);
    }

    private void makeJsonRequest() {
        String url = Uri.parse("http://image.baidu.com/search/index?")
                .buildUpon()
                .appendQueryParameter("tn", "resultjson")
                .appendQueryParameter("word", "微距摄影")
                .build().toString();

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                url, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        mTextView.setText(response.toString());
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText(error.toString());
                    }
                });
        mRequestQueue.add(objectRequest);
    }

    private void makeImageRequest() {
        String url = "http://img3.imgtn.bdimg.com/it/u=1382485185,1958087964&fm=23&gp=0.jpg";

        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        mImageView.setImageBitmap(response);
                    }
                }, 200, 200, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mImageView.setImageResource(R.mipmap.ic_launcher);
                    }
                }
        );
        mRequestQueue.add(imageRequest);
    }

    private LocalImageCache mLocalImageCache = new LocalImageCache();
    private ImageLoader mImageLoader;

    private void loadImage() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue, mLocalImageCache);
        }

        ImageLoader.ImageListener imageListener = ImageLoader
                .getImageListener(mImageView, R.mipmap.ic_launcher, R.mipmap.ic_launcher);

        String url = "http://img1.imgtn.bdimg.com/it/u=1245538184,752165177&fm=23&gp=0.jpg";
        mImageLoader.get(url, imageListener, 200, 200);
    }

    private class LocalImageCache implements ImageLoader.ImageCache {
        private LruCache<String, Bitmap> mCache;

        LocalImageCache() {
            int maxSize = 10 * 1024 * 1024;

            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                     return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }
    }

    private void refreshNetworkImageView() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue, mLocalImageCache);
        }

        mNetworkImageView.setDefaultImageResId(R.mipmap.ic_launcher);
        mNetworkImageView.setErrorImageResId(R.mipmap.ic_launcher);
        mNetworkImageView.setImageUrl("", mImageLoader);
        mNetworkImageView.setImageUrl("http://img1.imgtn.bdimg.com/it/u=494242567,3557760312&fm=11&gp=0.jpg",
                mImageLoader);
    }

    private void makeXmlRequest() {
        XMLRequest xmlRequest = new XMLRequest(
                "http://flash.weather.com.cn/wmaps/xml/china.xml",
                new Response.Listener<XmlPullParser>() {
                    @Override
                    public void onResponse(XmlPullParser response) {
                        Intent intent = CustomActivity.getIntent(
                                getApplicationContext(), getWeatherInfo(response));
                        getApplicationContext().startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });

        mRequestQueue.add(xmlRequest);
    }

    private ArrayList<WeatherModel> getWeatherInfo(XmlPullParser xmlPullParser) {
        ArrayList<WeatherModel> rst = new ArrayList<>();
        WeatherModel weatherModel;

        try {
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("city")) {
                            weatherModel = new WeatherModel();

                            weatherModel.mProvinceName = xmlPullParser.getAttributeValue(0);
                            weatherModel.mCityName = xmlPullParser.getAttributeValue(2);
                            weatherModel.mStateDetailed = xmlPullParser.getAttributeValue(5);
                            weatherModel.mTemperature = xmlPullParser.getAttributeValue(7)
                                    + "~" + xmlPullParser.getAttributeValue(6);

                            weatherModel.mWindState = xmlPullParser.getAttributeValue(8);
                            rst.add(weatherModel);
                        }
                        break;
                }

                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rst;
    }
}