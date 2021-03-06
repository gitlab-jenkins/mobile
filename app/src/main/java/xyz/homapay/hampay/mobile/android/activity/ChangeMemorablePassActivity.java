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
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class ChangeMemorablePassActivity extends AppCompatActivity implements View.OnClickListener {

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
    private String currentMemorable = "";
    private String newMemorable = "";
    private String inputPasswordValue = "";
    private Bundle bundle;
    private RequestChangeMemorableWord requestChangeMemorableWord;
    private ChangeMemorableWordRequest changeMemorableWordRequest;
    private Context context;
    private Activity activity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

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
        setContentView(R.layout.activity_change_memorable_pass);
        ButterKnife.bind(this);

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        context = this;
        activity = ChangeMemorablePassActivity.this;
        hamPayDialog = new HamPayDialog(activity);
        bundle = getIntent().getExtras();
        currentMemorable = bundle.getString("currentMemorable");
        newMemorable = bundle.getString("newMemorable");
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
                    if (prefs.getString(Constants.MEMORABLE_WORD, "").compareTo(currentMemorable) == 0) {
                        changeMemorableWordRequest = new ChangeMemorableWordRequest();
                        changeMemorableWordRequest.setPassCode(inputPasswordValue);
                        changeMemorableWordRequest.setCurrentMemorableWord(currentMemorable);
                        changeMemorableWordRequest.setNewMemorableWord(newMemorable);
                        requestChangeMemorableWord = new RequestChangeMemorableWord(context, new RequestChangeMemorableWordTaskCompleteListener());
                        requestChangeMemorableWord.execute(changeMemorableWordRequest);
                    } else {
                        new HamPayDialog(activity).showDisMatchMemorableDialog();
                    }
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

    public class RequestChangeMemorableWordTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<ChangeMemorableWordResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<ChangeMemorableWordResponse> changeMemorableWordResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

            if (changeMemorableWordResponseMessage != null) {
                if (changeMemorableWordResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.CHANGE_MEMORABLE_WORD_SUCCESS;
                    editor.putString(Constants.MEMORABLE_WORD, newMemorable);
                    editor.commit();
                    new HamPayDialog(activity).showSuccessChangeSettingDialog(changeMemorableWordResponseMessage.getService().getResultStatus().getDescription(), false);
                } else if (changeMemorableWordResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.CHANGE_MEMORABLE_WORD_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.CHANGE_MEMORABLE_WORD_FAILURE;
                    requestChangeMemorableWord = new RequestChangeMemorableWord(context, new RequestChangeMemorableWordTaskCompleteListener());
                    new HamPayDialog(activity).showFailChangeMemorableWordDialog(
                            changeMemorableWordResponseMessage.getService().getResultStatus().getCode(),
                            changeMemorableWordResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                serviceName = ServiceEvent.CHANGE_MEMORABLE_WORD_FAILURE;
                requestChangeMemorableWord = new RequestChangeMemorableWord(context, new RequestChangeMemorableWordTaskCompleteListener());
                new HamPayDialog(activity).showFailChangeMemorableWordDialog(
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_change_memorable_word));
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
