package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hampay.mobile.android.R;

public class SplashActivity extends Activity {

//    private void moveViewToScreenCenter( View view )
//    {
//        RelativeLayout root = (RelativeLayout) findViewById( R.id.rootLayout );
//        DisplayMetrics dm = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics( dm );
//        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();
//
//        int originalPos[] = new int[2];
//        view.getLocationOnScreen( originalPos );
//
//        int xDest = dm.widthPixels/2;
//        xDest -= (view.getMeasuredWidth()/2);
//        int yDest = dm.heightPixels/2 - (view.getMeasuredHeight()/2) - statusBarOffset;
//
//        TranslateAnimation anim = new TranslateAnimation( 0, xDest - originalPos[0] , 0, yDest - originalPos[1] );
//        anim.setDuration(1000);
//        anim.setFillAfter( true );
//        view.startAnimation(anim);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Animation a = AnimationUtils.loadAnimation(this, R.anim.splash_logo);
        ((LinearLayout) findViewById(R.id.logoImage)).startAnimation(a);

        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(3 * 1500);

                    Intent intent = new Intent(getBaseContext(), IntroSliderActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_left_right_transition, R.anim.activity_right_left_transition);

                    finish();

                } catch (Exception e) {

                }
            }
        };
        background.start();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

}
