package com.hampay.mobile.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationVerifyAccountRequest;
import com.hampay.common.core.model.request.RegistrationVerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.RegistrationVerifyAccountResponse;
import com.hampay.common.core.model.response.RegistrationVerifyTransferMoneyResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestRegisterVerifyAccount;
import com.hampay.mobile.android.async.RequestRegistrationVerifyTransferMoney;
import com.hampay.mobile.android.webservice.WebServices;

public class PrePasswordActivity extends ActionBarActivity {

    CardView keepOn_CardView;

    RelativeLayout loading_rl;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_password);

        loading_rl = (RelativeLayout) findViewById(R.id.loading_rl);

        prefs = getPreferences(MODE_PRIVATE);


        keepOn_CardView = (CardView) findViewById(R.id.keepOn_CardView);
        keepOn_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PrePasswordActivity.this, PasswordEntryActivity.class);
                startActivity(intent);
            }
        });




    }

}

