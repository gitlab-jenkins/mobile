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
import com.hampay.mobile.android.webservice.WebServices;

public class VerifyAccountActivity extends ActionBarActivity {


    VerifyAccountRequest verifyAccountRequest;
    FacedTextView verification_response_text;
    CardView verify_account_CardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        verify_account_CardView = (CardView)findViewById(R.id.verify_account_CardView);
        verify_account_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyTransferMoneyRequest verifyTransferMoneyRequest = new VerifyTransferMoneyRequest();

                new HttpVerifyTransferMoneyResponse().execute(verifyTransferMoneyRequest);
            }
        });

        verification_response_text = (FacedTextView)findViewById(R.id.verification_response_text);

        verifyAccountRequest = new VerifyAccountRequest();

        new HttpVerifyAccountResponse().execute(verifyAccountRequest);


    }

    private ResponseMessage<VerifyAccountResponse> verifyAccountResponse;

    public class HttpVerifyAccountResponse extends AsyncTask<VerifyAccountRequest, Void, String> {

        @Override
        protected String doInBackground(VerifyAccountRequest... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            verifyAccountResponse = webServices.verifyAccountResponse(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (verifyAccountResponse.getService().getResultStatus() != null) {

                verification_response_text.setText(
                        verifyAccountResponse.getService().getTransferMoneyComment());
            }
        }
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
