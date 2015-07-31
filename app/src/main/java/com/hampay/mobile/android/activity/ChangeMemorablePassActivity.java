package com.hampay.mobile.android.activity;

import android.content.Context;
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
import com.hampay.common.core.model.request.ChangeMemorableWordRequest;
import com.hampay.common.core.model.request.ChangePassCodeRequest;
import com.hampay.common.core.model.response.ChangeMemorableWordResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.component.material.RippleView;
import com.hampay.mobile.android.webservice.WebServices;

public class ChangeMemorablePassActivity extends ActionBarActivity implements View.OnClickListener{


    String currentMemorable = "";
    String newMemorable = "";

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

    String inputPasswordValue = "";

    ImageView input_digit_1;
    ImageView input_digit_2;
    ImageView input_digit_3;
    ImageView input_digit_4;
    ImageView input_digit_5;

    SharedPreferences prefs;

    RelativeLayout password_0_rl;

    Bundle bundle;

    LinearLayout keyboard;
    LinearLayout password_holder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_memorable_pass);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        password_holder = (LinearLayout)findViewById(R.id.password_holder);
        password_holder.setOnClickListener(this);

        bundle = getIntent().getExtras();

        currentMemorable = bundle.getString("currentMemorable");
        newMemorable = bundle.getString("newMemorable");


        password_0_rl = (RelativeLayout)findViewById(R.id.password_0_rl);

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


    private ResponseMessage<ChangeMemorableWordResponse> changeMemorableWordResponse;

    public class HttpChangeMemorableWordRequest extends AsyncTask<ChangeMemorableWordRequest, Void, String> {

        @Override
        protected String doInBackground(ChangeMemorableWordRequest... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            changeMemorableWordResponse = webServices.changeMemorableWordResponse(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (changeMemorableWordResponse != null) {

                finish();

//                Intent intent = new Intent();
//                intent.setClass(RememberPhraseActivity.this, CompleteRegistrationActivity.class);
//                startActivity(intent);

            }
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

                    input_digit_1.setImageResource(R.drawable.pass_icon_2);
                    input_digit_2.setImageResource(R.drawable.pass_icon_2);
                    input_digit_3.setImageResource(R.drawable.pass_icon_2);
                    input_digit_4.setImageResource(R.drawable.pass_icon_2);
                    input_digit_5.setImageResource(R.drawable.pass_icon_2);


                    ChangeMemorableWordRequest changeMemorableWordRequest = new ChangeMemorableWordRequest();
                    changeMemorableWordRequest.setCurrentMemorableWord(currentMemorable);
                    changeMemorableWordRequest.setNewMemorableWord(newMemorable);
                    changeMemorableWordRequest.setPassCode(inputPasswordValue);

                    new HttpChangeMemorableWordRequest().execute(changeMemorableWordRequest);


                    break;
            }
        }
    }



}
