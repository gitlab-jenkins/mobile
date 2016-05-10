package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.ChangeMemorableWordRequest;
import xyz.homapay.hampay.common.core.model.response.ChangeMemorableWordResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestChangeMemorableWord;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class ChangeMemorablePassActivity extends AppCompatActivity implements View.OnClickListener{


    HamPayDialog hamPayDialog;

    String currentMemorable = "";
    String newMemorable = "";

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
    FacedTextView keyboard_dismiss;
    RelativeLayout backspace;

    String inputPasswordValue = "";

    ImageView input_digit_1;
    ImageView input_digit_2;
    ImageView input_digit_3;
    ImageView input_digit_4;
    ImageView input_digit_5;


    RelativeLayout password_0_rl;

    Bundle bundle;

    LinearLayout keyboard;
    LinearLayout password_holder;

    RequestChangeMemorableWord requestChangeMemorableWord;
    ChangeMemorableWordRequest changeMemorableWordRequest;

    Context context;
    Activity activity;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Tracker hamPayGaTracker;

    public void contactUs(View view){
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/changeMemorableWord.html");
    }

    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HamPayApplication.setAppSate(AppState.Resumed);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_memorable_pass);

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);


        context = this;
        activity = ChangeMemorablePassActivity.this;

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        password_holder = (LinearLayout)findViewById(R.id.password_holder);
        password_holder.setOnClickListener(this);

        bundle = getIntent().getExtras();

        currentMemorable = bundle.getString("currentMemorable");
        newMemorable = bundle.getString("newMemorable");


        password_0_rl = (RelativeLayout)findViewById(R.id.password_0_rl);

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
        backspace = (RelativeLayout)findViewById(R.id.backspace);
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
                    input_digit_1.setImageResource(R.drawable.pass_value_empty);
                    input_digit_2.setImageResource(R.drawable.pass_value_empty);
                    input_digit_3.setImageResource(R.drawable.pass_value_empty);
                    input_digit_4.setImageResource(R.drawable.pass_value_empty);
                    input_digit_5.setImageResource(R.drawable.pass_value_empty);
                    vibrator.vibrate(20);
                    break;

                case 1:
                    input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_2.setImageResource(R.drawable.pass_value_empty);
                    input_digit_3.setImageResource(R.drawable.pass_value_empty);
                    input_digit_4.setImageResource(R.drawable.pass_value_empty);
                    input_digit_5.setImageResource(R.drawable.pass_value_empty);
                    vibrator.vibrate(20);

                    break;
                case 2:
                    input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_3.setImageResource(R.drawable.pass_value_empty);
                    input_digit_4.setImageResource(R.drawable.pass_value_empty);
                    input_digit_5.setImageResource(R.drawable.pass_value_empty);
                    vibrator.vibrate(20);
                    break;
                case 3:
                    input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_3.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_4.setImageResource(R.drawable.pass_value_empty);
                    input_digit_5.setImageResource(R.drawable.pass_value_empty);
                    vibrator.vibrate(20);
                    break;
                case 4:
                    input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_3.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_4.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_5.setImageResource(R.drawable.pass_value_empty);
                    vibrator.vibrate(20);
                    break;
                case 5:
                    input_digit_1.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_2.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_3.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_4.setImageResource(R.drawable.pass_value_placeholder);
                    input_digit_5.setImageResource(R.drawable.pass_value_placeholder);
                    vibrator.vibrate(20);

                    input_digit_1.setImageResource(R.drawable.pass_value_empty);
                    input_digit_2.setImageResource(R.drawable.pass_value_empty);
                    input_digit_3.setImageResource(R.drawable.pass_value_empty);
                    input_digit_4.setImageResource(R.drawable.pass_value_empty);
                    input_digit_5.setImageResource(R.drawable.pass_value_empty);


                    if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
                        Intent intent = new Intent();
                        intent.setClass(context, HamPayLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }else {
                        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                        editor.commit();

                        if (prefs.getString(Constants.MEMORABLE_WORD, "").compareTo(currentMemorable) == 0){
                            changeMemorableWordRequest = new ChangeMemorableWordRequest();
                            changeMemorableWordRequest.setPassCode(inputPasswordValue);
                            changeMemorableWordRequest.setCurrentMemorableWord(currentMemorable);
                            changeMemorableWordRequest.setNewMemorableWord(newMemorable);
                            requestChangeMemorableWord = new RequestChangeMemorableWord(context, new RequestChangeMemorableWordTaskCompleteListener());
                            requestChangeMemorableWord.execute(changeMemorableWordRequest);
                        }else {
                            new HamPayDialog(activity).showDisMatchMemorableDialog();
                        }
                    }


                    break;
            }
        }
    }


    public class RequestChangeMemorableWordTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<ChangeMemorableWordResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<ChangeMemorableWordResponse> changeMemorableWordResponseMessage)
        {

            hamPayDialog.dismisWaitingDialog();

            if (changeMemorableWordResponseMessage != null) {
                if (changeMemorableWordResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    editor.putString(Constants.MEMORABLE_WORD, newMemorable);
                    editor.commit();
                    new HamPayDialog(activity).showSuccessChangeSettingDialog(changeMemorableWordResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Memorable Word")
                            .setAction("Change")
                            .setLabel("Success")
                            .build());
                }
                else {
                    requestChangeMemorableWord = new RequestChangeMemorableWord(context, new RequestChangeMemorableWordTaskCompleteListener());
                    new HamPayDialog(activity).showFailChangeMemorableWordDialog(requestChangeMemorableWord, changeMemorableWordRequest,
                            changeMemorableWordResponseMessage.getService().getResultStatus().getCode(),
                            changeMemorableWordResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Memorable Word")
                            .setAction("Change")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestChangeMemorableWord = new RequestChangeMemorableWord(context, new RequestChangeMemorableWordTaskCompleteListener());
                new HamPayDialog(activity).showFailChangeMemorableWordDialog(requestChangeMemorableWord, changeMemorableWordRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_change_memorable_word));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Memorable Word")
                        .setAction("Change")
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
    public void onBackPressed() {

        if (keyboard.getVisibility() == View.VISIBLE){
            new Collapse(keyboard).animate();
        }
        else {
            finish();
        }
    }

}
