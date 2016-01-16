package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.TACRequest;
import xyz.homapay.hampay.common.core.model.response.TACResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestLogin;
import xyz.homapay.hampay.mobile.android.async.RequestTAC;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.material.RippleView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.FailedLoginResponse;
import xyz.homapay.hampay.mobile.android.model.LoginData;
import xyz.homapay.hampay.mobile.android.model.SuccessLoginResponse;
import xyz.homapay.hampay.mobile.android.util.AppInfo;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.SecurityUtils;


public class HamPayLoginActivity extends Activity implements View.OnClickListener {



    RequestLogin requestLogin;

    FacedTextView hampay_memorableword_text;
    String nationalCode = "";
    String memorableWord;
    String installationToken;



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
    RippleView keyboard_help;
    RippleView backspace;

    String inputPassValue = "";

    ImageView input_digit_1;
    ImageView input_digit_2;
    ImageView input_digit_3;
    ImageView input_digit_4;
    ImageView input_digit_5;

    LinearLayout keyboard;
    LinearLayout password_holder;

    Context context;
    Activity activity;

    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    HamPayDialog hamPayDialog;

    FacedTextView user_name;

    TACRequest tacRequest;
    RequestTAC requestTAC;

    Tracker hamPayGaTracker;

    Bundle bundle;

    boolean fromNotification = false;

    String password = "";

    public void contactUs(View view){
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/login.html");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ham_pay_login);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        editor.putBoolean(Constants.FETCHED_HAMPAY_ENABLED, false);
        editor.commit();

        user_name = (FacedTextView)findViewById(R.id.user_name);
        user_name.setText("سلام: " + prefs.getString(Constants.REGISTERED_USER_FAMILY, ""));


        bundle = getIntent().getExtras();

        if (bundle != null){
            fromNotification = bundle.getBoolean(Constants.NOTIFICATION);
        }



        context = this;
        activity = HamPayLoginActivity.this;

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        password_holder = (LinearLayout)findViewById(R.id.password_holder);
        password_holder.setOnClickListener(this);


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
        keyboard_help = (RippleView)findViewById(R.id.keyboard_help);
        keyboard_help.setOnClickListener(this);
        backspace = (RippleView)findViewById(R.id.backspace);
        backspace.setOnClickListener(this);

        input_digit_1 = (ImageView)findViewById(R.id.input_digit_1);
        input_digit_2 = (ImageView)findViewById(R.id.input_digit_2);
        input_digit_3 = (ImageView)findViewById(R.id.input_digit_3);
        input_digit_4 = (ImageView)findViewById(R.id.input_digit_4);
        input_digit_5 = (ImageView)findViewById(R.id.input_digit_5);


        hampay_memorableword_text = (FacedTextView)findViewById(R.id.hampay_memorableword_text);

        nationalCode = prefs.getString(Constants.REGISTERED_NATIONAL_CODE, "");

        memorableWord = prefs.getString(Constants.MEMORABLE_WORD, "");

        if (memorableWord != null) {
            hampay_memorableword_text.setText(memorableWord);
        }

        installationToken = prefs.getString(Constants.UUID, "");

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (requestTAC != null){
            if (!requestTAC.isCancelled())
                requestTAC.cancel(true);
        }

        if (requestLogin != null){
            if (!requestLogin.isCancelled()){
                requestLogin.cancel(true);
            }
        }

    }

