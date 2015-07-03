package com.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.TACRequest;
import com.hampay.common.core.model.response.TACResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.dialog.AlertUtils;
import com.hampay.mobile.android.functions.DeviceUuidFactory;
import com.hampay.mobile.android.messaging.SecurityUtils;
import com.hampay.mobile.android.service.LoginService;
import com.hampay.mobile.android.webservice.WebServices;

public class HamPayLoginActivity extends ActionBarActivity implements View.OnClickListener {

    CardView hampay_login_CardView;

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
    RelativeLayout backspace;

    String inputPassValue = "";

    ImageView input_digit_1;
    ImageView input_digit_2;
    ImageView input_digit_3;
    ImageView input_digit_4;
    ImageView input_digit_5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ham_pay_login);

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
        backspace = (RelativeLayout)findViewById(R.id.backspace);
        backspace.setOnClickListener(this);

        input_digit_1 = (ImageView)findViewById(R.id.input_digit_1);
        input_digit_2 = (ImageView)findViewById(R.id.input_digit_2);
        input_digit_3 = (ImageView)findViewById(R.id.input_digit_3);
        input_digit_4 = (ImageView)findViewById(R.id.input_digit_4);
        input_digit_5 = (ImageView)findViewById(R.id.input_digit_5);


        hampay_login_CardView = (CardView)findViewById(R.id.hampay_login_CardView);
        hampay_login_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TACRequest tacRequest = new TACRequest();
                tacRequest.setRequestUUID("");
                new HttpTACResponse().execute(tacRequest);
            }
        });
    }

    private ResponseMessage<TACResponse> tACResponse;

    public class HttpTACResponse extends AsyncTask<TACRequest, Void, String> {

        @Override
        protected String doInBackground(TACRequest... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            tACResponse = webServices.tACResponse(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (tACResponse.getService().getResultStatus() != null) {

                Intent intent = new Intent();
                intent.setClass(HamPayLoginActivity.this, MainActivity.class);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        }
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
            Intent intent = new Intent();
            intent.setClass(HamPayLoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

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


        AlertUtils.getInstance().showProgressDialog(this);


        LoginService service = new LoginService(this);
//        service.sendLoginRequest("87378fbf3a67463dac9829256f26270a", passCode);
        String memorableWord = "";

        String userId = "";

        String installationToken = "";


        String password = SecurityUtils.getInstance(this).
                generatePassword(inputPassValue,
                        memorableWord,
                        new DeviceUuidFactory(this).getDeviceUuid().toString(),
                        installationToken);

        service.sendLoginRequest(userId, password);
    }
}
