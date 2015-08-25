package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.component.material.progressbar.ProgressView;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;

public class StartActivity extends Activity {

    ButtonRectangle start_button;

    Activity activity;
    SharedPreferences.Editor editor;

//    ProgressView waitingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        activity = StartActivity.this;

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
//        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, StartActivity.class.getName());
//        editor.commit();

//        waitingProgress = (ProgressView)findViewById(R.id.waitingProgress);
//        waitingProgress.start();

        start_button = (ButtonRectangle)findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, PostStartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }

}
