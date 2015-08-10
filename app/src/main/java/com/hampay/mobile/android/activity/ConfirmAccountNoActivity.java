package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;

public class ConfirmAccountNoActivity extends Activity {

    ButtonRectangle keepOn_button;

    SharedPreferences.Editor editor;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_account_no);

        activity = ConfirmAccountNoActivity.this;

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, ConfirmAccountNoActivity.class.toString());
        editor.commit();

        keepOn_button = (ButtonRectangle)findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ConfirmAccountNoActivity.this, ConfirmInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);

            }
        });
    }


    public void contactUs(View view){
//        new HamPayDialog(this).showContactUsDialog();
        new HamPayDialog(this).showHelpDialog(Constants.SERVER_IP + ":8080" + "/help/accountVerification.html");
    }


    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }

}
