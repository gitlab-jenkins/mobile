package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.TACRequest;
import com.hampay.common.core.model.response.TACResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestLogin;
import com.hampay.mobile.android.async.RequestTAC;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.RippleView;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.model.FailedLoginResponse;
import com.hampay.mobile.android.model.LoginData;
import com.hampay.mobile.android.model.SuccessLoginResponse;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.DeviceInfo;
import com.hampay.mobile.android.util.SecurityUtils;

import java.lang.reflect.Type;


public class HamPayLoginActivity extends ActionBarActivity implements View.OnClickListener {

    SharedPreferences.Editor editor;

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

    SharedPreferences prefs;

    RelativeLayout loading_rl;
    FacedTextView user_name;

    TACRequest tacRequest;
    RequestTAC requestTAC;


    public void contactUs(View view){
        (new HamPayDialog(this)).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ham_pay_login);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        user_name = (FacedTextView)findViewById(R.id.user_name);
        user_name.setText("سلام: " + prefs.getString(Constants.REGISTERED_USER_FAMILY, ""));

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        context = this;
        activity = HamPayLoginActivity.this;

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

        installationToken = prefs.getString("UUID", "");

    }

    private ResponseMessage<TACResponse> tACResponse;

    public class RequestTACResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<TACResponse>>
    {
        public RequestTACResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<TACResponse> tacResponseMessage)
        {
            loading_rl.setVisibility(View.GONE);
            if (tacResponseMessage != null) {

                if (tacResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    if (tacResponseMessage.getService().getShouldAcceptTAC()) {

                        (new HamPayDialog(activity)).showTACAcceptDialog(tacResponseMessage.getService().getTac());

                    } else {
                        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                        editor.commit();
                        Intent intent = new Intent();
                        intent.setClass(activity, MainActivity.class);
                        intent.putExtra(Constants.USER_PROFILE_DTO, tacResponseMessage.getService().getUserProfile());
                        startActivity(intent);
                        finish();
                    }
                }else {
                    requestTAC = new RequestTAC(context, new RequestTACResponseTaskCompleteListener());
                    new HamPayDialog(activity).showFailTCRequestDialog(requestTAC, tacRequest,
                            tacResponseMessage.getService().getResultStatus().getCode(),
                            tacResponseMessage.getService().getResultStatus().getDescription());
                }
            }
            else {
                requestTAC = new RequestTAC(context, new RequestTACResponseTaskCompleteListener());
                new HamPayDialog(activity).showFailTCRequestDialog(requestTAC, tacRequest,
                        "2000",
                        getString(R.string.msg_fail_tac_request));
            }

        }

        @Override
        public void onTaskPreRun() {
            loading_rl.setVisibility(View.VISIBLE);
        }
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

            loading_rl.setVisibility(View.GONE);
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
                        failedLoginResponse.setMessage(getString(R.string.msg_fail_hampay_login));
                        new HamPayDialog(activity).showLoginFailDialog(failedLoginResponse);
                    }else {
                        failedLoginResponse = new FailedLoginResponse();
                        failedLoginResponse.setCode(-1);
                        failedLoginResponse.setMessage(getString(R.string.msg_fail_hampay_server));
                        new HamPayDialog(activity).showLoginFailDialog(failedLoginResponse);
                    }
                }else {

                    editor.putString(Constants.LOGIN_TOKEN_ID, successLoginResponse.getTokenId());
                    editor.commit();
                    tacRequest = new TACRequest();
                    requestTAC = new RequestTAC(context, new RequestTACResponseTaskCompleteListener());
                    requestTAC.execute(tacRequest);
                }
            }else {
                failedLoginResponse = new FailedLoginResponse();
                failedLoginResponse.setCode(-1);
                failedLoginResponse.setMessage(getString(R.string.msg_fail_hampay_server));
                new HamPayDialog(activity).showLoginFailDialog(failedLoginResponse);
            }
    }

    @Override
    public void onTaskPreRun() {
        loading_rl.setVisibility(View.VISIBLE);
    }
}



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.password_holder:
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

            String password = SecurityUtils.getInstance(this).
                    generatePassword(inputPassValue,
                            memorableWord,
                            new DeviceInfo(context).getAndroidId(),
                            installationToken);

            LoginData loginData = new LoginData();

            loginData.setUserPassword(password);
            loginData.setUserName(nationalCode);

            new RequestLogin(context, new RequestLoginResponseTaskCompleteListener()).execute(loginData);


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


    private void sendLoginRequest() {


//        AlertUtils.getInstance().showProgressDialog(this);
//
//
//        LoginService service = new LoginService(this);
////        service.sendLoginRequest("87378fbf3a67463dac9829256f26270a", passCode);
//        String memorableWord = "";
//
//        String userId = "";
//
//        String installationToken = "";
//
//        cls
//
//        String password = SecurityUtils.getInstance(this).
//                generatePassword(inputPassValue,
//                        memorableWord,
//                        new DeviceUuidFactory(this).getDeviceUuid().toString(),
//                        installationToken);
//
//        service.sendLoginRequest(userId, password);
    }
}
