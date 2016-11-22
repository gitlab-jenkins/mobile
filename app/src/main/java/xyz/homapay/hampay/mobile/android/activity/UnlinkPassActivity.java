package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.UnlinkUserRequest;
import xyz.homapay.hampay.common.core.model.response.UnlinkUserResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestUnlinkUser;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.app.AppEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class UnlinkPassActivity extends AppCompatActivity implements View.OnClickListener{


    private HamPayDialog hamPayDialog;
    private String inputPasswordValue = "";
    private FacedTextView input_digit_1;
    private FacedTextView input_digit_2;
    private FacedTextView input_digit_3;
    private FacedTextView input_digit_4;
    private FacedTextView input_digit_5;
    private LinearLayout keyboard;
    private LinearLayout password_holder;
    private RequestUnlinkUser requestUnlinkUser;
    private UnlinkUserRequest unlinkUserRequest;
    private Context context;
    private Activity activity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private DatabaseHelper databaseHelper;
    private AppEvent appEvent = AppEvent.UNLINK;

    public void backActionBar(View view){
        finish();
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

        if (requestUnlinkUser != null){
            if (!requestUnlinkUser.isCancelled())
                requestUnlinkUser.cancel(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HamPayApplication.setAppSate(AppState.Resumed);
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlink_user_pass);

        databaseHelper = new DatabaseHelper(this);

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);


        context = this;
        activity = UnlinkPassActivity.this;

        hamPayDialog = new HamPayDialog(activity);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        password_holder = (LinearLayout)findViewById(R.id.password_holder);
        password_holder.setOnClickListener(this);

        input_digit_1 = (FacedTextView)findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView)findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView)findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView)findViewById(R.id.input_digit_4);
        input_digit_5 = (FacedTextView)findViewById(R.id.input_digit_5);
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
        }
    }

    public class RequestUnlinkUserTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<UnlinkUserResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<UnlinkUserResponse> unlinkUserResponseResponseMessage)
        {

            hamPayDialog.dismisWaitingDialog();
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

            if (unlinkUserResponseResponseMessage != null) {
                if (unlinkUserResponseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.UNLINK_USER_SUCCESS;
                    logEvent.log(appEvent);
                    editor.clear().commit();
                    editor.commit();

                    databaseHelper.deleteAllDataBase();

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else if (unlinkUserResponseResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.UNLINK_USER_FAILURE;
                    forceLogout();
                }
                else {
                    serviceName = ServiceEvent.UNLINK_USER_FAILURE;
                    requestUnlinkUser = new RequestUnlinkUser(context, new RequestUnlinkUserTaskCompleteListener());
                    new HamPayDialog(activity).showFailUnlinkDialog(
                            unlinkUserResponseResponseMessage.getService().getResultStatus().getCode(),
                            unlinkUserResponseResponseMessage.getService().getResultStatus().getDescription());
                }
            }else {
                serviceName = ServiceEvent.UNLINK_USER_FAILURE;
                requestUnlinkUser = new RequestUnlinkUser(context, new RequestUnlinkUserTaskCompleteListener());
                new HamPayDialog(activity).showFailUnlinkDialog(
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_unlink_user));
            }
            logEvent.log(serviceName);
            resetLayout();
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
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

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(context, HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (activity != null) {
            finish();
            startActivity(intent);
        }
    }

    public void pressKey(View view){
        if (view.getTag().toString().equals("*")){
            new Collapse(keyboard).animate();
        }
        else {
            inputDigit(view.getTag().toString());
        }
    }

    private void inputDigit(String digit){

        if (digit.contains("d")){
            if (inputPasswordValue.length() > 0) {
                inputPasswordValue = inputPasswordValue.substring(0, inputPasswordValue.length() - 1);
                if (inputPasswordValue.length() == 4){
                    input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_5.setText("");
                }
                else if (inputPasswordValue.length() == 3){
                    input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_4.setText("");
                }
                else if (inputPasswordValue.length() == 2){
                    input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_3.setText("");
                }
                else if (inputPasswordValue.length() == 1){
                    input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_2.setText("");
                }
                else if (inputPasswordValue.length() == 0){
                    input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_1.setText("");
                }
            }
            return;
        }
        else {
            if (inputPasswordValue.length() <= 5) {
                inputPasswordValue += digit;
            }
        }

        if (inputPasswordValue.length() <= 5) {
            switch (inputPasswordValue.length()) {
                case 1:
                    input_digit_1.setBackgroundResource(R.drawable.pass_value_placeholder);
                    input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                    break;

                case 2:
                    input_digit_2.setBackgroundResource(R.drawable.pass_value_placeholder);
                    input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                    break;
                case 3:
                    input_digit_3.setBackgroundResource(R.drawable.pass_value_placeholder);
                    input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                    break;
                case 4:
                    input_digit_4.setBackgroundResource(R.drawable.pass_value_placeholder);
                    input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                    break;
                case 5:
                    input_digit_5.setBackgroundResource(R.drawable.pass_value_placeholder);
                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();
                    unlinkUserRequest = new UnlinkUserRequest();
                    unlinkUserRequest.setPassCode(inputPasswordValue);
                    unlinkUserRequest.setMemorableWord(prefs.getString(Constants.MEMORABLE_WORD, ""));
                    requestUnlinkUser = new RequestUnlinkUser(context, new RequestUnlinkUserTaskCompleteListener());
                    requestUnlinkUser.execute(unlinkUserRequest);
                    break;
            }
        }
    }

    private void resetLayout(){
        inputPasswordValue = "";

        input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
    }

}
