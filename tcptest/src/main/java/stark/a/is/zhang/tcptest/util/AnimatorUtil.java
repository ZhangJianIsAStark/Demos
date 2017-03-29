package stark.a.is.zhang.tcptest.util;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

public class AnimatorUtil {
    public static ObjectAnimator createClickAnimator(View view) {
        PropertyValuesHolder pvhX =
                PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.8f, 1.0f);

        PropertyValuesHolder pvhY =
                PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.8f, 1.0f);

        return ObjectAnimator
                .ofPropertyValuesHolder(view, pvhX, pvhY)
                .setDuration(100);
    }
}