    public class RequestTACResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<TACResponse>>
    {
        public RequestTACResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<TACResponse> tacResponseMessage)
        {
            hamPayDialog.dismisWaitingDialog();
            if (tacResponseMessage != null) {

                if (tacResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    editor.putString(Constants.USER_ID_TOKEN, tacResponseMessage.getService().getUserIdToken());
                    editor.commit();

                    if (tacResponseMessage.getService().getShouldAcceptTAC()) {

                        //Remove below line
                        editor.putBoolean(Constants.DISMIS_TAC, false).commit();

                        (new HamPayDialog(activity)).showTACAcceptDialog(tacResponseMessage.getService().getTac());

                    } else {
                        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                        editor.commit();
                        Intent intent = new Intent();
                        intent.setClass(activity, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(Constants.USER_PROFILE_DTO, tacResponseMessage.getService().getUserProfile());
                        intent.putExtra(Constants.PENDING_PURCHASE_PAYMENT, tacResponseMessage.getService().getPurchaseRequestId());
                        intent.putExtra(Constants.NOTIFICATION, fromNotification);
                        editor.putBoolean(Constants.FORCE_USER_PROFILE, false);
                        editor.commit();
                        finish();
                        startActivity(intent);
                    }

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Request TAC")
                            .setAction("Request")
                            .setLabel("Success")
                            .build());

                }else {
                    requestTAC = new RequestTAC(context, new RequestTACResponseTaskCompleteListener());
                    new HamPayDialog(activity).showFailTCRequestDialog(requestTAC, tacRequest,
                            tacResponseMessage.getService().getResultStatus().getCode(),
                            tacResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Request TAC")
                            .setAction("Request")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }
            else {
                requestTAC = new RequestTAC(context, new RequestTACResponseTaskCompleteListener());
                new HamPayDialog(activity).showFailTCRequestDialog(requestTAC, tacRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_tac_request));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Request TAC")
                        .setAction("Request")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }


    public class RequestLoginResponseTaskCompleteListener implements AsyncTaskCompleteListener<String>
    {
        public RequestLoginResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(String loginResponse)
        {

            SuccessLoginResponse successLoginResponse;
            FailedLoginResponse failedLoginResponse;

            hamPayDialog.dismisWaitingDialog();
            if (loginResponse != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<SuccessLoginResponse>() {}.getType();
                JsonParser jsonParser = new JsonParser();
                JsonElement responseElement = jsonParser.parse(loginResponse.toString());
                successLoginResponse = gson.fromJson(responseElement.toString(), listType);

                if (successLoginResponse == null || successLoginResponse.getSuccessUrl() == null) {
                    listType = new TypeToken<FailedLoginResponse>() {}.getType();
                    jsonParser = new JsonParser();
                    responseElement = jsonParser.parse(loginResponse.toString());
                    failedLoginResponse = gson.fromJson(responseElement.toString(), listType);
                    if (failedLoginResponse != null) {

                        if (failedLoginResponse.getMessage().equalsIgnoreCase(Constants.USER_ACCOUNT_LOCKET)){
                            failedLoginResponse.setMessage(getString(R.string.msg_locked_hampay_login));
                        }else {
                            failedLoginResponse.setMessage(getString(R.string.msg_fail_hampay_login));
                        }

                        new HamPayDialog(activity).showLoginFailDialog(failedLoginResponse);

                        hamPayGaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("User Login")
                                .setAction("Login")
                                .setLabel("Fail(Server)")
                                .build());
                    }else {
                        failedLoginResponse = new FailedLoginResponse();
                        failedLoginResponse.setCode(Constants.LOCAL_ERROR_CODE);
                        failedLoginResponse.setMessage(getString(R.string.msg_fail_hampay_server));
                        new HamPayDialog(activity).showLoginFailDialog(failedLoginResponse);

                        hamPayGaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("User Login")
                                .setAction("Login")
                                .setLabel("Fail(Mobile)")
                                .build());
                    }
                }else {
                    editor.putString(Constants.LOGIN_TOKEN_ID, successLoginResponse.getTokenId());
                    editor.commit();
                    tacRequest = new TACRequest();
                    tacRequest.setDeviceId(new DeviceInfo(context).getAndroidId());
                    tacRequest.setAppVersion(new AppInfo(context).getVersionCode() + "");
                    requestTAC = new RequestTAC(context, new RequestTACResponseTaskCompleteListener());
                    requestTAC.execute(tacRequest);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("User Login")
                            .setAction("Login")
                            .setLabel("Success")
                            .build());
                }
            }else {
                failedLoginResponse = new FailedLoginResponse();
                failedLoginResponse.setCode(Constants.LOCAL_ERROR_CODE);
                failedLoginResponse.setMessage(getString(R.string.msg_fail_hampay_server));
                new HamPayDialog(activity).showLoginFailDialog(failedLoginResponse);

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("User Login")
                        .setAction("Login")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
    }

    @Override
    public void onTaskPreRun() {
        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
    }
}



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.password_holder:

                if (keyboard.getVisibility() != View.VISIBLE)
                    new Expand(keyboard).animate();

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

