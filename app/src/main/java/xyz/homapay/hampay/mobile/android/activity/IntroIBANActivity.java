package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.IBANConfirmationRequest;
import xyz.homapay.hampay.common.core.model.response.IBANConfirmationResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestIBANConfirmation;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class IntroIBANActivity extends AppCompatActivity {

    HamPayDialog hamPayDialog;
    FacedTextView sheba_verify_button;
    FacedEditText ibanNumberValue;
    PersianEnglishDigit persianEnglishDigit;
    Activity activity;

    RequestIBANConfirmation requestIBANConfirmation;
    IBANConfirmationRequest ibanConfirmationRequest;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private Context context;

    public void backActionBar(View view){
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_account);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        activity = this;
        context = this;

        hamPayDialog = new HamPayDialog(activity);

        persianEnglishDigit = new PersianEnglishDigit();

        ibanNumberValue = (FacedEditText)findViewById(R.id.ibanNumberValue);
        ibanNumberValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ibanNumberValue.removeTextChangedListener(this);
                ibanNumberValue.setText(persianEnglishDigit.E2P(ibanNumberValue.getText().toString()));
                ibanNumberValue.setSelection(ibanNumberValue.getText().toString().length());
                ibanNumberValue.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sheba_verify_button = (FacedTextView)findViewById(R.id.sheba_verify_button);
        sheba_verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ChangeIbanPassActivity.class);
                intent.putExtra(Constants.USER_IBAN, ibanNumberValue.getText().toString());
                activity.startActivityForResult(intent, Constants.IBAN_CHANGE_RESULT_CODE);
//                finish();
//                ibanConfirmationRequest = new IBANConfirmationRequest();
//                ibanConfirmationRequest.setIban("IR" + persianEnglishDigit.P2E(ibanNumberValue.getText().toString()));
//                requestIBANConfirmation = new RequestIBANConfirmation(activity, new RequestIBANConfirmationTaskCompleteListener());
//                requestIBANConfirmation.execute(ibanConfirmationRequest);
            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IBAN_CHANGE_RESULT_CODE) {
            if(resultCode == RESULT_OK){
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.RETURN_IBAN_CONFIRMED, data.getStringExtra(Constants.RETURN_IBAN_CONFIRMED));
                setResult(RESULT_OK, returnIntent);
                activity.finish();
            }
        }
    }

    public class RequestIBANConfirmationTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<IBANConfirmationResponse>> {
        public RequestIBANConfirmationTaskCompleteListener() {
        }


        @Override
        public void onTaskComplete(ResponseMessage<IBANConfirmationResponse> ibanConfirmationResponseMessage) {
            hamPayDialog.dismisWaitingDialog();
            if (ibanConfirmationResponseMessage != null) {

                if (ibanConfirmationResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    hamPayDialog.showIBANConfirmationDialog(ibanNumberValue.getText().toString(), ibanConfirmationResponseMessage.getService());
                } else {
                    hamPayDialog.dismisWaitingDialog();
                    requestIBANConfirmation = new RequestIBANConfirmation(activity, new RequestIBANConfirmationTaskCompleteListener());

                    new HamPayDialog(activity).showFailIBANConfirmationDialog(requestIBANConfirmation, ibanConfirmationRequest,
                            ibanConfirmationResponseMessage.getService().getResultStatus().getCode(),
                            ibanConfirmationResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                hamPayDialog.dismisWaitingDialog();
                requestIBANConfirmation = new RequestIBANConfirmation(activity, new RequestIBANConfirmationTaskCompleteListener());
                new HamPayDialog(activity).showFailIBANConfirmationDialog(requestIBANConfirmation, ibanConfirmationRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_iban_confirm));


            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

}
