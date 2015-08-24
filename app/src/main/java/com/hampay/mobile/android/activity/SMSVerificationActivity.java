package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import com.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import com.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import com.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestRegistrationSendSmsToken;
import com.hampay.mobile.android.async.RequestVerifyMobile;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.component.material.RippleView;
import com.hampay.mobile.android.component.numericalprogressbar.NumberProgressBar;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;

public class SMSVerificationActivity extends Activity implements View.OnClickListener{


    Activity activity;

    ButtonRectangle verify_button;

    RippleView digit_1;
    RippleView digit_2;
    RippleView digit_3;
    RippleView digit_4;
    RippleView digit_5;
    RippleView digit_6;
    RippleView digit_7;
    RippleView digit_8;
    RippleView digit_9;
    RippleView digit_0;
    RippleView resend_active_code;
    RippleView backspace;

    String receivedSmsValue = "";

    FacedTextView input_digit_1;
    FacedTextView input_digit_2;
    FacedTextView input_digit_3;
    FacedTextView input_digit_4;
    FacedTextView input_digit_5;

    Context context;

    RequestRegistrationSendSmsToken requestRegistrationSendSmsToken;
    RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest;

    LinearLayout keyboard;
    LinearLayout activation_holder;

    SharedPreferences.Editor editor;

//    RelativeLayout loading_rl;
    HamPayDialog hamPayDialog;

    SharedPreferences prefs;

    RequestVerifyMobile requestVerifyMobile;
    RegistrationVerifyMobileRequest registrationVerifyMobileRequest;

    private BroadcastReceiver mIntentReceiver;

    private NumberProgressBar numberProgressBar;

    CountDownTimer countDownTimer;

    boolean sendSmsPermision = false;
    int sendSmsCounter = 0;

    @Override
    protected void onResume() {
        super.onResume();

        android.util.Log.e("recevice", "rec");

        IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
        mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("get_msg");

                msg = msg.replace("\n", "");
                String body = msg.substring(msg.lastIndexOf(":") + 1,
                        msg.length()).trim();
                String pNumber = msg.substring(0, msg.lastIndexOf(":"));

                receivedSmsValue = body;

                editor.putString(Constants.RECEIVED_SMS_ACTIVATION, receivedSmsValue);
                editor.commit();

                numberProgressBar.setProgress(0);
                numberProgressBar.setVisibility(View.INVISIBLE);
                countDownTimer.cancel();

                input_digit_1.setText(receivedSmsValue.substring(0, 1));
                input_digit_1.setBackgroundColor(Color.TRANSPARENT);
                input_digit_2.setText(receivedSmsValue.substring(1, 2));
                input_digit_2.setBackgroundColor(Color.TRANSPARENT);
                input_digit_3.setText(receivedSmsValue.substring(2, 3));
                input_digit_3.setBackgroundColor(Color.TRANSPARENT);
                input_digit_4.setText(receivedSmsValue.substring(3, 4));
                input_digit_4.setBackgroundColor(Color.TRANSPARENT);
                input_digit_5.setText(receivedSmsValue.substring(4, 5));
                input_digit_5.setBackgroundColor(Color.TRANSPARENT);

            }
        };
        this.registerReceiver(mIntentReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (countDownTimer != null)
            countDownTimer.cancel();

        if (requestVerifyMobile != null){
            if (!requestVerifyMobile.isCancelled())
                requestVerifyMobile.cancel(true);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);

        context = this;

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.RECEIVED_SMS_ACTIVATION, "");
        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, SMSVerificationActivity.class.getName());
        editor.commit();

        numberProgressBar = (NumberProgressBar)findViewById(R.id.numberProgressBar);

        countDownTimer = new CountDownTimer(181000, 1000) {

            public void onTick(long millisUntilFinished) {
                numberProgressBar.incrementProgressBy(1);
            }

            public void onFinish() {

                numberProgressBar.setProgress(0);

                if (prefs.getString(Constants.RECEIVED_SMS_ACTIVATION, "").length() == 0) {

                    sendSmsCounter++;

                    if (sendSmsCounter < 3) {
                        sendSmsPermision = true;
                        Toast.makeText(context, getString(R.string.msg_fail_receive_sms), Toast.LENGTH_LONG).show();
                    } else {
                        sendSmsPermision = false;
                        Toast.makeText(context, getString(R.string.sms_upper_reach_sms), Toast.LENGTH_LONG).show();
                    }

                    keyboard.setVisibility(LinearLayout.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.keyboard);
                    animation.setDuration(400);
                    keyboard.setAnimation(animation);
                    keyboard.animate();
                    animation.start();
                    keyboard.setVisibility(View.VISIBLE);
                }
            }
        }.start();

