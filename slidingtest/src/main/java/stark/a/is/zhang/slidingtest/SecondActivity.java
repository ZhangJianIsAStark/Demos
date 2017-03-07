package stark.a.is.zhang.slidingtest;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SecondActivity extends AppCompatActivity {
    GestureDetectorCompat mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mGestureDetector = new GestureDetectorCompat(
                this, new LocalGestureListener());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private static final float MIN_MOVE_INSTANCE = 200;
    private static final float SPEED_MIN = 200;

    private class LocalGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float move = e2.getX() - e1.getX();
            if (move > MIN_MOVE_INSTANCE && velocityX > SPEED_MIN) {
                Log.d("ZJTest", "go to the first activity");
                finish();
                overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_to_right);
            }

            return true;
        }
    }
}