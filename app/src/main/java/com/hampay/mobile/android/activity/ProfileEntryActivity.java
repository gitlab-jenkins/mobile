package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationEntryRequest;
import com.hampay.common.core.model.response.BankListResponse;
import com.hampay.common.core.model.response.RegistrationEntryResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.adapter.BankListAdapter;
import com.hampay.mobile.android.component.FacedEditText;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.util.DeviceInfo;
import com.hampay.mobile.android.webservice.WebServices;

public class ProfileEntryActivity extends ActionBarActivity {


    CardView keepOn_CardView;
    RelativeLayout bankSelection;
    Dialog bankSelectionDialog;

    FacedEditText cellNumberValue;
    FacedEditText nationalCodeValue;
    FacedEditText accountNumberValue;
    FacedTextView selectedBankTitle;
    String selectedBankValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_entry);


        cellNumberValue = (FacedEditText)findViewById(R.id.cellNumberValue);
        nationalCodeValue = (FacedEditText)findViewById(R.id.nationalCodeValue);
        accountNumberValue = (FacedEditText)findViewById(R.id.accountNumberValue);
        selectedBankTitle = (FacedTextView)findViewById(R.id.selectedBankText);

        bankSelection = (RelativeLayout)findViewById(R.id.bankSelection);
        bankSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Rect displayRectangle = new Rect();
                Activity parent = (Activity) ProfileEntryActivity.this;
                Window window = parent.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                View view = getLayoutInflater().inflate(R.layout.dialog_bank_select, null);

                ListView bankListView = (ListView) view.findViewById(R.id.bankListView);

                BankListAdapter bankListAdapter = new BankListAdapter(getApplicationContext(), bankListResponse.getService().getBanks());

                bankListView.setAdapter(bankListAdapter);
                bankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedBankTitle.setText(bankListResponse.getService().getBanks().get(position).getTitle());
                        selectedBankValue = bankListResponse.getService().getBanks().get(position).getCode();
                        bankSelectionDialog.dismiss();
                    }
                });


                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
                bankSelectionDialog = new Dialog(ProfileEntryActivity.this);
                bankSelectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                bankSelectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                bankSelectionDialog.setContentView(view);
                bankSelectionDialog.setTitle(null);
                bankSelectionDialog.setCanceledOnTouchOutside(true);

                bankSelectionDialog.show();
            }
        });

        new HttpBankList().execute();

        keepOn_CardView = (CardView) findViewById(R.id.keepOn_CardView);
        keepOn_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegistrationEntryRequest registrationEntryRequest = new RegistrationEntryRequest();



                registrationEntryRequest.setCellNumber(cellNumberValue.getText().toString());
                registrationEntryRequest.setAccountNumber(accountNumberValue.getText().toString());
                registrationEntryRequest.setBankCode(selectedBankValue);
                registrationEntryRequest.setNationalCode(nationalCodeValue.getText().toString());
                registrationEntryRequest.setImei(new DeviceInfo(getApplicationContext()).getIMEI());

                new HttpRegistrationEntry().execute(registrationEntryRequest);


            }
        });
    }


    private ResponseMessage<BankListResponse> bankListResponse;

    public class HttpBankList extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            bankListResponse = webServices.getBankList();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (bankListResponse != null) {
                selectedBankTitle.setText(bankListResponse.getService().getBanks().get(0).getTitle());
                selectedBankValue = bankListResponse.getService().getBanks().get(0).getTitle();
            }


        }
    }

    private ResponseMessage<RegistrationEntryResponse> registrationEntryResponse;

    public class HttpRegistrationEntry extends AsyncTask<RegistrationEntryRequest, Void, String> {

        @Override
        protected String doInBackground(RegistrationEntryRequest... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            registrationEntryResponse = webServices.registrationEntry(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (registrationEntryResponse.getService().getUserIdToken() != null) {

                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putString("UserIdToken", registrationEntryResponse.getService().getUserIdToken());
                editor.commit();

                Intent intent = new Intent();
                intent.setClass(ProfileEntryActivity.this, VerificationActivity.class);
                startActivity(intent);

            }
            else {

            }
        }

    }
}

