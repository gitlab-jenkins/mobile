package com.hampay.mobile.android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.ChangeMemorableWordRequest;
import com.hampay.common.core.model.request.VerifyAccountRequest;
import com.hampay.common.core.model.request.VerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.ChangeMemorableWordResponse;
import com.hampay.common.core.model.response.VerifyAccountResponse;
import com.hampay.common.core.model.response.VerifyTransferMoneyResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.webservice.WebServices;

public class VerifyAccountActivity extends ActionBarActivity {


    FacedTextView verification_response_text;
    ButtonRectangle verify_account_button;
    Bundle bundle;
    String TransferMoneyComment = "";

    public void contactUs(View view){
        (new HamPayDialog(this)).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        bundle = getIntent().getExtras();
        TransferMoneyComment = bundle.getString(Constants.TRANSFER_MONEY_COMMENT, "");
        verification_response_text = (FacedTextView)findViewById(R.id.verification_response_text);
        verification_response_text.setText(TransferMoneyComment);


        verify_account_button = (ButtonRectangle)findViewById(R.id.verify_account_button);
        verify_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyTransferMoneyRequest verifyTransferMoneyRequest = new VerifyTransferMoneyRequest();

                new HttpVerifyTransferMoneyResponse().execute(verifyTransferMoneyRequest);
            }
        });


    }


    private ResponseMessage<VerifyTransferMoneyResponse> verifyTransferMoneyResponse;

    public class HttpVerifyTransferMoneyResponse extends AsyncTask<VerifyTransferMoneyRequest, Void, String> {

        @Override
        protected String doInBackground(VerifyTransferMoneyRequest... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            verifyTransferMoneyResponse = webServices.verifyTransferMoneyResponse(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (verifyTransferMoneyResponse.getService().getResultStatus() != null) {
                if (verifyTransferMoneyResponse.getService().getIsVerified()){
                    Intent intent = new Intent();
                    intent.setClass(VerifyAccountActivity.this, CongratsAccountActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

}
