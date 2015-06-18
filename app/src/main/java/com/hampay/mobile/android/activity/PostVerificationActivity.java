package com.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;

public class PostVerificationActivity extends ActionBarActivity implements View.OnClickListener{

    CardView verification_CardView;

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
    FacedTextView resend_active_code;
    FacedTextView backspace;

    String inputStringValue = "";

    ImageView input_digit_1;
    ImageView input_digit_2;
    ImageView input_digit_3;
    ImageView input_digit_4;
    ImageView input_digit_5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_verification);

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
        resend_active_code = (FacedTextView)findViewById(R.id.resend_active_code);
        resend_active_code.setOnClickListener(this);
        backspace = (FacedTextView)findViewById(R.id.backspace);
        backspace.setOnClickListener(this);

        input_digit_1 = (ImageView)findViewById(R.id.input_digit_1);
        input_digit_2 = (ImageView)findViewById(R.id.input_digit_2);
        input_digit_3 = (ImageView)findViewById(R.id.input_digit_3);
        input_digit_4 = (ImageView)findViewById(R.id.input_digit_4);
        input_digit_5 = (ImageView)findViewById(R.id.input_digit_5);


        verification_CardView = (CardView)findViewById(R.id.verification_CardView);
        verification_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PostVerificationActivity.this, ConfirmAccountNoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
            if (inputStringValue.length() > 0) {
                inputStringValue = inputStringValue.substring(0, inputStringValue.length() - 1);
            }
        }
        else {
            if (inputStringValue.length() <= 5) {
                inputStringValue += digit;
            }
        }


        switch (inputStringValue.length()){

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
}
