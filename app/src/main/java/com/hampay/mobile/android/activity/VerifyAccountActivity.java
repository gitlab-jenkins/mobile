package com.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.ChangeMemorableWordRequest;
import com.hampay.common.core.model.request.VerifyAccountRequest;
import com.hampay.common.core.model.request.VerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.ChangeMemorableWordResponse;
import com.hampay.common.core.model.response.TACResponse;
import com.hampay.common.core.model.response.VerifyAccountResponse;
import com.hampay.common.core.model.response.VerifyTransferMoneyResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestTAC;
import com.hampay.mobile.android.async.RequestVerifyTransferMoney;
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
    Context context;

    public void contactUs(View view){
        (new HamPayDialog(this)).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        context = this;

        bundle = getIntent().getExtras();
        TransferMoneyComment = bundle.getString(Constants.TRANSFER_MONEY_COMMENT, "");
        verification_response_text = (FacedTextView)findViewById(R.id.verification_response_text);
        verification_response_text.setText(TransferMoneyComment);

        verify_account_button = (ButtonRectangle)findViewById(R.id.verify_account_button);
        verify_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VerifyTransferMoneyRequest verifyTransferMoneyRequest = new VerifyTransferMoneyRequest();

                new RequestVerifyTransferMoney(context, new RequestVerifyTransferMoneyTaskCompleteListener()).execute(verifyTransferMoneyRequest);
            }
        });
    }


    public class RequestVerifyTransferMoneyTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<VerifyTransferMoneyResponse>>
    {
        public RequestVerifyTransferMoneyTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<VerifyTransferMoneyResponse> verifyTransferMoneyResponseMessage)
        {
            if (verifyTransferMoneyResponseMessage != null) {
                if (verifyTransferMoneyResponseMessage.getService().getIsVerified()){
                    Intent intent = new Intent();
                    intent.setClass(VerifyAccountActivity.this, CongratsAccountActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            else {
                Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskPreRun() { }
    }
}
