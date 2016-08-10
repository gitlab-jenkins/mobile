package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Timer;
import java.util.TimerTask;

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
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.ScaleConverter;

public class SMSVerificationActivity extends AppCompatActivity implements View.OnClickListener{


    Activity activity;

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
    FacedTextView resend_active_code;
    LinearLayout progress_layout;
    RelativeLayout backspace;

    String receivedSmsValue = "";

    FacedTextView input_digit_1;
    FacedTextView input_digit_2;
    FacedTextView input_digit_3;
    FacedTextView input_digit_4;
//    FacedTextView input_digit_5;

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
    private FacedTextView remain_timer;
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
//        stopTimerTask();
        if (requestVerifyMobile != null){
            if (!requestVerifyMobile.isCancelled())
                requestVerifyMobile.cancel(true);
        }
    }


    RelativeLayout.LayoutParams params;
    View reached_progress;

    int timeCounter = 0;
    float screenWidthPercentage = 0;

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    public void startTimer() {
        timeCounter = 0;
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000);
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {

            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeCounter += 1;
                        params.width = (int)(screenWidthPercentage * timeCounter);
                        reached_progress.setLayoutParams(params);
                        minutes =  (int)((180 - timeCounter) / (60));
                        seconds = (int)(180 - timeCounter) % 60;
                        remain_timer.setText(persianEnglishDigit.E2P(String.format("%02d:%02d", minutes, seconds)));

                        if (timeCounter >= 180){
                            stopTimerTask();
                            sendSmsPermission = true;
                            progress_layout.setVisibility(View.GONE);
                            resend_active_code.setVisibility(View.VISIBLE);
                            if (keyboard.getVisibility() != View.VISIBLE)
                                new Expand(keyboard).animate();
                        }
                    }
                });

                handler.post(new Runnable() {
                    public void run() {

                    }
                });
            }
        };
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void userManual(View view){
        Intent intent = new Intent();
        intent.setClass(activity, UserManualActivity.class);
        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_sms_verification);
        intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_sms_verification);
        startActivity(intent);
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

                input_digit_1.setText(persianEnglishDigit.E2P(receivedSmsValue.substring(0, 1)));
                input_digit_1.setBackgroundColor(Color.TRANSPARENT);
                input_digit_2.setText(persianEnglishDigit.E2P(receivedSmsValue.substring(1, 2)));
                input_digit_2.setBackgroundColor(Color.TRANSPARENT);
                input_digit_3.setText(persianEnglishDigit.E2P(receivedSmsValue.substring(2, 3)));
                input_digit_3.setBackgroundColor(Color.TRANSPARENT);
                input_digit_4.setText(persianEnglishDigit.E2P(receivedSmsValue.substring(3, 4)));
                input_digit_4.setBackgroundColor(Color.TRANSPARENT);
                if (receivedSmsValue.length() == 4) {
                    registrationVerifyMobileRequest = new RegistrationVerifyMobileRequest();
                    registrationVerifyMobileRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                    registrationVerifyMobileRequest.setSmsToken(receivedSmsValue);
                    receivedSmsValue = "";
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


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidthPercentage = (size.x - ScaleConverter.dpToPx(16)) / 180f;

        reached_progress = (View)findViewById(R.id.reached_progress);

        params= (RelativeLayout.LayoutParams) reached_progress.getLayoutParams();

        startTimer();
        remain_timer = (FacedTextView)findViewById(R.id.remain_timer);

        bundle = getIntent().getExtras();

        activity = SMSVerificationActivity.this;

        hamPayDialog = new HamPayDialog(activity);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        activation_holder = (LinearLayout)findViewById(R.id.activation_holder);
        activation_holder.setOnClickListener(this);

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
        backspace = (RelativeLayout) findViewById(R.id.backspace);
        backspace.setOnClickListener(this);
        resend_active_code = (FacedTextView)findViewById(R.id.resend_active_code);
        resend_active_code.setOnClickListener(this);

        progress_layout = (LinearLayout)findViewById(R.id.progress_layout);


        input_digit_1 = (FacedTextView)findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView)findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView)findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView)findViewById(R.id.input_digit_4);
