package stark.a.is.zhang.slidingtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class FirstActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }

    private static final float MIN_MOVE_INSTANCE = 200;
    private static final float SPEED_MIN = 200;

    private float mOrigin;
    private boolean mAlreadyJump = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        addEventToVelocityTracker(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mOrigin = event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                float move = event.getX();
                float speed = getScrollVelocity();

                if ((mOrigin - move > MIN_MOVE_INSTANCE)
                        && (speed > SPEED_MIN) && !mAlreadyJump){
                    Log.d("ZJTest", "go to second activity");
                    startActivity(new Intent(this, SecondActivity.class));
                    overridePendingTransition(R.anim.in_from_right,
                            R.anim.out_to_left);
                    mAlreadyJump = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                mAlreadyJump = false;
                break;

            default:
                return super.onTouchEvent(event);
        }

        return true;
    }

    private VelocityTracker mVelocityTracker;

    private void addEventToVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    private float getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        return Math.abs(mVelocityTracker.getXVelocity());
    }
}
