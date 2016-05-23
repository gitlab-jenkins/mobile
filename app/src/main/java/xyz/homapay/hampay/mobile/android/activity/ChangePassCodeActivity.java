package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Map;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.ChangePassCodeRequest;
import xyz.homapay.hampay.common.core.model.response.ChangePassCodeResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestChangePassCode;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PasswordComplexity;

public class ChangePassCodeActivity extends AppCompatActivity implements View.OnClickListener {

    HamPayDialog hamPayDialog;

    FacedTextView digit_1;
    FacedTextView digit_2;
    FacedTextView digit_3;
    FacedTextView digit_4;
    FacedTextView digit_5;
    FacedTextView digit_6;
    FacedTextView digit_7;
    FacedTextView digit_8;
    FacedTextView digit_9;
    FacedTextView digit_0;
    FacedTextView keyboard_dismiss;
    RelativeLayout backspace;

    String currentPassword = "";
    String inputPasswordValue = "";
    String inputRePasswordValue = "";

    ImageView input_digit_1;
    ImageView input_digit_2;
    ImageView input_digit_3;
    ImageView input_digit_4;
    ImageView input_digit_5;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    LinearLayout keyboard;
    LinearLayout password_holder;

    RequestChangePassCode requestChangePassCode;
    ChangePassCodeRequest changePassCodeRequest;

    FacedTextView pass_code_change_text;
    int passCodeChangeStep = 1;

    Context context;
    Activity activity;

    Tracker hamPayGaTracker;

    public void backActionBar(View view){
        finish();
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
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass_code);

        context = this;
        activity = ChangePassCodeActivity.this;

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        password_holder = (LinearLayout)findViewById(R.id.password_holder);
        password_holder.setOnClickListener(this);

        digit_1 = (FacedTextView)findViewById(R.id.digit_1);
        digit_1.setOnClickListener(this);
        digit_2 = (FacedTextView)findViewById(R.id.digit_2);
        digit_2.setOnClickListener(this);
        digit_3 = (FacedTextView)findViewById(R.id.digit_3);
        digit_3.setOnClickListener(this);
        digit_4 = (FacedTextView)findViewById(R.id.digit_4);
        digit_4.setOnClickListener(this);
        digit_5 = (FacedTextView)findViewById(R.id.digit_5);
        digit_5.setOnClickListener(this);
        digit_6 = (FacedTextView)findViewById(R.id.digit_6);
        digit_6.setOnClickListener(this);
        digit_7 = (FacedTextView)findViewById(R.id.digit_7);
        digit_7.setOnClickListener(this);
        digit_8 = (FacedTextView)findViewById(R.id.digit_8);
        digit_8.setOnClickListener(this);
        digit_9 = (FacedTextView)findViewById(R.id.digit_9);
        digit_9.setOnClickListener(this);
        digit_0 = (FacedTextView)findViewById(R.id.digit_0);
        digit_0.setOnClickListener(this);
        keyboard_dismiss = (FacedTextView)findViewById(R.id.keyboard_dismiss);
        keyboard_dismiss.setOnClickListener(this);
        backspace = (RelativeLayout)findViewById(R.id.backspace);
        backspace.setOnClickListener(this);

        input_digit_1 = (ImageView)findViewById(R.id.input_digit_1);
        input_digit_2 = (ImageView)findViewById(R.id.input_digit_2);
        input_digit_3 = (ImageView)findViewById(R.id.input_digit_3);
        input_digit_4 = (ImageView)findViewById(R.id.input_digit_4);
        input_digit_5 = (ImageView)findViewById(R.id.input_digit_5);
        pass_code_change_text = (FacedTextView)findViewById(R.id.pass_code_change_text);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.password_holder:
                if (keyboard.getVisibility() != View.VISIBLE)
                    new Expand(keyboard).animate();
                break;
            case R.id.keyboard_dismiss:
                if (keyboard.getVisibility() == View.VISIBLE)
                    new Collapse(keyboard).animate();
                break;
            case R.id.digit_1:
                inputDigit("1");
                break;

            case R.id.digit_2:
                inputDigit("2");
                break;

            case R.id.digit_3:
                inputDigit("3");
                break;

            case R.id.digit_4:
                inputDigit("4");
                break;

            case R.id.digit_5:
                inputDigit("5");
                break;

            case R.id.digit_6:
                inputDigit("6");
                break;

            case R.id.digit_7:
                inputDigit("7");
                break;