            case R.id.keyboard_help:
                new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/login.html");
                break;
        }
    }


    private void inputDigit(String digit){


        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        if (digit.contains("d")){
            if (inputPassValue.length() > 0) {
                inputPassValue = inputPassValue.substring(0, inputPassValue.length() - 1);
            }
        }
        else {
            if (inputPassValue.length() <= 4) {
                inputPassValue += digit;
            }
        }

        if (inputPassValue.length() == 5){

//            nationalCode = "testUser";

            try {

                password = SecurityUtils.getInstance(this).
                        generatePassword(inputPassValue,
                                memorableWord,
                                new DeviceInfo(context).getAndroidId(),
                                installationToken);

//                password = "12345678";

            }catch (NoSuchAlgorithmException ex){}
            catch (UnsupportedEncodingException ex){}

            LoginData loginData = new LoginData();

            loginData.setUserPassword(password);
            loginData.setUserName(new PersianEnglishDigit(nationalCode).P2E());

            keyboard.setEnabled(false);

            //Remove below lines
//            editor.putString(Constants.LOGIN_TOKEN_ID, /*successLoginResponse.getTokenId()*/"aaaaaaaaaaaaaaaaaaaaaaaaaaa");
//            editor.commit();
//            if (prefs.getBoolean(Constants.DISMIS_TAC, true)) {
//                tacRequest = new TACRequest();
//                tacRequest.setDeviceId(new DeviceInfo(context).getAndroidId());
//                tacRequest.setAppVersion(new AppInfo(context).getVersionCode() + "");
//                requestTAC = new RequestTAC(context, new RequestTACResponseTaskCompleteListener());
//                requestTAC.execute(tacRequest);
//            }
            //Until here

            requestLogin = new RequestLogin(context, new RequestLoginResponseTaskCompleteListener());
            requestLogin.execute(loginData);


            inputPassValue = "";
            input_digit_1.setImageResource(R.drawable.pass_icon_2);
            input_digit_2.setImageResource(R.drawable.pass_icon_2);
            input_digit_3.setImageResource(R.drawable.pass_icon_2);
            input_digit_4.setImageResource(R.drawable.pass_icon_2);
            input_digit_5.setImageResource(R.drawable.pass_icon_2);

        }


        switch (inputPassValue.length()){

            case 0:
                input_digit_1.setImageResource(R.drawable.pass_icon_2);
                input_digit_2.setImageResource(R.drawable.pass_icon_2);
                input_digit_3.setImageResource(R.drawable.pass_icon_2);
                input_digit_4.setImageResource(R.drawable.pass_icon_2);
                input_digit_5.setImageResource(R.drawable.pass_icon_2);
                vibrator.vibrate(20);
                break;

            case 1:
                input_digit_1.setImageResource(R.drawable.pass_icon_1);
                input_digit_2.setImageResource(R.drawable.pass_icon_2);
                input_digit_3.setImageResource(R.drawable.pass_icon_2);
                input_digit_4.setImageResource(R.drawable.pass_icon_2);
                input_digit_5.setImageResource(R.drawable.pass_icon_2);
                vibrator.vibrate(20);

                break;
            case 2:
                input_digit_1.setImageResource(R.drawable.pass_icon_1);
                input_digit_2.setImageResource(R.drawable.pass_icon_1);
                input_digit_3.setImageResource(R.drawable.pass_icon_2);
                input_digit_4.setImageResource(R.drawable.pass_icon_2);
                input_digit_5.setImageResource(R.drawable.pass_icon_2);
                vibrator.vibrate(20);
                break;
            case 3:
                input_digit_1.setImageResource(R.drawable.pass_icon_1);
                input_digit_2.setImageResource(R.drawable.pass_icon_1);
                input_digit_3.setImageResource(R.drawable.pass_icon_1);
                input_digit_4.setImageResource(R.drawable.pass_icon_2);
                input_digit_5.setImageResource(R.drawable.pass_icon_2);
                vibrator.vibrate(20);
                break;
            case 4:
                input_digit_1.setImageResource(R.drawable.pass_icon_1);
                input_digit_2.setImageResource(R.drawable.pass_icon_1);
                input_digit_3.setImageResource(R.drawable.pass_icon_1);
                input_digit_4.setImageResource(R.drawable.pass_icon_1);
                input_digit_5.setImageResource(R.drawable.pass_icon_2);
                vibrator.vibrate(20);
                break;
            case 5:
                input_digit_1.setImageResource(R.drawable.pass_icon_1);
                input_digit_2.setImageResource(R.drawable.pass_icon_1);
                input_digit_3.setImageResource(R.drawable.pass_icon_1);
                input_digit_4.setImageResource(R.drawable.pass_icon_1);
                input_digit_5.setImageResource(R.drawable.pass_icon_1);
                vibrator.vibrate(20);
                break;
        }

    }


    @Override
    public void onBackPressed() {
        if (keyboard.getVisibility() == View.VISIBLE){
            new Collapse(keyboard).animate();
        }
        else {
            new HamPayDialog(activity).showExitLoginDialog();
        }
    }
}
