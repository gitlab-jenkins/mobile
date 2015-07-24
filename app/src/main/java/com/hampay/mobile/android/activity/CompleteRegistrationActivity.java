package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;

public class CompleteRegistrationActivity extends ActionBarActivity {

    ButtonRectangle hampay_login_button;

    Activity activity;

    SharedPreferences.Editor editor;

    public void contactUs(View view){
        (new HamPayDialog(this)).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_registration);

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.REGISTERED_USER, true);
        editor.commit();

        activity = CompleteRegistrationActivity.this;

        hampay_login_button = (ButtonRectangle)findViewById(R.id.hampay_login_button);
        hampay_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(CompleteRegistrationActivity.this, HamPayLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        new HamPayDialog(activity).showExitRegistrationDialog();
//    }

}
