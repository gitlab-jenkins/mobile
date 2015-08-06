package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.component.material.progressbar.ProgressView;
import com.hampay.mobile.android.dialog.HamPayDialog;

public class StartActivity extends ActionBarActivity {

    ButtonRectangle start_button;

    Activity activity;

//    ProgressView waitingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        activity = StartActivity.this;

//        waitingProgress = (ProgressView)findViewById(R.id.waitingProgress);
//        waitingProgress.start();

        start_button = (ButtonRectangle)findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.setClass(StartActivity.this, PostStartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }

}
