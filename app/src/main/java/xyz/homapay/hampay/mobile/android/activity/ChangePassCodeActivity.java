package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.ChangePassCodeRequest;
import xyz.homapay.hampay.common.core.model.response.ChangePassCodeResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestChangePassCode;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PasswordComplexity;

public class ChangePassCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private HamPayDialog hamPayDialog;
    private String currentPassword = "";
    private String inputPasswordValue = "";
    private String inputRePasswordValue = "";
    private FacedTextView input_digit_1;
    private FacedTextView input_digit_2;
    private FacedTextView input_digit_3;
    private FacedTextView input_digit_4;
    private FacedTextView input_digit_5;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private LinearLayout keyboard;
    private LinearLayout password_holder;
    private RequestChangePassCode requestChangePassCode;
    private ChangePassCodeRequest changePassCodeRequest;
    private FacedTextView pass_code_change_text;
    private int passCodeChangeStep = 0;
    private Context context;
    private Activity activity;

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
        setContentView(R.layout.activity_change_pass_code);

        context = this;
        activity = ChangePassCodeActivity.this;
        hamPayDialog = new HamPayDialog(activity);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        password_holder = (LinearLayout)findViewById(R.id.password_holder);
        password_holder.setOnClickListener(this);
        input_digit_1 = (FacedTextView)findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView)findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView)findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView)findViewById(R.id.input_digit_4);
        input_digit_5 = (FacedTextView)findViewById(R.id.input_digit_5);
        pass_code_change_text = (FacedTextView)findViewById(R.id.pass_code_change_text);
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

    public class RequestChangePassCodeTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<ChangePassCodeResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<ChangePassCodeResponse> changePassCodeResponseMessage)
        {

            hamPayDialog.dismisWaitingDialog();

            if (changePassCodeResponseMessage != null) {
                if (changePassCodeResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    passCodeChangeStep = 1;
                    pass_code_change_text.setText(getString(R.string.change_pass_code_text_1));
                    currentPassword = "";
                    inputPasswordValue = "";
                    inputRePasswordValue = "";
                    input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                    new HamPayDialog(activity).showSuccessChangeSettingDialog(changePassCodeResponseMessage.getService().getResultStatus().getDescription());

                }else if (changePassCodeResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
                else {
                    resetLayout();
                    requestChangePassCode = new RequestChangePassCode(context, new RequestChangePassCodeTaskCompleteListener());
                    new HamPayDialog(activity).showFailChangePassCodeDialog(requestChangePassCode, changePassCodeRequest,
                            changePassCodeResponseMessage.getService().getResultStatus().getCode(),
                            changePassCodeResponseMessage.getService().getResultStatus().getDescription());
                }
            }else {
                resetLayout();
                requestChangePassCode = new RequestChangePassCode(context, new RequestChangePassCodeTaskCompleteListener());
                new HamPayDialog(activity).showFailChangePassCodeDialog(requestChangePassCode, changePassCodeRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_change_pass_code));
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }


    private void resetLayout(){
        passCodeChangeStep = 1;
        pass_code_change_text.setText(getString(R.string.change_pass_code_text_1));
        currentPassword = "";
        inputPasswordValue = "";
        inputRePasswordValue = "";

        input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
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

        switch (passCodeChangeStep){
            case 0:
                if (digit.contains("d")){
                    if (currentPassword.length() > 0) {
                        currentPassword = currentPassword.substring(0, currentPassword.length() - 1);
                        if (currentPassword.length() == 4){
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setText("");
                        }
                        else if (currentPassword.length() == 3){
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_4.setText("");
                        }
                        else if (currentPassword.length() == 2){
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_3.setText("");
                        }
                        else if (currentPassword.length() == 1){
                            input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_2.setText("");
                        }
                        else if (currentPassword.length() == 0){
                            input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_1.setText("");
                        }
                    }
                    return;
                }
                else {
                    if (currentPassword.length() <= 5) {
                        currentPassword += digit;
                    }
                }

                if (currentPassword.length() <= 5) {
                    switch (currentPassword.length()) {
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
                            input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            passCodeChangeStep = 1;
                            pass_code_change_text.setText(getString(R.string.change_pass_code_text_2));
                            break;
                    }
                }
                break;

            case 1:
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
                            input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            int passwordComplexity = new PasswordComplexity(inputPasswordValue).check();
                            if (passwordComplexity != 1){
                                inputPasswordValue = "";
                                Toast.makeText(activity, getString(passwordComplexity), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            passCodeChangeStep = 2;
                            pass_code_change_text.setText(getString(R.string.change_pass_code_text_3));
                            break;
                    }
                }
                break;

            case 2:
                if (digit.contains("d")){
                    if (inputRePasswordValue.length() > 0) {
                        inputRePasswordValue = inputRePasswordValue.substring(0, inputRePasswordValue.length() - 1);
                        if (inputRePasswordValue.length() == 4){
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setText("");
                        }
                        else if (inputRePasswordValue.length() == 3){
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_4.setText("");
                        }
                        else if (inputRePasswordValue.length() == 2){
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_3.setText("");
                        }
                        else if (inputRePasswordValue.length() == 1){
                            input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_2.setText("");
                        }
                        else if (inputRePasswordValue.length() == 0){
                            input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_1.setText("");
                        }
                    }
                    return;
                }
                else {
                    if (inputRePasswordValue.length() <= 5) {
                        inputRePasswordValue += digit;
                    }
                }

                if (inputRePasswordValue.length() <= 5) {
                    switch (inputRePasswordValue.length()) {
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
                            if (keyboard.getVisibility() == View.VISIBLE)
                                new Collapse(keyboard).animate();

                            if (inputPasswordValue.equalsIgnoreCase(inputRePasswordValue)) {
                                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                                editor.commit();
                                changePassCodeRequest = new ChangePassCodeRequest();
                                changePassCodeRequest.setCurrentPassCode(currentPassword);
                                changePassCodeRequest.setNewPassCode(inputPasswordValue);
                                changePassCodeRequest.setMemorableCode(prefs.getString(Constants.MEMORABLE_WORD, ""));
                                requestChangePassCode = new RequestChangePassCode(context, new RequestChangePassCodeTaskCompleteListener());
                                requestChangePassCode.execute(changePassCodeRequest);
                            } else {
                                (new HamPayDialog(this)).showDisMatchPasswordDialog();
                                resetLayout();
                            }
                            break;
                    }
                }
                break;
        }
    }

}
