package com.webnav.matth.geomap;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

import static com.google.android.gms.analytics.internal.zzy.v;

/**
 * Created by matth on 28/8/2017.
 */

public class ExpandAnimation{
    private View mAnimatedView;
    private int duration;
    private int targetHeight;
    private boolean isExpanded = false;

    public ExpandAnimation(View view, int duration, int targetHeight) {
        this.mAnimatedView = view;
        this.duration = duration;
        this.targetHeight = targetHeight;
    }

    public void expandAnimate() {
        if (isExpanded) {
            collapse();
        } else {
            expand();
        }
    }

    public void expand() {

        int prevHeight  = mAnimatedView.getHeight();

        mAnimatedView.setVisibility(View.VISIBLE);
        this.isExpanded = true;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatedView.getLayoutParams().height = (int) animation.getAnimatedValue();
                mAnimatedView.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void collapse() {
        int prevHeight  = mAnimatedView.getHeight();
        this.isExpanded = false;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatedView.getLayoutParams().height = (int) animation.getAnimatedValue();
                mAnimatedView.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }
}
