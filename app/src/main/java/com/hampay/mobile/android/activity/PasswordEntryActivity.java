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

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationPassCodeEntryRequest;
import com.hampay.common.core.model.response.RegistrationPassCodeEntryResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestPassCodeEntry;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;

public class PasswordEntryActivity extends ActionBarActivity implements View.OnClickListener{

    Activity activity;

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

    String inputPasswordValue = "";
    String inputRePasswordValue = "";

    ImageView input_digit_1;
    ImageView input_digit_2;
    ImageView input_digit_3;
    ImageView input_digit_4;
    ImageView input_digit_5;

    SharedPreferences prefs;

    RelativeLayout password_1_rl, password_2_rl;

    LinearLayout keyboard;
    LinearLayout password_holder;

    Context context;

    RelativeLayout loading_rl;

    public void contactUs(View view){
        new HamPayDialog(this).showContactUsDialog();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_entry);

        activity = PasswordEntryActivity.this;

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        password_holder = (LinearLayout)findViewById(R.id.password_holder);
        password_holder.setOnClickListener(this);

        prefs = getPreferences(MODE_PRIVATE);

        context = this;

        password_1_rl = (RelativeLayout)findViewById(R.id.password_1_rl);
        password_2_rl = (RelativeLayout)findViewById(R.id.password_2_rl);


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

    }

    public class RequestPassCodeEntryResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationPassCodeEntryResponse>>
    {
        public RequestPassCodeEntryResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationPassCodeEntryResponse> passCodeEntryResponseMessage)
        {

            loading_rl.setVisibility(View.GONE);

            if (passCodeEntryResponseMessage != null) {
                password_1_rl.setVisibility(View.VISIBLE);
                password_2_rl.setVisibility(View.INVISIBLE);

                inputPasswordValue = "";
                inputRePasswordValue = "";

                input_digit_1.setImageResource(R.drawable.pass_icon_2);
                input_digit_2.setImageResource(R.drawable.pass_icon_2);
                input_digit_3.setImageResource(R.drawable.pass_icon_2);
                input_digit_4.setImageResource(R.drawable.pass_icon_2);
                input_digit_5.setImageResource(R.drawable.pass_icon_2);

                Intent intent = new Intent();
                intent.setClass(PasswordEntryActivity.this, MemorableWordEntryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                finish();
                startActivity(intent);
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

        if (password_1_rl.getVisibility() == View.VISIBLE) {


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
                            RegistrationPassCodeEntryRequest registrationPassCodeEntryRequest = new RegistrationPassCodeEntryRequest();
                            registrationPassCodeEntryRequest.setUserIdToken(prefs.getString("UserIdToken", ""));
                            registrationPassCodeEntryRequest.setPassCode(inputPasswordValue);

                            new RequestPassCodeEntry(context, new RequestPassCodeEntryResponseTaskCompleteListener()).execute(registrationPassCodeEntryRequest);

                        } else {

                            (new HamPayDialog(this)).showDisMatchPasswordDialog();


                            password_1_rl.setVisibility(View.VISIBLE);
                            password_2_rl.setVisibility(View.INVISIBLE);

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

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }
}
