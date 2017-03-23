package stark.a.is.zhang.volleytest;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class CustomActivity extends AppCompatActivity {
    static Intent getIntent(Context context, ArrayList<WeatherModel> data) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("weatherInfo", data);

        Intent intent = new Intent(context, CustomActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        initData();
        configChildView();
    }

    private ArrayList<WeatherModel> mWeatherModelList;

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mWeatherModelList = bundle.getParcelableArrayList("weatherInfo");
    }

    RecyclerView mRecyclerView;

    private void configChildView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.custom_recycler_view);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new WeatherHolderAdapter());
    }

    private class WeatherHolder extends RecyclerView.ViewHolder {
        private TextView mProvince;
        private TextView mCity;
        private TextView mDetail;
        private TextView mWind;
        private TextView mTemperature;

        WeatherHolder(View itemView) {
            super(itemView);

            mProvince = (TextView) itemView.findViewById(R.id.province);
            mCity = (TextView) itemView.findViewById(R.id.city);
            mDetail = (TextView) itemView.findViewById(R.id.detail);
            mWind = (TextView) itemView.findViewById(R.id.wind);
            mTemperature = (TextView) itemView.findViewById(R.id.temperature);
        }

        void bindWeatherModel(WeatherModel weatherModel) {
            mProvince.setText(weatherModel.mProvinceName);
            mCity.setText(weatherModel.mCityName);
            mDetail.setText(weatherModel.mStateDetailed);
            mWind.setText(weatherModel.mWindState);
            mTemperature.setText(weatherModel.mTemperature);
        }
    }

    private class WeatherHolderAdapter extends RecyclerView.Adapter<WeatherHolder> {
        @Override
        public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_weather_model, parent, false);
            return new WeatherHolder(view);
        }

        @Override
        public void onBindViewHolder(WeatherHolder holder, int position) {
            holder.bindWeatherModel(mWeatherModelList.get(position));
        }

        @Override
        public int getItemCount() {
            return mWeatherModelList.size();
        }
    }
}
