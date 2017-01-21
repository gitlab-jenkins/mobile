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
import xyz.homapay.hampay.common.core.model.request.IBANChangeRequest;
import xyz.homapay.hampay.common.core.model.response.IBANChangeResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestIBANChange;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class ChangeIbanPassActivity extends AppCompatActivity implements View.OnClickListener {

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
    private String inputPasswordValue = "";
    private Context context;
    private Activity activity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private IBANChangeRequest ibanChangeRequest;
    private RequestIBANChange requestIBANChange;
    private String iban = "";
    private HamPayDialog hamPayDialog;

    public void backActionBar(View view) {
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

        if (requestIBANChange != null) {
            if (!requestIBANChange.isCancelled())
                requestIBANChange.cancel(true);
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
        setContentView(R.layout.activity_change_iban_pass);
        ButterKnife.bind(this);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        iban = getIntent().getExtras().getString(Constants.IBAN_NUMBER);
        context = this;
        activity = ChangeIbanPassActivity.this;
        hamPayDialog = new HamPayDialog(activity);
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
                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();
                    ibanChangeRequest = new IBANChangeRequest();
                    ibanChangeRequest.setIban(new PersianEnglishDigit().P2E(iban));
                    requestIBANChange = new RequestIBANChange(activity, new RequestIBANChangeTaskCompleteListener(ibanChangeRequest));
                    requestIBANChange.execute(ibanChangeRequest);
                    inputPasswordValue = "";
                    break;
            }
        }
    }

    private void resetLogin() {
        inputPasswordValue = "";
        input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
        input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
    }

    public class RequestIBANChangeTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<IBANChangeResponse>> {

        IBANChangeRequest ibanChangeRequest;
        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);

        public RequestIBANChangeTaskCompleteListener(IBANChangeRequest ibanChangeRequest) {
            this.ibanChangeRequest = ibanChangeRequest;
        }

        @Override
        public void onTaskComplete(ResponseMessage<IBANChangeResponse> ibanChangeResponseMessage) {
            hamPayDialog.dismisWaitingDialog();
            if (ibanChangeResponseMessage != null) {
                if (ibanChangeResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.IBAN_CHANGE_SUCCESS;
                    editor.putBoolean(Constants.SETTING_CHANGE_IBAN_STATUS, true);
                    editor.commit();
                    hamPayDialog.showSuccessChangeSettingDialog(ibanChangeResponseMessage.getService().getResultStatus().getDescription(), false);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.RETURN_IBAN_CONFIRMED, iban);
                    setResult(RESULT_OK, returnIntent);
                    activity.finish();
                } else if (ibanChangeResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.IBAN_CHANGE_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.IBAN_CHANGE_FAILURE;
                    requestIBANChange = new RequestIBANChange(activity, new RequestIBANChangeTaskCompleteListener(ibanChangeRequest));
                    hamPayDialog.showFailIBANChangeDialog(
                            ibanChangeResponseMessage.getService().getResultStatus().getCode(),
                            ibanChangeResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                serviceName = ServiceEvent.IBAN_CHANGE_FAILURE;
                requestIBANChange = new RequestIBANChange(activity, new RequestIBANChangeTaskCompleteListener(ibanChangeRequest));
                hamPayDialog.showFailIBANChangeDialog(
                        Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.msg_fail_iban_change));
            }
            logEvent.log(serviceName);
            resetLogin();
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

}
