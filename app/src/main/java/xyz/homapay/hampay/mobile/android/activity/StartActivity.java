package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class StartActivity extends AppCompatActivity {

    FacedTextView start_button;

    Activity activity;
    SharedPreferences.Editor editor;

    public void userManual(View view){
        Intent intent = new Intent();
        intent.setClass(activity, UserManualActivity.class);
        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_start_activity);
        intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_start_activity);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        int density= getResources().getDisplayMetrics().densityDpi;
//
//        switch(density)
//        {
//            case DisplayMetrics.DENSITY_LOW:
//                Toast.makeText(this, "LDPI", Toast.LENGTH_SHORT).show();
//                break;
//            case DisplayMetrics.DENSITY_MEDIUM:
//                Toast.makeText(this, "MDPI", Toast.LENGTH_SHORT).show();
//                break;
//            case DisplayMetrics.DENSITY_HIGH:
//                Toast.makeText(this, "HDPI", Toast.LENGTH_SHORT).show();
//                break;
//            case DisplayMetrics.DENSITY_XHIGH:
//                Toast.makeText(this, "XHDPI", Toast.LENGTH_SHORT).show();
//                break;
//        }

        activity = StartActivity.this;

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        start_button = (FacedTextView) findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HamPayDialog(StartActivity.this).showTcPrivacyDialog();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HamPayApplication.setAppSate(AppState.Resumed);
    }
}
