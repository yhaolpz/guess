package com.example.asus.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by yhao on 2016/12/31.
 *
 */

public class AnimUtil {

    public static void playScaleAnim(View view) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f, 1f).setDuration(100);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f, 1f).setDuration(100);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();
    }
}
