package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.ChangeEmailRequest;
import xyz.homapay.hampay.common.core.model.response.ChangeEmailResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestChangeEmail;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class ChangeEmailPassActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.input_digit_1)
    FacedTextView input_digit_1;
    @BindView(R.id.input_digit_2)
    FacedTextView input_digit_2;
    @BindView(R.id.input_digit_3)
    FacedTextView input_digit_3;
    @BindView(R.id.input_digit_4)
    FacedTextView input_digit_4;
    @BindView(R.id.input_digit_5)
    FacedTextView input_digit_5;
    @BindView(R.id.keyboard)
    LinearLayout keyboard;
    private HamPayDialog hamPayDialog;
    private String inputPasswordValue = "";
    private Context context;
    private Activity activity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String userEmail = "";

    public void backActionBar(View view) {
        finish();
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
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email_pass);
        ButterKnife.bind(this);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        context = this;
        activity = ChangeEmailPassActivity.this;
        hamPayDialog = new HamPayDialog(activity);
        userEmail = getIntent().getExtras().getString(Constants.REGISTERED_USER_EMAIL);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    @Override
    public void onBackPressed() {

        if (keyboard.getVisibility() == View.VISIBLE) {
            new Collapse(keyboard).animate();
        } else {
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

    public void pressKey(View view) {
        if (view.getTag().toString().equals("*")) {
            new Collapse(keyboard).animate();
        } else {
            inputDigit(view.getTag().toString());
        }
    }

    private void inputDigit(String digit) {

        if (digit.contains("d")) {
            if (inputPasswordValue.length() > 0) {
                inputPasswordValue = inputPasswordValue.substring(0, inputPasswordValue.length() - 1);
                if (inputPasswordValue.length() == 4) {
                    input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_5.setText("");
                } else if (inputPasswordValue.length() == 3) {
                    input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_4.setText("");
                } else if (inputPasswordValue.length() == 2) {
                    input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_3.setText("");
                } else if (inputPasswordValue.length() == 1) {
                    input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_2.setText("");
                } else if (inputPasswordValue.length() == 0) {
                    input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
                    input_digit_1.setText("");
                }
            }
            return;
        } else {
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
                    AppManager.setMobileTimeout(context);
                    editor.commit();
                    ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest();
                    changeEmailRequest.setEmail(userEmail);
                    changeEmailRequest.setPassCode(inputPasswordValue);
                    changeEmailRequest.setMemorableWord(prefs.getString(Constants.MEMORABLE_WORD, ""));
                    RequestChangeEmail requestChangeEmail = new RequestChangeEmail(activity, new RequestChangeEmailTaskCompleteListener(changeEmailRequest));
                    requestChangeEmail.execute(changeEmailRequest);
                    inputPasswordValue = "";
                    break;
            }
        }
    }

    private void resetLayout() {
        inputPasswordValue = "";
        input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
    }

    public class RequestChangeEmailTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<ChangeEmailResponse>> {

        private ChangeEmailRequest changeEmailRequest;

        public RequestChangeEmailTaskCompleteListener(ChangeEmailRequest changeEmailRequest) {
            this.changeEmailRequest = changeEmailRequest;

        }

        @Override
        public void onTaskComplete(ResponseMessage<ChangeEmailResponse> changeEmailResponseResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

            if (changeEmailResponseResponseMessage != null) {
                if (changeEmailResponseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.CHANGE_EMAIL_SUCCESS;
                    editor.putString(Constants.REGISTERED_USER_EMAIL, userEmail);
                    editor.commit();
                    new HamPayDialog(activity).showSuccessChangeSettingDialog(changeEmailResponseResponseMessage.getService().getResultStatus().getDescription(), false);
                } else if (changeEmailResponseResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.CHANGE_EMAIL_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.CHANGE_EMAIL_FAILURE;
                    new HamPayDialog(activity).showFailChangeEmail(
                            changeEmailResponseResponseMessage.getService().getResultStatus().getCode(),
                            changeEmailResponseResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                serviceName = ServiceEvent.CHANGE_EMAIL_FAILURE;
                new HamPayDialog(activity).showFailChangeEmail(
                        Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.msg_gail_change_email));
            }
            logEvent.log(serviceName);
            resetLayout();
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

}
