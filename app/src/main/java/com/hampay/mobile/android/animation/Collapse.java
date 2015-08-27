package com.hampay.mobile.android.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by amir on 8/27/15.
 */
public class Collapse {

    private View view;
    
    public Collapse(View view){
        this.view = view;
    }
    
    public void animate(){
        final int initialHeight = view.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    view.setVisibility(View.GONE);
                }else{
                    view.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };


        a.setDuration((int)(initialHeight / view.getContext().getResources().getDisplayMetrics().density * 2));
        view.startAnimation(a);
    }
    
}
