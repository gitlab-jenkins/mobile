package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;

public class PostRegVerifyAccountNoActivity extends ActionBarActivity {

    Activity activity;

    ButtonRectangle keepOn_button;

    RelativeLayout loading_rl;

    SharedPreferences prefs;

    public void contactUs(View view){
        new HamPayDialog(this).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_reg_verify_account);

        activity = PostRegVerifyAccountNoActivity.this;

        loading_rl = (RelativeLayout) findViewById(R.id.loading_rl);

        prefs = getPreferences(MODE_PRIVATE);


        keepOn_button = (ButtonRectangle) findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PostRegVerifyAccountNoActivity.this, PasswordEntryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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

