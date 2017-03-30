package stark.a.is.zhang.tcptest.model;

import android.graphics.Bitmap;

public class ViewModel {
    private Bitmap mBitmap;
    private String mTitle;
    private boolean mIsChecked;

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }
}
