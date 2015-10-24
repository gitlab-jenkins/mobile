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
import xyz.homapay.hampay.common.core.model.request.UnlinkUserRequest;
import xyz.homapay.hampay.common.core.model.response.UnlinkUserResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestUnlinkUser;
import xyz.homapay.hampay.mobile.android.component.material.RippleView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class UnlinkPassActivity extends AppCompatActivity implements View.OnClickListener{


    HamPayDialog hamPayDialog;

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


    RelativeLayout password_0_rl;

    LinearLayout keyboard;
    LinearLayout password_holder;

    RequestUnlinkUser requestUnlinkUser;
    UnlinkUserRequest unlinkUserRequest;

    Context context;
    Activity activity;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Tracker hamPayGaTracker;


    public void contactUs(View view){
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/user-unlink.html");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlink_user_pass);

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);


        context = this;
        activity = UnlinkPassActivity.this;

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        password_holder = (LinearLayout)findViewById(R.id.password_holder);
        password_holder.setOnClickListener(this);

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

    @Override
    protected void onStop() {
        super.onStop();


        if (requestUnlinkUser != null){
            if (!requestUnlinkUser.isCancelled())
                requestUnlinkUser.cancel(true);
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


                    if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
                        Intent intent = new Intent();
                        intent.setClass(context, HamPayLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }else {
                        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                        editor.commit();
                        unlinkUserRequest = new UnlinkUserRequest();
                        unlinkUserRequest.setPassCode(inputPasswordValue);
                        unlinkUserRequest.setMemorableWord(prefs.getString(Constants.MEMORABLE_WORD, ""));
                        requestUnlinkUser = new RequestUnlinkUser(context, new RequestUnlinkUserTaskCompleteListener());
                        requestUnlinkUser.execute(unlinkUserRequest);
                    }


                    break;
            }
        }
    }


    public class RequestUnlinkUserTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<UnlinkUserResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<UnlinkUserResponse> unlinkUserResponseResponseMessage)
        {

            hamPayDialog.dismisWaitingDialog();

            if (unlinkUserResponseResponseMessage != null) {
                if (unlinkUserResponseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    editor.clear().commit();
                    editor.commit();

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), AppSliderActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Unlink User")
                            .setAction("Unlink")
                            .setLabel("Success")
                            .build());
                }
                else {
                    requestUnlinkUser = new RequestUnlinkUser(context, new RequestUnlinkUserTaskCompleteListener());
                    new HamPayDialog(activity).showFailUnlinkDialog(requestUnlinkUser, unlinkUserRequest,
                            unlinkUserResponseResponseMessage.getService().getResultStatus().getCode(),
                            unlinkUserResponseResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Unlink User")
                            .setAction("Unlink")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestUnlinkUser = new RequestUnlinkUser(context, new RequestUnlinkUserTaskCompleteListener());
                new HamPayDialog(activity).showFailUnlinkDialog(requestUnlinkUser, unlinkUserRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_unlink_user));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Unlink User")
                        .setAction("Unlink")
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
