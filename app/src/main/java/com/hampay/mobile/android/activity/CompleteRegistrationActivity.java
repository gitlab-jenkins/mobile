package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;

public class CompleteRegistrationActivity extends ActionBarActivity {

    ButtonRectangle hampay_login_button;

    ImageView step_circle;

    Activity activity;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public void contactUs(View view){
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/reg-com.html");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_complete_registration);


        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.REGISTERED_USER, true);
        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, "");
        editor.commit();

        step_circle = (ImageView)findViewById(R.id.step_circle);

        if (prefs.getBoolean(Constants.VERIFIED_USER, false)){
            step_circle.setImageResource(R.drawable.step_circle_g_6);
        }else {
            step_circle.setImageResource(R.drawable.step_circle_b_6);
        }

        activity = CompleteRegistrationActivity.this;

        hampay_login_button = (ButtonRectangle)findViewById(R.id.hampay_login_button);
        hampay_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(CompleteRegistrationActivity.this, HamPayLoginActivity.class);
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
