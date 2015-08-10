package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;

public class PostStartActivity extends Activity {


    ButtonRectangle keepOn_button;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_start);

        activity = PostStartActivity.this;

        keepOn_button = (ButtonRectangle)findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(PostStartActivity.this, ProfileEntryActivity.class);
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