            case R.id.digit_8:
                inputDigit("8");
                break;

            case R.id.digit_9:
                inputDigit("9");
                break;

            case R.id.digit_0:
                inputDigit("0");
                break;

            case R.id.backspace:
                inputDigit("d");
                break;
        }
    }

    private void inputDigit(String digit){

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (passCodeChangeStep == 1) {
            if (currentPassword.length() <= 4) {

                if (digit.contains("d")) {
                    if (currentPassword.length() > 0) {
                        currentPassword = currentPassword.substring(0, currentPassword.length() - 1);
                    }
                } else {
                    if (currentPassword.length() <= 4) {
                        currentPassword += digit;
                    }
                }


                switch (currentPassword.length()) {

                    case 0:
                        input_digit_1.setImageResource(R.drawable.pass_value_empty);
                        input_digit_2.setImageResource(R.drawable.pass_value_empty);
                        input_digit_3.setImageResource(R.drawable.pass_value_empty);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);
                        break;

                    case 1:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_empty);
                        input_digit_3.setImageResource(R.drawable.pass_value_empty);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);

                        break;
                    case 2:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_3.setImageResource(R.drawable.pass_value_empty);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);
                        break;
                    case 3:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_3.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);
                        break;
                    case 4:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_3.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_4.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);
                        break;
                    case 5:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_3.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_4.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_5.setImageResource(R.drawable.pass_value_placeholder);
                        vibrator.vibrate(20);

                        int passwordComplexity = new PasswordComplexity(currentPassword).check();
                        if (passwordComplexity != 1){
                            currentPassword = "";
                            Toast.makeText(activity, getString(passwordComplexity), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        passCodeChangeStep = 2;
                        pass_code_change_text.setText(getString(R.string.change_pass_code_text_2));

                        input_digit_1.setImageResource(R.drawable.pass_value_empty);
                        input_digit_2.setImageResource(R.drawable.pass_value_empty);
                        input_digit_3.setImageResource(R.drawable.pass_value_empty);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);


                        break;
                }
            }
        }

        else if (passCodeChangeStep == 2) {


            if (inputPasswordValue.length() <= 4) {

                if (digit.contains("d")) {
                    if (inputPasswordValue.length() > 0) {
                        inputPasswordValue = inputPasswordValue.substring(0, inputPasswordValue.length() - 1);
                    }
                } else {
                    if (inputPasswordValue.length() <= 4) {
                        inputPasswordValue += digit;
                    }
                }


                switch (inputPasswordValue.length()) {

                    case 0:
                        input_digit_1.setImageResource(R.drawable.pass_value_empty);
                        input_digit_2.setImageResource(R.drawable.pass_value_empty);
                        input_digit_3.setImageResource(R.drawable.pass_value_empty);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);
                        break;

                    case 1:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_empty);
                        input_digit_3.setImageResource(R.drawable.pass_value_empty);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);

                        break;
                    case 2:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_3.setImageResource(R.drawable.pass_value_empty);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);
                        break;
                    case 3:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_3.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);
                        break;
                    case 4:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_3.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_4.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);
                        break;
                    case 5:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_3.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_4.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_5.setImageResource(R.drawable.pass_value_placeholder);
                        vibrator.vibrate(20);

                        int passwordComplexity = new PasswordComplexity(inputPasswordValue).check();
                        if (passwordComplexity != 1){
                            inputPasswordValue = "";
                            Toast.makeText(activity, getString(passwordComplexity), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        passCodeChangeStep = 3;
                        pass_code_change_text.setText(getString(R.string.change_pass_code_text_3));

                        input_digit_1.setImageResource(R.drawable.pass_value_empty);
                        input_digit_2.setImageResource(R.drawable.pass_value_empty);
                        input_digit_3.setImageResource(R.drawable.pass_value_empty);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);


                        break;
                }
            }
        }else {

            if (inputRePasswordValue.length() <= 4) {

                if (digit.contains("d")) {
                    if (inputRePasswordValue.length() > 0) {
                        inputRePasswordValue = inputRePasswordValue.substring(0, inputRePasswordValue.length() - 1);
                    }
                } else {
                    if (inputRePasswordValue.length() <= 4) {
                        inputRePasswordValue += digit;
                    }
                }


                switch (inputRePasswordValue.length()) {

                    case 0:
                        input_digit_1.setImageResource(R.drawable.pass_value_empty);
                        input_digit_2.setImageResource(R.drawable.pass_value_empty);
                        input_digit_3.setImageResource(R.drawable.pass_value_empty);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);
                        break;

                    case 1:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_empty);
                        input_digit_3.setImageResource(R.drawable.pass_value_empty);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);

                        break;
                    case 2:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_3.setImageResource(R.drawable.pass_value_empty);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);
                        break;
                    case 3:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_3.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_4.setImageResource(R.drawable.pass_value_empty);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);
                        break;
                    case 4:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_3.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_4.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_5.setImageResource(R.drawable.pass_value_empty);
                        vibrator.vibrate(20);
                        break;
                    case 5:
                        input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_3.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_4.setImageResource(R.drawable.pass_value_placeholder);
                        input_digit_5.setImageResource(R.drawable.pass_value_placeholder);
                        vibrator.vibrate(20);

                        int passwordComplexity = new PasswordComplexity(inputRePasswordValue).check();
                        if (passwordComplexity != 1){
                            inputRePasswordValue = "";
                            Toast.makeText(activity, getString(passwordComplexity), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (inputPasswordValue.equalsIgnoreCase(inputRePasswordValue)) {
                            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                            editor.commit();
                            changePassCodeRequest = new ChangePassCodeRequest();
                            changePassCodeRequest.setCurrentPassCode(currentPassword);
                            changePassCodeRequest.setNewPassCode(inputPasswordValue);
                            changePassCodeRequest.setMemorableCode(prefs.getString(Constants.MEMORABLE_WORD, ""));
                            requestChangePassCode = new RequestChangePassCode(context, new RequestChangePassCodeTaskCompleteListener());
                            requestChangePassCode.execute(changePassCodeRequest);
                        } else {
                            (new HamPayDialog(this)).showDisMatchPasswordDialog();
                            resetLayout();
                        }

                        break;
                }
            }
        }
    }


    public class RequestChangePassCodeTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<ChangePassCodeResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<ChangePassCodeResponse> changePassCodeResponseMessage)
        {

            hamPayDialog.dismisWaitingDialog();

            if (changePassCodeResponseMessage != null) {
                if (changePassCodeResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    passCodeChangeStep = 1;
                    pass_code_change_text.setText(getString(R.string.change_pass_code_text_1));
                    currentPassword = "";
                    inputPasswordValue = "";
                    inputRePasswordValue = "";
                    input_digit_1.setImageResource(R.drawable.pass_value_empty);
                    input_digit_2.setImageResource(R.drawable.pass_value_empty);
                    input_digit_3.setImageResource(R.drawable.pass_value_empty);
                    input_digit_4.setImageResource(R.drawable.pass_value_empty);
                    input_digit_5.setImageResource(R.drawable.pass_value_empty);
                    new HamPayDialog(activity).showSuccessChangeSettingDialog(changePassCodeResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Change PassCode")
                            .setAction("Change")
                            .setLabel("Success")
                            .build());

                }else {
                    resetLayout();
                    requestChangePassCode = new RequestChangePassCode(context, new RequestChangePassCodeTaskCompleteListener());
                    new HamPayDialog(activity).showFailChangePassCodeDialog(requestChangePassCode, changePassCodeRequest,
                            changePassCodeResponseMessage.getService().getResultStatus().getCode(),
                            changePassCodeResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Change PassCode")
                            .setAction("Change")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                resetLayout();
                requestChangePassCode = new RequestChangePassCode(context, new RequestChangePassCodeTaskCompleteListener());
                new HamPayDialog(activity).showFailChangePassCodeDialog(requestChangePassCode, changePassCodeRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_change_pass_code));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Change PassCode")
                        .setAction("Change")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }


    private void resetLayout(){
        passCodeChangeStep = 1;
        pass_code_change_text.setText(getString(R.string.change_pass_code_text_1));
        currentPassword = "";
        inputPasswordValue = "";
        inputRePasswordValue = "";

        input_digit_1.setImageResource(R.drawable.pass_value_empty);
        input_digit_2.setImageResource(R.drawable.pass_value_empty);
        input_digit_3.setImageResource(R.drawable.pass_value_empty);
        input_digit_4.setImageResource(R.drawable.pass_value_empty);
        input_digit_5.setImageResource(R.drawable.pass_value_empty);
    }

    @Override
    public void onBackPressed() {

        if (keyboard.getVisibility() == View.VISIBLE){
            new Collapse(keyboard).animate();
        }
        else {
            finish();
        }
    }

}
