package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.app.AppEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PreferencesManager;

public class CompleteRegistrationActivity extends AppCompatActivity {

    private FacedTextView hampay_login_button;
    private Activity activity;
    private SharedPreferences.Editor editor;
    private FacedTextView congrats_text;
    private PreferencesManager preferencesManager;
    private AppEvent appEvent = AppEvent.REGISTER;


    public void userManual(View view){
        Intent intent = new Intent();
        intent.setClass(activity, UserManualActivity.class);
        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_complete);
        intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_complete);
        startActivity(intent);
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
        preferencesManager = new PreferencesManager(this);
        preferencesManager.setRegistered(true);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.NOTIFICATION_STATUS, true);
        editor.commit();
        LogEvent logEvent = new LogEvent(this);
        logEvent.log(appEvent);
        congrats_text = (FacedTextView)findViewById(R.id.congrats_text);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.complete_registarion_text_1));
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.rgb(109, 7, 109));
        spannableStringBuilder.setSpan(foregroundColorSpan, 21, 26, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        congrats_text.setText(spannableStringBuilder);

        activity = CompleteRegistrationActivity.this;

        hampay_login_button = (FacedTextView) findViewById(R.id.hampay_login_button);
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
}