//        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        activity = SMSVerificationActivity.this;

        hamPayDialog = new HamPayDialog(activity);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        activation_holder = (LinearLayout)findViewById(R.id.activation_holder);
        activation_holder.setOnClickListener(this);


        digit_1 = (RippleView)findViewById(R.id.digit_1);
        digit_1.setOnClickListener(this);


        digit_2 = (RippleView)findViewById(R.id.digit_2);
        digit_2.setOnClickListener(this);
        digit_3 = (RippleView)findViewById(R.id.digit_3);
        digit_3.setOnClickListener(this);
        digit_4 = (RippleView)findViewById(R.id.digit_4);
        digit_4.setOnClickListener(this);
        digit_5 = (RippleView)findViewById(R.id.digit_5);
        digit_5.setOnClickListener(this);
        digit_6 = (RippleView)findViewById(R.id.digit_6);
        digit_6.setOnClickListener(this);
        digit_7 = (RippleView)findViewById(R.id.digit_7);
        digit_7.setOnClickListener(this);
        digit_8 = (RippleView)findViewById(R.id.digit_8);
        digit_8.setOnClickListener(this);
        digit_9 = (RippleView)findViewById(R.id.digit_9);
        digit_9.setOnClickListener(this);
        digit_0 = (RippleView)findViewById(R.id.digit_0);
        digit_0.setOnClickListener(this);
        resend_active_code = (RippleView)findViewById(R.id.resend_active_code);
        resend_active_code.setOnClickListener(this);
        backspace = (RippleView)findViewById(R.id.backspace);
        backspace.setOnClickListener(this);

        input_digit_1 = (FacedTextView)findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView)findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView)findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView)findViewById(R.id.input_digit_4);
        input_digit_5 = (FacedTextView)findViewById(R.id.input_digit_5);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        registrationSendSmsTokenRequest = new RegistrationSendSmsTokenRequest();
        registrationSendSmsTokenRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));

        verify_button = (ButtonRectangle)findViewById(R.id.verify_button);
        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (receivedSmsValue.length() == 5) {

                    registrationVerifyMobileRequest = new RegistrationVerifyMobileRequest();

                    registrationVerifyMobileRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                    registrationVerifyMobileRequest.setSmsToken(receivedSmsValue);

                    verify_button.setEnabled(false);
                    requestVerifyMobile = new RequestVerifyMobile(context, new RequestRegistrationVerifyMobileTaskCompleteListener());
                    requestVerifyMobile.execute(registrationVerifyMobileRequest);

                }else {
                    Toast.makeText(context, getString(R.string.msg_fail_sms_verification), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void contactUs(View view){
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/reg-smsToken.html");
    }

    public class RequestRegistrationVerifyMobileTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyMobileResponse>>
    {
        @Override
        public void onTaskComplete(ResponseMessage<RegistrationVerifyMobileResponse> registrationVerifyMobileResponseMessage)
        {

            numberProgressBar.setProgress(0);
            verify_button.setEnabled(true);
//            loading_rl.setVisibility(View.GONE);
            hamPayDialog.dismisWaitingDialog();
            if (registrationVerifyMobileResponseMessage != null) {

                if (registrationVerifyMobileResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    if (registrationVerifyMobileResponseMessage.getService().getIsVerified()) {
                        Intent intent = new Intent();
                        intent.setClass(SMSVerificationActivity.this, ConfirmAccountNoActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);

                    } else {
                        new HamPayDialog(activity).showIncorrectSMSVerification();
                    }
                }else {
                    requestVerifyMobile = new RequestVerifyMobile(context, new RequestRegistrationVerifyMobileTaskCompleteListener());
                    new HamPayDialog(activity).showFailRegistrationVerifyMobileDialog(requestVerifyMobile, registrationVerifyMobileRequest,
                            registrationVerifyMobileResponseMessage.getService().getResultStatus().getCode(),
                            registrationVerifyMobileResponseMessage.getService().getResultStatus().getDescription());
                }

            }else {
                requestVerifyMobile = new RequestVerifyMobile(context, new RequestRegistrationVerifyMobileTaskCompleteListener());
                new HamPayDialog(activity).showFailRegistrationVerifyMobileDialog(requestVerifyMobile, registrationVerifyMobileRequest,
                        registrationVerifyMobileResponseMessage.getService().getResultStatus().getCode(),
                        registrationVerifyMobileResponseMessage.getService().getResultStatus().getDescription());
            }
        }

        @Override
        public void onTaskPreRun() {
            keyboard.setVisibility(View.GONE);
//            loading_rl.setVisibility(View.VISIBLE);
            hamPayDialog.showWaitingdDialog("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.activation_holder:
                keyboard.setVisibility(LinearLayout.VISIBLE);
                Animation animation   =    AnimationUtils.loadAnimation(this, R.anim.keyboard);
                animation.setDuration(400);
                keyboard.setAnimation(animation);
                keyboard.animate();
                animation.start();
                keyboard.setVisibility(View.VISIBLE);
                break;

            case R.id.digit_1:
                inputDigit("1");
                break;

            case R.id.rect:
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

            case R.id.resend_active_code:
                if (sendSmsPermision){
                    sendSmsPermision = false;

                    requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(context, new RequestRegistrationSendSmsTokenTaskCompleteListener());
                    requestRegistrationSendSmsToken.execute(registrationSendSmsTokenRequest);

                }
                break;


        }
    }

    private void inputDigit(String digit){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (receivedSmsValue.length() <= 4) {

            switch (receivedSmsValue.length()) {

                case 0:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_1.setText("");
                        input_digit_1.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_1.setText(digit);
                        input_digit_1.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_2.setText("");
                    input_digit_2.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_3.setText("");
                    input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_4.setText("");
                    input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_5.setText("");
                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);
                    break;

                case 1:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_2.setText("");
                        input_digit_2.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_2.setText(digit);
                        input_digit_2.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_3.setText("");
                    input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_4.setText("");
                    input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_5.setText("");
                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);

                    break;
                case 2:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_3.setText("");
                        input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_3.setText(digit);
                        input_digit_3.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_4.setText("");
                    input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_5.setText("");
                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);
                    break;
                case 3:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_4.setText("");
                        input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_4.setText(digit);
                        input_digit_4.setBackgroundColor(Color.TRANSPARENT);
                    }
                    vibrator.vibrate(20);
                    break;
                case 4:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_5.setText("");
                        input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_5.setText(digit);
                        input_digit_5.setBackgroundColor(Color.TRANSPARENT);
                    }
                    vibrator.vibrate(20);
                    break;
                case 5:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_5.setText("");
                        input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_5.setText(digit);
                        input_digit_5.setBackgroundColor(Color.TRANSPARENT);
                    }
                    vibrator.vibrate(20);
                    break;
            }

        }

        if (digit.contains("d")){
            if (receivedSmsValue.length() > 0) {
                receivedSmsValue = receivedSmsValue.substring(0, receivedSmsValue.length() - 1);
                if (receivedSmsValue.length() == 4){
                    input_digit_5.setText("");
                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                else if (receivedSmsValue.length() == 3){
                    input_digit_4.setText("");
                    input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                else if (receivedSmsValue.length() == 2){
                    input_digit_3.setText("");
                    input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                else if (receivedSmsValue.length() == 1){
                    input_digit_2.setText("");
                    input_digit_2.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                else if (receivedSmsValue.length() == 0){
                    input_digit_1.setText("");
                    input_digit_1.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
            }
        }
        else {
            if (receivedSmsValue.length() <= 4) {
                receivedSmsValue += digit;
            }
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }


    public class RequestRegistrationSendSmsTokenTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationSendSmsTokenResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsTokenResponse)
        {


//            loading_rl.setVisibility(View.GONE);

            hamPayDialog.dismisWaitingDialog();

            if (registrationSendSmsTokenResponse != null) {
                if (registrationSendSmsTokenResponse.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    editor.putString(Constants.REGISTERED_ACTIVITY_DATA, VerificationActivity.class.toString());
                    editor.commit();


                    numberProgressBar.setProgress(0);

                    countDownTimer = new CountDownTimer(181000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            numberProgressBar.incrementProgressBy(1);
                        }

                        public void onFinish() {

                            sendSmsCounter++;

                            if (sendSmsCounter < 3) {
                                sendSmsPermision = true;
                                Toast.makeText(context, getString(R.string.msg_fail_receive_sms), Toast.LENGTH_LONG).show();
                            }else {
                                sendSmsPermision = false;
                                Toast.makeText(context, getString(R.string.sms_upper_reach_sms), Toast.LENGTH_LONG).show();
                            }

                            keyboard.setVisibility(LinearLayout.VISIBLE);
                            Animation animation   =    AnimationUtils.loadAnimation(context, R.anim.keyboard);
                            animation.setDuration(400);
                            keyboard.setAnimation(animation);
                            keyboard.animate();
                            animation.start();
                            keyboard.setVisibility(View.VISIBLE);
                        }
                    }.start();


                }else {

                    requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(context, new RequestRegistrationSendSmsTokenTaskCompleteListener());
                    new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(requestRegistrationSendSmsToken, registrationSendSmsTokenRequest,
                            registrationSendSmsTokenResponse.getService().getResultStatus().getCode(),
                            registrationSendSmsTokenResponse.getService().getResultStatus().getDescription());
                }

            }else {
                requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(context, new RequestRegistrationSendSmsTokenTaskCompleteListener());
                new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(requestRegistrationSendSmsToken, registrationSendSmsTokenRequest,
                        "200",
                        getString(R.string.mgs_fail_registration_send_sms_token));
            }
        }

        @Override
        public void onTaskPreRun() {
//            loading_rl.setVisibility(View.VISIBLE);
            hamPayDialog.showWaitingdDialog("");
        }
    }


}
