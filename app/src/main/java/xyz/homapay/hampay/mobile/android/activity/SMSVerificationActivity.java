package xyz.homapay.hampay.mobile.android.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Manifest;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationSendSmsToken;
import xyz.homapay.hampay.mobile.android.async.RequestVerifyMobile;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.ActionPermission;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionContactDialog;
import xyz.homapay.hampay.mobile.android.dialog.sms.ActionSMS;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.ScaleConverter;

public class SMSVerificationActivity extends AppCompatActivity implements View.OnClickListener, PermissionContactDialog.PermissionContactDialogListener{

    private Activity activity;
    private FacedTextView digit_1;
    private FacedTextView digit_2;
    private FacedTextView digit_3;
    private FacedTextView digit_4;
    private FacedTextView digit_5;
    private FacedTextView digit_6;
    private FacedTextView digit_7;
    private FacedTextView digit_8;
    private FacedTextView digit_9;
    private FacedTextView digit_0;
    private FacedTextView keyboard_dismiss;
    private FacedTextView resend_active_code;
    private LinearLayout progress_layout;
    private RelativeLayout backspace;
    private String receivedSmsValue = "";
    private FacedTextView input_digit_1;
    private FacedTextView input_digit_2;
    private FacedTextView input_digit_3;
    private FacedTextView input_digit_4;
    private Context context;
    private ArrayList<PermissionListener> permissionListeners = new ArrayList<>();
    private RequestRegistrationSendSmsToken requestRegistrationSendSmsToken;
    private RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest;
    private LinearLayout keyboard;
    private LinearLayout activation_holder;
    private SharedPreferences.Editor editor;
    private HamPayDialog hamPayDialog;
    private SharedPreferences prefs;
    private RequestVerifyMobile requestVerifyMobile;
    private RegistrationVerifyMobileRequest registrationVerifyMobileRequest;
    private RelativeLayout.LayoutParams params;
    private View reached_progress;
    private int timeCounter = 0;
    private float screenWidthPercentage = 0;
    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();
    private FacedTextView remain_timer;
    private boolean sendSmsPermission = false;
    private int sendSmsCounter = 0;
    private String cellNumber;
    private FacedTextView sms_delivery_text;
    private boolean smsVerified = false;


    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
        if (requestVerifyMobile != null){
            if (!requestVerifyMobile.isCancelled())
                requestVerifyMobile.cancel(true);
        }
    }

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
                        }
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

    public void backActionBar(View view){
        finish();
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
    }

    Bundle bundle;

    private int minutes = 0;
    private int seconds = 0;
    private PersianEnglishDigit persianEnglishDigit;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);
        context = this;
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

        cellNumber = bundle.getString(Constants.REGISTERED_CELL_NUMBER);
        sms_delivery_text = (FacedTextView)findViewById(R.id.sms_delivery_text);
        sms_delivery_text.setText(getString(R.string.deliver_verification, persianEnglishDigit.E2P(cellNumber)));

        progress_layout = (LinearLayout)findViewById(R.id.progress_layout);

        input_digit_1 = (FacedTextView)findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView)findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView)findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView)findViewById(R.id.input_digit_4);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        registrationSendSmsTokenRequest = new RegistrationSendSmsTokenRequest();
        registrationSendSmsTokenRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        for (PermissionListener permissionListener : permissionListeners)
            if (permissionListener.onResult(requestCode, permissions, grantResults)) {
                permissionListeners.remove(permissionListener);
            }
    }



    private void requestAndLoadUserContact() {
        String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};

        permissionListeners = new RequestPermissions().request(activity, Constants.READ_CONTACTS, permissions, new PermissionListener() {
            @Override
            public boolean onResult(int requestCode, String[] requestPermissions, int[] grantResults) {
                if (requestCode == Constants.READ_CONTACTS) {
                    if (grantResults.length > 0 && requestPermissions[0].equals(Manifest.permission.READ_CONTACTS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (smsVerified) {
                            Intent intent = new Intent();
                            intent.setClass(SMSVerificationActivity.this, PasswordEntryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                            finish();
                            startActivity(intent);
                        }
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                            if (showRationale){
                                handler.post(new Runnable() {
                                    public void run() {
                                        PermissionContactDialog permissionContactDialog = new PermissionContactDialog();
                                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                        fragmentTransaction.add(permissionContactDialog, null);
                                        fragmentTransaction.commitAllowingStateLoss();
                                    }
                                });
                            }else {
                                if (smsVerified) {
                                    Intent intent = new Intent();
                                    intent.setClass(activity, PasswordEntryActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    startActivity(intent);
                                }
                            }
                        }else {
                            handler.post(new Runnable() {
                                public void run() {
                                    PermissionContactDialog permissionContactDialog = new PermissionContactDialog();
                                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.add(permissionContactDialog, null);
                                    fragmentTransaction.commitAllowingStateLoss();
                                }
                            });
                        }
                    }
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onFinishEditDialog(ActionPermission actionPermission) {
        switch (actionPermission){
            case GRANT:
                requestAndLoadUserContact();
                break;
            case DENY:
                if (smsVerified) {
                    Intent intent = new Intent();
                    intent.setClass(activity, PasswordEntryActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);
                }
                break;
        }
    }



    public class RequestRegistrationVerifyMobileTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyMobileResponse>>
    {
        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);

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
                    smsVerified = true;
                    serviceName = ServiceEvent.REGISTRATION_VERIFY_MOBILE_SUCCESS;
                    stopTimerTask();
                    if (registrationVerifyMobileResponseMessage.getService().getIsVerified()) {

                        requestAndLoadUserContact();

                    } else {
                        new HamPayDialog(activity).showIncorrectSMSVerification();
                    }
                }else {
                    serviceName = ServiceEvent.REGISTRATION_VERIFY_MOBILE_FAILURE;
                    requestVerifyMobile = new RequestVerifyMobile(context, new RequestRegistrationVerifyMobileTaskCompleteListener());
                    new HamPayDialog(activity).showFailRegistrationVerifyMobileDialog(registrationVerifyMobileResponseMessage.getService().getResultStatus().getCode(),
                            registrationVerifyMobileResponseMessage.getService().getResultStatus().getDescription());
                }

            }else {
                serviceName = ServiceEvent.REGISTRATION_VERIFY_MOBILE_FAILURE;
                requestVerifyMobile = new RequestVerifyMobile(context, new RequestRegistrationVerifyMobileTaskCompleteListener());
                new HamPayDialog(activity).showFailRegistrationVerifyMobileDialog(Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_send_sms));
            }
            logEvent.log(serviceName);
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
            smsVerified = false;
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

            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

            hamPayDialog.dismisWaitingDialog();

            if (registrationSendSmsTokenResponse != null) {
                if (registrationSendSmsTokenResponse.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.REGISTRATION_SEND_SMS_TOKEN_SUCCESS;
                    resend_active_code.setVisibility(View.GONE);
                    progress_layout.setVisibility(View.VISIBLE);
                    startTimer();
                }
                else {
                    serviceName = ServiceEvent.REGISTRATION_SEND_SMS_TOKEN_FAILURE;
                    requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(context, new RequestRegistrationSendSmsTokenTaskCompleteListener());
                    new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(requestRegistrationSendSmsToken, registrationSendSmsTokenRequest,
                            registrationSendSmsTokenResponse.getService().getResultStatus().getCode(),
                            registrationSendSmsTokenResponse.getService().getResultStatus().getDescription());
                }

            }else {
                serviceName = ServiceEvent.REGISTRATION_SEND_SMS_TOKEN_FAILURE;
                requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(context, new RequestRegistrationSendSmsTokenTaskCompleteListener());
                new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(requestRegistrationSendSmsToken, registrationSendSmsTokenRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.mgs_fail_registration_send_sms_token));
            }
            logEvent.log(serviceName);
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
            finish();
        }
    }
}