package stark.a.is.zhang.volleytest;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherModel implements Parcelable{
    String mProvinceName;
    String mCityName;
    String mStateDetailed;
    String mTemperature;
    String mWindState;

    public WeatherModel() {
    }

    public WeatherModel(Parcel in) {
        mProvinceName = in.readString();
        mCityName = in.readString();
        mStateDetailed = in.readString();
        mTemperature = in.readString();
        mWindState = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mProvinceName);
        dest.writeString(mCityName);
        dest.writeString(mStateDetailed);
        dest.writeString(mTemperature);
        dest.writeString(mWindState);
    }

    public static final Parcelable.Creator<WeatherModel> CREATOR =
            new Parcelable.Creator<WeatherModel> () {
                @Override
                public WeatherModel createFromParcel(Parcel source) {
                    return new WeatherModel(source);
                }

                @Override
                public WeatherModel[] newArray(int size) {
                    return new WeatherModel[size];
                }
            };

    @Override
    public String toString() {
        return mProvinceName + ", " + mCityName + ", "
                + mStateDetailed + ", " + mTemperature + ", " + mWindState;
    }
}
