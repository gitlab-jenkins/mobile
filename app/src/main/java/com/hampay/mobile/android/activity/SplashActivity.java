package com.hampay.mobile.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.hampay.mobile.android.R;

public class SplashActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(2 * 1000);

                    Intent i=new Intent(getBaseContext(), IntroActivity.class);
                    startActivity(i);

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