//        input_digit_5 = (FacedTextView)findViewById(R.id.input_digit_5);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        registrationSendSmsTokenRequest = new RegistrationSendSmsTokenRequest();
        registrationSendSmsTokenRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));

    }

    public class RequestRegistrationVerifyMobileTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyMobileResponse>>
    {
        @Override
        public void onTaskComplete(ResponseMessage<RegistrationVerifyMobileResponse> registrationVerifyMobileResponseMessage)
        {

            input_digit_1.setText("");
            input_digit_2.setText("");
            input_digit_3.setText("");
            input_digit_4.setText("");

            hamPayDialog.dismisWaitingDialog();
            if (registrationVerifyMobileResponseMessage != null) {

                if (registrationVerifyMobileResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    stopTimerTask();

                    if (registrationVerifyMobileResponseMessage.getService().getIsVerified()) {

                        Intent intent = new Intent();
                        intent.setClass(SMSVerificationActivity.this, PasswordEntryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
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
                    new HamPayDialog(activity).showFailRegistrationVerifyMobileDialog(registrationVerifyMobileResponseMessage.getService().getResultStatus().getCode(),
                            registrationVerifyMobileResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Verify Mobile")
                            .setAction("Verify")
                            .setLabel("Fail(Server)")
                            .build());
                }

            }else {
                requestVerifyMobile = new RequestVerifyMobile(context, new RequestRegistrationVerifyMobileTaskCompleteListener());
                new HamPayDialog(activity).showFailRegistrationVerifyMobileDialog(Constants.LOCAL_ERROR_CODE,
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
            hamPayDialog.showWaitingDialog("");
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
                sendSmsCounter++;
                if (sendSmsCounter < 3) {
                    if (sendSmsPermission) {
                        sendSmsPermission = false;
                        hamPayDialog.showWaitingDialog("");
                        requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(context, new RequestRegistrationSendSmsTokenTaskCompleteListener());
                        requestRegistrationSendSmsToken.execute(registrationSendSmsTokenRequest);
                    }
                }else {
                    Toast.makeText(context, getString(R.string.sms_upper_reach_sms), Toast.LENGTH_LONG).show();
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
                    } else {
                        input_digit_1.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_2.setText("");
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    break;

                case 1:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_2.setText("");
                    } else {
                        input_digit_2.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    break;
                case 2:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_3.setText("");
                    } else {
                        input_digit_3.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_4.setText("");
                    break;
                case 3:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_4.setText("");
                    } else {
                        input_digit_4.setText(persianEnglishDigit.E2P(digit));
                    }
                    break;
                case 4:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_4.setText("");
                    } else {
                        input_digit_4.setText(persianEnglishDigit.E2P(digit));
                    }
                    break;
            }

        }

        if (digit.contains("d")){
            if (receivedSmsValue.length() > 0) {
                receivedSmsValue = receivedSmsValue.substring(0, receivedSmsValue.length() - 1);
                if (receivedSmsValue.length() == 3){
                    input_digit_4.setText("");
                }
                else if (receivedSmsValue.length() == 2){
                    input_digit_3.setText("");
                }
                else if (receivedSmsValue.length() == 1){
                    input_digit_2.setText("");
                }
                else if (receivedSmsValue.length() == 0){
                    input_digit_1.setText("");
                }
            }
        }
        else {
            if (receivedSmsValue.length() <= 4) {
                receivedSmsValue += digit;
            }
        }

        if (receivedSmsValue.length() == 4) {

            registrationVerifyMobileRequest = new RegistrationVerifyMobileRequest();

            registrationVerifyMobileRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
            registrationVerifyMobileRequest.setSmsToken(receivedSmsValue);
            receivedSmsValue = "";
            requestVerifyMobile = new RequestVerifyMobile(context, new RequestRegistrationVerifyMobileTaskCompleteListener());
            requestVerifyMobile.execute(registrationVerifyMobileRequest);

        }
    }

    public class RequestRegistrationSendSmsTokenTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationSendSmsTokenResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsTokenResponse)
        {

            hamPayDialog.dismisWaitingDialog();

            if (registrationSendSmsTokenResponse != null) {
                if (registrationSendSmsTokenResponse.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    resend_active_code.setVisibility(View.GONE);
                    progress_layout.setVisibility(View.VISIBLE);
                    startTimer();

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Send Sms Token")
                            .setAction("Send")
                            .setLabel("Success")
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
    @Override
    public void onBackPressed() {
        if (keyboard.getVisibility() == View.VISIBLE){
            new Collapse(keyboard).animate();
        }
        else {
            new HamPayDialog(activity).exitRegistrationDialog();
        }
    }
}