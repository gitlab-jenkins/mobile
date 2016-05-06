package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
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
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestIBANConfirmation;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_account);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        activity = this;

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

                ibanConfirmationRequest = new IBANConfirmationRequest();
                ibanConfirmationRequest.setIban("IR" + persianEnglishDigit.P2E(ibanNumberValue.getText().toString()));
                requestIBANConfirmation = new RequestIBANConfirmation(activity, new RequestIBANConfirmationTaskCompleteListener());
                requestIBANConfirmation.execute(ibanConfirmationRequest);

            }
        });

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
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

}
