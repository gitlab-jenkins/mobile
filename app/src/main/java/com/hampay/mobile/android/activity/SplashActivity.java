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
