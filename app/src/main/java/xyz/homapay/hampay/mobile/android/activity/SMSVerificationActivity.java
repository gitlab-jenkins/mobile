package xyz.homapay.hampay.mobile.android.activity;

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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationSendSmsToken;
import xyz.homapay.hampay.mobile.android.async.RequestVerifyMobile;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.component.material.RippleView;
import xyz.homapay.hampay.mobile.android.component.numericalprogressbar.NumberProgressBar;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class SMSVerificationActivity extends AppCompatActivity implements View.OnClickListener{


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
    RippleView keyboard_dismiss;
    RippleView resend_active_code;
    RippleView backspace;

    FacedTextView sms_delivery_text;

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

    HamPayDialog hamPayDialog;

    SharedPreferences prefs;

    RequestVerifyMobile requestVerifyMobile;
    RegistrationVerifyMobileRequest registrationVerifyMobileRequest;

    private BroadcastReceiver mIntentReceiver;

    private NumberProgressBar numberProgressBar;
    private FacedTextView remain_timer;

    CountDownTimer countDownTimer;

    boolean sendSmsPermission = false;
    int sendSmsCounter = 0;

    Tracker hamPayGaTracker;

    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
        if (countDownTimer != null)
            countDownTimer.cancel();

        if (requestVerifyMobile != null){
            if (!requestVerifyMobile.isCancelled())
                requestVerifyMobile.cancel(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HamPayApplication.setAppSate(AppState.Resumed);

        IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
        mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("get_msg");
                receivedSmsValue = message.substring(message.lastIndexOf(":") + 1, message.length()).trim();
                editor.putString(Constants.RECEIVED_SMS_ACTIVATION, receivedSmsValue);
                editor.commit();

                numberProgressBar.setProgress(0);
                numberProgressBar.setVisibility(View.INVISIBLE);
                countDownTimer.cancel();

                input_digit_1.setText(persianEnglishDigit.E2P(receivedSmsValue.substring(0, 1)));
                input_digit_1.setBackgroundColor(Color.TRANSPARENT);
                input_digit_2.setText(persianEnglishDigit.E2P(receivedSmsValue.substring(1, 2)));
                input_digit_2.setBackgroundColor(Color.TRANSPARENT);
                input_digit_3.setText(persianEnglishDigit.E2P(receivedSmsValue.substring(2, 3)));
                input_digit_3.setBackgroundColor(Color.TRANSPARENT);
                input_digit_4.setText(persianEnglishDigit.E2P(receivedSmsValue.substring(3, 4)));
                input_digit_4.setBackgroundColor(Color.TRANSPARENT);
                input_digit_5.setText(persianEnglishDigit.E2P(receivedSmsValue.substring(4, 5)));
                input_digit_5.setBackgroundColor(Color.TRANSPARENT);

                if (receivedSmsValue.length() == 5) {

                    registrationVerifyMobileRequest = new RegistrationVerifyMobileRequest();

                    registrationVerifyMobileRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                    registrationVerifyMobileRequest.setSmsToken(receivedSmsValue);

                    verify_button.setEnabled(false);
                    requestVerifyMobile = new RequestVerifyMobile(context, new RequestRegistrationVerifyMobileTaskCompleteListener());
                    requestVerifyMobile.execute(registrationVerifyMobileRequest);

                }

            }
        };
        this.registerReceiver(mIntentReceiver, intentFilter);
    }

    Bundle bundle;

    private int minutes = 0;
    private int seconds = 0;
    private PersianEnglishDigit persianEnglishDigit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);

        context = this;

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        persianEnglishDigit = new PersianEnglishDigit();

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.RECEIVED_SMS_ACTIVATION, "");
        editor.commit();

        numberProgressBar = (NumberProgressBar)findViewById(R.id.numberProgressBar);
        remain_timer = (FacedTextView)findViewById(R.id.remain_timer);

        countDownTimer = new CountDownTimer(181000, 1000) {

            public void onTick(long millisUntilFinished) {
                numberProgressBar.incrementProgressBy(1);
                minutes =  (int)(millisUntilFinished / (60 * 1000));
                seconds = (int)(millisUntilFinished / 1000) % 60;
                remain_timer.setText(persianEnglishDigit.E2P(String.format("%02d:%02d", minutes, seconds)));
            }

            public void onFinish() {

                numberProgressBar.setProgress(0);
                remain_timer.setText("۰۰:۰۰");

                if (prefs.getString(Constants.RECEIVED_SMS_ACTIVATION, "").length() == 0) {

                    sendSmsCounter++;

                    if (sendSmsCounter < 3) {
                        sendSmsPermission = true;
                        Toast.makeText(context, getString(R.string.msg_fail_receive_sms), Toast.LENGTH_LONG).show();
                    } else {
                        sendSmsPermission = false;
                        Toast.makeText(context, getString(R.string.sms_upper_reach_sms), Toast.LENGTH_LONG).show();
                    }

                    resend_active_code.setVisibility(View.VISIBLE);

                }
            }
        }.start();

        bundle = getIntent().getExtras();

        activity = SMSVerificationActivity.this;

        hamPayDialog = new HamPayDialog(activity);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        activation_holder = (LinearLayout)findViewById(R.id.activation_holder);
        activation_holder.setOnClickListener(this);

        sms_delivery_text = (FacedTextView)findViewById(R.id.sms_delivery_text);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.deliver_verification));
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.rgb(255, 158, 158));
        spannableStringBuilder.setSpan(foregroundColorSpan, 70, 81, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sms_delivery_text.setText(spannableStringBuilder);

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
        keyboard_dismiss = (RippleView)findViewById(R.id.keyboard_dismiss);
        keyboard_dismiss.setOnClickListener(this);
        backspace = (RippleView)findViewById(R.id.backspace);
        backspace.setOnClickListener(this);
        resend_active_code = (RippleView)findViewById(R.id.resend_active_code);
        resend_active_code.setOnClickListener(this);

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
                    Toast.makeText(context, getString(R.string.msg_fail_sms_correct_entry), Toast.LENGTH_SHORT).show();
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

            hamPayDialog.dismisWaitingDialog();
            if (registrationVerifyMobileResponseMessage != null) {

                if (registrationVerifyMobileResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    if (registrationVerifyMobileResponseMessage.getService().getIsVerified()) {

                        Intent intent = new Intent();
                        intent.setClass(SMSVerificationActivity.this, PasswordEntryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);

//                        registerCardRequest = new RegisterCardRequest();
//                        registerCardRequest.setMobileNumber(new PersianEnglishDigit().P2E(cellNumber));
//                        registerCardRequest.setCardNumber(new PersianEnglishDigit().P2E(cardNumber));
//                        requestRegisterCard = new RequestRegisterCard(context, new RequestRegisterCardTaskCompleteListener());
//                        requestRegisterCard.execute(registerCardRequest);

                        hamPayGaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Verify Mobile")
                                .setAction("Verify")
                                .setLabel("Success")
                                .build());

                    } else {
                        new HamPayDialog(activity).showIncorrectSMSVerification();

                        hamPayGaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Verify Mobile")
                                .setAction("Verify")
                                .setLabel("Success(Is Not Verified)")
                                .build());
                    }
                }else {
                    requestVerifyMobile = new RequestVerifyMobile(context, new RequestRegistrationVerifyMobileTaskCompleteListener());
                    new HamPayDialog(activity).showFailRegistrationVerifyMobileDialog(requestVerifyMobile, registrationVerifyMobileRequest,
                            registrationVerifyMobileResponseMessage.getService().getResultStatus().getCode(),
                            registrationVerifyMobileResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Verify Mobile")
                            .setAction("Verify")
                            .setLabel("Fail(Server)")
                            .build());
                }

            }else {
                requestVerifyMobile = new RequestVerifyMobile(context, new RequestRegistrationVerifyMobileTaskCompleteListener());
                new HamPayDialog(activity).showFailRegistrationVerifyMobileDialog(requestVerifyMobile, registrationVerifyMobileRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_send_sms));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Verify Mobile")
                        .setAction("Verify")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.activation_holder:
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
                if (sendSmsPermission){
                    sendSmsPermission = false;
                    hamPayDialog.showWaitingdDialog("");
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
                        input_digit_1.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_1.setText(persianEnglishDigit.E2P(digit));
                        input_digit_1.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_2.setText("");
                    input_digit_2.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    input_digit_3.setText("");
                    input_digit_3.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    input_digit_4.setText("");
                    input_digit_4.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    input_digit_5.setText("");
                    input_digit_5.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);
                    break;

                case 1:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_2.setText("");
                        input_digit_2.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_2.setText(persianEnglishDigit.E2P(digit));
                        input_digit_2.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_3.setText("");
                    input_digit_3.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    input_digit_4.setText("");
                    input_digit_4.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    input_digit_5.setText("");
                    input_digit_5.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);

                    break;
                case 2:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_3.setText("");
                        input_digit_3.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_3.setText(persianEnglishDigit.E2P(digit));
                        input_digit_3.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_4.setText("");
                    input_digit_4.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    input_digit_5.setText("");
                    input_digit_5.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);
                    break;
                case 3:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_4.setText("");
                        input_digit_4.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_4.setText(persianEnglishDigit.E2P(digit));
                        input_digit_4.setBackgroundColor(Color.TRANSPARENT);
                    }
                    vibrator.vibrate(20);
                    break;
                case 4:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_5.setText("");
                        input_digit_5.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_5.setText(persianEnglishDigit.E2P(digit));
                        input_digit_5.setBackgroundColor(Color.TRANSPARENT);
                    }
                    vibrator.vibrate(20);
                    break;
                case 5:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_5.setText("");
                        input_digit_5.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_5.setText(persianEnglishDigit.E2P(digit));
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
                    input_digit_5.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                }
                else if (receivedSmsValue.length() == 3){
                    input_digit_4.setText("");
                    input_digit_4.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                }
                else if (receivedSmsValue.length() == 2){
                    input_digit_3.setText("");
                    input_digit_3.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                }
                else if (receivedSmsValue.length() == 1){
                    input_digit_2.setText("");
                    input_digit_2.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                }
                else if (receivedSmsValue.length() == 0){
                    input_digit_1.setText("");
                    input_digit_1.setBackground(ContextCompat.getDrawable(context, R.drawable.remember_edittext_bg));
                }
            }
        }
        else {
            if (receivedSmsValue.length() <= 4) {
                receivedSmsValue += digit;
            }
        }

        if (receivedSmsValue.length() == 5) {

            registrationVerifyMobileRequest = new RegistrationVerifyMobileRequest();

            registrationVerifyMobileRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
            registrationVerifyMobileRequest.setSmsToken(receivedSmsValue);

            verify_button.setEnabled(false);
            requestVerifyMobile = new RequestVerifyMobile(context, new RequestRegistrationVerifyMobileTaskCompleteListener());
            requestVerifyMobile.execute(registrationVerifyMobileRequest);

        }
    }

    @Override
    public void onBackPressed() {

        if (keyboard.getVisibility() == View.VISIBLE){
            new Collapse(keyboard).animate();
        }
        else {
            new HamPayDialog(activity).showExitRegistrationDialog();
        }
    }


    public class RequestRegistrationSendSmsTokenTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationSendSmsTokenResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsTokenResponse)
        {

            hamPayDialog.dismisWaitingDialog();

            if (registrationSendSmsTokenResponse != null) {
                if (registrationSendSmsTokenResponse.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Send Sms Token")
                            .setAction("Send")
                            .setLabel("Success")
                            .build());


                    numberProgressBar.setProgress(0);

                    countDownTimer = new CountDownTimer(181000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            numberProgressBar.incrementProgressBy(1);
                        }

                        public void onFinish() {

                            sendSmsCounter++;

                            if (sendSmsCounter < 3) {
                                sendSmsPermission = true;
                                Toast.makeText(context, getString(R.string.msg_fail_receive_sms), Toast.LENGTH_LONG).show();
                            }else {
                                sendSmsPermission = false;
                                Toast.makeText(context, getString(R.string.sms_upper_reach_sms), Toast.LENGTH_LONG).show();
                            }

                            resend_active_code.setVisibility(View.VISIBLE);

                        }
                    }.start();
                }else if (registrationSendSmsTokenResponse.getService().getResultStatus() == ResultStatus.REGISTRATION_INVALID_STEP){
                    new HamPayDialog(activity).showInvalidStepDialog();

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Send Sms Token")
                            .setAction("Send")
                            .setLabel("Success(Invalid)")
                            .build());
                }
                else {
                    requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(context, new RequestRegistrationSendSmsTokenTaskCompleteListener());
                    new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(requestRegistrationSendSmsToken, registrationSendSmsTokenRequest,
                            registrationSendSmsTokenResponse.getService().getResultStatus().getCode(),
                            registrationSendSmsTokenResponse.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Send Sms Token")
                            .setAction("Send")
                            .setLabel("Fail(Server)")
                            .build());
                }

            }else {
                requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(context, new RequestRegistrationSendSmsTokenTaskCompleteListener());
                new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(requestRegistrationSendSmsToken, registrationSendSmsTokenRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.mgs_fail_registration_send_sms_token));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Send Sms Token")
                        .setAction("Send")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() {   }
    }

}
