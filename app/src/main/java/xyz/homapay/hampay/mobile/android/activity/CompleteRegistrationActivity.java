package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.MobileRegistrationIdEntryRequest;
import xyz.homapay.hampay.common.core.model.response.MobileRegistrationIdEntryResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestMobileRegistrationIdEntry;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class CompleteRegistrationActivity extends AppCompatActivity {

    ButtonRectangle hampay_login_button;

    ImageView step_circle;

    Activity activity;
    Context context;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;


    public void contactUs(View view){
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/reg-com.html");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_complete_registration);


        context = this;

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

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

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
