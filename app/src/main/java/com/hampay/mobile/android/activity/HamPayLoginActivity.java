package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.TACRequest;
import com.hampay.common.core.model.response.RegistrationVerifyAccountResponse;
import com.hampay.common.core.model.response.TACResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestConfirmUserData;
import com.hampay.mobile.android.async.RequestLogin;
import com.hampay.mobile.android.async.RequestTAC;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.AlertUtils;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.functions.DeviceUuidFactory;
import com.hampay.mobile.android.messaging.SecurityUtils;
import com.hampay.mobile.android.model.LoginData;
import com.hampay.mobile.android.model.LoginResponse;
import com.hampay.mobile.android.service.LoginService;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.DeviceInfo;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.UUID;


public class HamPayLoginActivity extends ActionBarActivity implements View.OnClickListener {

    SharedPreferences prefs;

    SharedPreferences.Editor editor;

    FacedTextView hampay_memorableword_text;
    String cellNumber = "";
    String memorableWord;
    String installationToken;



    ButtonRectangle digit_1;
    ButtonRectangle digit_2;
    ButtonRectangle digit_3;
    ButtonRectangle digit_4;
    ButtonRectangle digit_5;
    ButtonRectangle digit_6;
    ButtonRectangle digit_7;
    ButtonRectangle digit_8;
    ButtonRectangle digit_9;
    ButtonRectangle digit_0;
    ButtonRectangle guide_key;
    ButtonRectangle backspace;

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

    RelativeLayout loading_rl;


    public void contactUs(View view){
        (new HamPayDialog(this)).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ham_pay_login);

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        context = this;
        activity = HamPayLoginActivity.this;

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        password_holder = (LinearLayout)findViewById(R.id.password_holder);
        password_holder.setOnClickListener(this);


        digit_1 = (ButtonRectangle)findViewById(R.id.digit_1);
        digit_1.setOnClickListener(this);
        digit_2 = (ButtonRectangle)findViewById(R.id.digit_2);
        digit_2.setOnClickListener(this);
        digit_3 = (ButtonRectangle)findViewById(R.id.digit_3);
        digit_3.setOnClickListener(this);
        digit_4 = (ButtonRectangle)findViewById(R.id.digit_4);
        digit_4.setOnClickListener(this);
        digit_5 = (ButtonRectangle)findViewById(R.id.digit_5);
        digit_5.setOnClickListener(this);
        digit_6 = (ButtonRectangle)findViewById(R.id.digit_6);
        digit_6.setOnClickListener(this);
        digit_7 = (ButtonRectangle)findViewById(R.id.digit_7);
        digit_7.setOnClickListener(this);
        digit_8 = (ButtonRectangle)findViewById(R.id.digit_8);
        digit_8.setOnClickListener(this);
        digit_9 = (ButtonRectangle)findViewById(R.id.digit_9);
        digit_9.setOnClickListener(this);
        digit_0 = (ButtonRectangle)findViewById(R.id.digit_0);
        digit_0.setOnClickListener(this);
        guide_key = (ButtonRectangle)findViewById(R.id.resend_active_code);
        guide_key.setOnClickListener(this);
        backspace = (ButtonRectangle)findViewById(R.id.backspace);
        backspace.setOnClickListener(this);

        input_digit_1 = (ImageView)findViewById(R.id.input_digit_1);
        input_digit_2 = (ImageView)findViewById(R.id.input_digit_2);
        input_digit_3 = (ImageView)findViewById(R.id.input_digit_3);
        input_digit_4 = (ImageView)findViewById(R.id.input_digit_4);
        input_digit_5 = (ImageView)findViewById(R.id.input_digit_5);


        hampay_memorableword_text = (FacedTextView)findViewById(R.id.hampay_memorableword_text);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        cellNumber = prefs.getString(Constants.REGISTERED_CELL_NUMBER, "");
        memorableWord = prefs.getString(Constants.MEMORABLE_WORD, "");

        if (memorableWord != null) {
            hampay_memorableword_text.setText(memorableWord);
        }

        installationToken = UUID.randomUUID().toString();

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

                if (tacResponseMessage.getService().getShouldAcceptTAC()){

                    (new HamPayDialog(activity)).showTACAcceptDialog(tacResponseMessage.getService().getTac());

                }
                else {
                    Intent intent = new Intent();

                    intent.setClass(activity, MainActivity.class);
                    intent.putExtra(Constants.USER_PROFILE_DTO, tacResponseMessage.getService().getUserProfile());
                    startActivity(intent);

                    finish();
                }

            }
            else {
                Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onTaskPreRun() {
            loading_rl.setVisibility(View.VISIBLE);
        }
    }


    public class RequestLoginResponseTaskCompleteListener implements AsyncTaskCompleteListener<LoginResponse>
    {
        public RequestLoginResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(LoginResponse loginResponse)
        {
            loading_rl.setVisibility(View.GONE);
            if (loginResponse != null) {

                if ((loginResponse.getSuccessUrl().length() > 0)) {

                    editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
                    editor.putString(Constants.TOKEN_ID, loginResponse.getTokenId());
                    editor.commit();

                    TACRequest tacRequest = new TACRequest();
                    new RequestTAC(context, new RequestTACResponseTaskCompleteListener()).execute(tacRequest);
                }else {
                    new HamPayDialog(activity).showLoginFailDialog();
                }


            }
            else {
                Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
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
//                            new DeviceUuidFactory(this).getDeviceUuid().toString(),
                            new DeviceInfo(this).getIMEI(),
                            installationToken);

            LoginData loginData = new LoginData();

            loginData.setUserPassword(password);
            loginData.setUserName(cellNumber);

            new RequestLogin(context, new RequestLoginResponseTaskCompleteListener()).execute(loginData);

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
