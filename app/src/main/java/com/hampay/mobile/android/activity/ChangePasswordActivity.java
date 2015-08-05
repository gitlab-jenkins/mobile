package com.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.ChangePassCodeRequest;
import com.hampay.common.core.model.request.RegistrationPassCodeEntryRequest;
import com.hampay.common.core.model.response.ChangePassCodeResponse;
import com.hampay.common.core.model.response.RegistrationPassCodeEntryResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.component.material.RippleView;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.webservice.WebServices;

public class ChangePasswordActivity extends ActionBarActivity implements View.OnClickListener {


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

    String currentPassword = "";
    String inputPasswordValue = "";
    String inputRePasswordValue = "";

    ImageView input_digit_1;
    ImageView input_digit_2;
    ImageView input_digit_3;
    ImageView input_digit_4;
    ImageView input_digit_5;

    SharedPreferences prefs;

    RelativeLayout password_0_rl, password_1_rl, password_2_rl;

    LinearLayout keyboard;
    LinearLayout password_holder;

    public void contactUs(View view){
        new HamPayDialog(this).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        password_holder = (LinearLayout)findViewById(R.id.password_holder);
        password_holder.setOnClickListener(this);

        password_0_rl = (RelativeLayout)findViewById(R.id.password_0_rl);
        password_1_rl = (RelativeLayout)findViewById(R.id.password_1_rl);
        password_2_rl = (RelativeLayout)findViewById(R.id.password_2_rl);


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

        if (password_0_rl.getVisibility() == View.VISIBLE) {
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

                        password_0_rl.setVisibility(View.INVISIBLE);

                        password_1_rl.setVisibility(View.VISIBLE);

                        input_digit_1.setImageResource(R.drawable.pass_icon_2);
                        input_digit_2.setImageResource(R.drawable.pass_icon_2);
                        input_digit_3.setImageResource(R.drawable.pass_icon_2);
                        input_digit_4.setImageResource(R.drawable.pass_icon_2);
                        input_digit_5.setImageResource(R.drawable.pass_icon_2);


                        break;
                }
            }
        }

        else if (password_1_rl.getVisibility() == View.VISIBLE) {


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

                        password_1_rl.setVisibility(View.INVISIBLE);

                        password_2_rl.setVisibility(View.VISIBLE);

                        input_digit_1.setImageResource(R.drawable.pass_icon_2);
                        input_digit_2.setImageResource(R.drawable.pass_icon_2);
                        input_digit_3.setImageResource(R.drawable.pass_icon_2);
                        input_digit_4.setImageResource(R.drawable.pass_icon_2);
                        input_digit_5.setImageResource(R.drawable.pass_icon_2);


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

                        if (inputPasswordValue.equalsIgnoreCase(inputRePasswordValue)) {
                            ChangePassCodeRequest changePassCodeRequest = new ChangePassCodeRequest();
                            changePassCodeRequest.setCurrentPassCode(currentPassword);
                            changePassCodeRequest.setMemorableCode("sa");
                            changePassCodeRequest.setNewPassCode(inputPasswordValue);
                            new HttpChangePassCodeResponse().execute(changePassCodeRequest);
                        } else {

                            (new HamPayDialog(this)).showDisMatchPasswordDialog();

                            password_0_rl.setVisibility(View.VISIBLE);
                            password_1_rl.setVisibility(View.INVISIBLE);
                            password_2_rl.setVisibility(View.INVISIBLE);

                            currentPassword = "";
                            inputPasswordValue = "";
                            inputRePasswordValue = "";

                            input_digit_1.setImageResource(R.drawable.pass_icon_2);
                            input_digit_2.setImageResource(R.drawable.pass_icon_2);
                            input_digit_3.setImageResource(R.drawable.pass_icon_2);
                            input_digit_4.setImageResource(R.drawable.pass_icon_2);
                            input_digit_5.setImageResource(R.drawable.pass_icon_2);
                        }

                        break;
                }
            }
        }
    }


    private ResponseMessage<ChangePassCodeResponse> changePassCodeResponseResponseMessage;

    public class HttpChangePassCodeResponse extends AsyncTask<ChangePassCodeRequest, Void, String> {

        @Override
        protected String doInBackground(ChangePassCodeRequest... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            changePassCodeResponseResponseMessage = webServices.changePassCodeResponse(params[0]);


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (changePassCodeResponseResponseMessage != null) {


                password_0_rl.setVisibility(View.VISIBLE);
                password_1_rl.setVisibility(View.INVISIBLE);
                password_2_rl.setVisibility(View.INVISIBLE);

                currentPassword = "";
                inputPasswordValue = "";
                inputRePasswordValue = "";

                input_digit_1.setImageResource(R.drawable.pass_icon_2);
                input_digit_2.setImageResource(R.drawable.pass_icon_2);
                input_digit_3.setImageResource(R.drawable.pass_icon_2);
                input_digit_4.setImageResource(R.drawable.pass_icon_2);
                input_digit_5.setImageResource(R.drawable.pass_icon_2);

                finish();

            }
        }
    }
}
