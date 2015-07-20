package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import com.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestVerifyMobile;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.NetworkConnectivity;

public class SMSVerificationActivity extends ActionBarActivity implements View.OnClickListener{


    Activity activity;

    ButtonRectangle verify_button;

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
    ButtonRectangle resend_active_code;
    ButtonRectangle backspace;

    String receivedSmsValue = "";

    FacedTextView input_digit_1;
    FacedTextView input_digit_2;
    FacedTextView input_digit_3;
    FacedTextView input_digit_4;
    FacedTextView input_digit_5;

    NetworkConnectivity networkConnectivity;
    Context context;

    LinearLayout keyboard;
    LinearLayout activation_holder;

    SharedPreferences.Editor editor;

    RelativeLayout loading_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        activity = SMSVerificationActivity.this;

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, SMSVerificationActivity.class.toString());
        editor.commit();

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        activation_holder = (LinearLayout)findViewById(R.id.activation_holder);
        activation_holder.setOnClickListener(this);

        editor.commit();



        networkConnectivity = new NetworkConnectivity(this);
        context = this;

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
        resend_active_code = (ButtonRectangle)findViewById(R.id.resend_active_code);
        resend_active_code.setOnClickListener(this);
        backspace = (ButtonRectangle)findViewById(R.id.backspace);
        backspace.setOnClickListener(this);

        input_digit_1 = (FacedTextView)findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView)findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView)findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView)findViewById(R.id.input_digit_4);
        input_digit_5 = (FacedTextView)findViewById(R.id.input_digit_5);


        verify_button = (ButtonRectangle)findViewById(R.id.verify_button);
        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (receivedSmsValue.length() == 5) {

                    if (networkConnectivity.isNetworkConnected()) {

                        RegistrationVerifyMobileRequest registrationVerifyMobileRequest = new RegistrationVerifyMobileRequest();
                        SharedPreferences prefs = getPreferences(MODE_PRIVATE);

                        registrationVerifyMobileRequest.setUserIdToken(prefs.getString("UserIdToken", ""));
                        registrationVerifyMobileRequest.setSmsToken(receivedSmsValue);

                        verify_button.setEnabled(false);
                        new RequestVerifyMobile(context, new RequestRegistrationVerifyMobileTaskCompleteListener()).execute(registrationVerifyMobileRequest);
                    } else {
                        Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, getString(R.string.msg_fail_sms_verification), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void contactUs(View view){
        new HamPayDialog(this).showContactUsDialog();
    }

    public class RequestRegistrationVerifyMobileTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyMobileResponse>>
    {
        @Override
        public void onTaskComplete(ResponseMessage<RegistrationVerifyMobileResponse> registrationVerifyMobileResponseMessage)
        {

            verify_button.setEnabled(true);
            loading_rl.setVisibility(View.GONE);
            if (registrationVerifyMobileResponseMessage != null) {
                Intent intent = new Intent();
                intent.setClass(SMSVerificationActivity.this, ConfirmAccountNoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskPreRun() {
            keyboard.setVisibility(View.GONE);
            loading_rl.setVisibility(View.VISIBLE);
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

        if (receivedSmsValue.length() <= 4) {

            switch (receivedSmsValue.length()) {

                case 0:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_1.setText("");
                    } else {
                        input_digit_1.setText(digit);
                    }
                    input_digit_2.setText("");
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    vibrator.vibrate(20);
                    break;

                case 1:
//                    input_digit_1.setText("");
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_2.setText("");
                    } else {
                        input_digit_2.setText(digit);
                    }
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    vibrator.vibrate(20);

                    break;
                case 2:
//                input_digit_1.setText("");
//                input_digit_2.setText("");
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_3.setText("");
                    } else {
                        input_digit_3.setText(digit);
                    }
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    vibrator.vibrate(20);
                    break;
                case 3:
//                input_digit_1.setText("");
//                input_digit_2.setText("");
//                    input_digit_3.setText("");
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_4.setText("");
                    } else {
                        input_digit_4.setText(digit);
                    }
//                    input_digit_4.setText("");
//                    input_digit_5.setText("");
                    vibrator.vibrate(20);
                    break;
                case 4:
//                input_digit_1.setText("");
//                input_digit_2.setText("");
//                input_digit_3.setText("");
//                    input_digit_4.setText("");
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_5.setText("");
                    } else {
                        input_digit_5.setText(digit);
                    }
                    vibrator.vibrate(20);
                    break;
                case 5:
//                input_digit_1.setText("");
//                input_digit_2.setText("");
//                input_digit_3.setText("");
//                input_digit_4.setText("");
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_5.setText("");
                    } else {
                        input_digit_5.setText(digit);
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
                }
                else if (receivedSmsValue.length() == 3){
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
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }

}
