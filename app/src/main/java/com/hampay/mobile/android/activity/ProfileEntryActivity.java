package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.BankListRequest;
import com.hampay.common.core.model.request.RegistrationEntryRequest;
import com.hampay.common.core.model.response.BankListResponse;
import com.hampay.common.core.model.response.RegistrationEntryResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.adapter.BankListAdapter;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestBankList;
import com.hampay.mobile.android.async.RequestRegistrationEntry;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.util.DeviceInfo;
import com.hampay.mobile.android.util.NetworkConnectivity;

public class ProfileEntryActivity extends ActionBarActivity {


    ButtonRectangle keepOn_button;
    RelativeLayout bankSelection;
    Dialog bankSelectionDialog;

    FacedEditText cellNumberValue;
    ImageView cellNumberIcon;
    boolean cellNumberIsValid = false;

    FacedEditText nationalCodeValue;
    ImageView nationalCodeIcon;
    boolean nationalCodeIsValid;

    FacedEditText accountNumberValue;
    boolean accountNumberIsValid = false;

    ImageView accountNumberIcon;
    FacedTextView selectedBankTitle;
    String selectedBankValue;
    RelativeLayout loading_rl;

    Context context;

    NetworkConnectivity networkConnectivity;

    private ResponseMessage<BankListResponse> bankListResponse;
    private ResponseMessage<RegistrationEntryResponse> registrationEntryResponseMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_entry);

        context = this;

        networkConnectivity = new NetworkConnectivity(this);

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);


        cellNumberValue = (FacedEditText)findViewById(R.id.cellNumberValue);
        cellNumberIcon = (ImageView)findViewById(R.id.cellNumberIcon);
        cellNumberValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 11 && cellNumberValue.getText().toString().startsWith("09")){
                    cellNumberIcon.setImageResource(R.drawable.right_icon);
                    cellNumberIsValid = true;
                }
                else {
                    cellNumberIcon.setImageResource(R.drawable.false_icon);
                    cellNumberIsValid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        nationalCodeValue = (FacedEditText)findViewById(R.id.nationalCodeValue);
        nationalCodeIcon = (ImageView)findViewById(R.id.nationalCodeIcon);
        nationalCodeValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10){
                    nationalCodeIsValid = true;
                    nationalCodeIcon.setImageResource(R.drawable.right_icon);
                }else {
                    nationalCodeIsValid = false;
                    nationalCodeIcon.setImageResource(R.drawable.false_icon);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        accountNumberValue = (FacedEditText)findViewById(R.id.accountNumberValue);
        accountNumberIcon = (ImageView)findViewById(R.id.accountNumberIcon);
        accountNumberValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 1){
                    accountNumberIsValid = true;
                    accountNumberIcon.setImageResource(R.drawable.right_icon);
                }else {
                    accountNumberIsValid = false;
                    accountNumberIcon.setImageResource(R.drawable.false_icon);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        accountNumberValue.addTextChangedListener(new AccountNoFormat(accountNumberValue));

        selectedBankTitle = (FacedTextView)findViewById(R.id.selectedBankText);

        bankSelection = (RelativeLayout)findViewById(R.id.bankSelection);
        bankSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bankListResponse != null && bankListResponse.getService().getBanks().size() > 0) {
                    showListBankDialog();
                }else {
                    if (networkConnectivity.isNetworkConnected()) {
                        loading_rl.setVisibility(View.VISIBLE);
                        BankListRequest bankListRequest = new BankListRequest();
                        new RequestBankList(context, new RequestBanksTaskCompleteListener(true)).execute(bankListRequest);
                    }else {
                        Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        if (networkConnectivity.isNetworkConnected()) {
            BankListRequest bankListRequest = new BankListRequest();
            new RequestBankList(this, new RequestBanksTaskCompleteListener()).execute(bankListRequest);
        }

        keepOn_button = (ButtonRectangle) findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (networkConnectivity.isNetworkConnected()) {

                    if (cellNumberIsValid && nationalCodeIsValid && accountNumberIsValid) {

                        RegistrationEntryRequest registrationEntryRequest = new RegistrationEntryRequest();

                        registrationEntryRequest.setCellNumber(cellNumberValue.getText().toString());
                        registrationEntryRequest.setAccountNumber(accountNumberValue.getText().toString());
                        registrationEntryRequest.setBankCode(selectedBankValue);
                        registrationEntryRequest.setNationalCode(nationalCodeValue.getText().toString());
                        registrationEntryRequest.setImei(new DeviceInfo(getApplicationContext()).getIMEI());


                        new RequestRegistrationEntry(context, new RequestRegistrationEntryTaskCompleteListener()).execute(registrationEntryRequest);

                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.fill_data), Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    private void showListBankDialog(){
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
//                        accountNumberValue.setHint(bankListResponse.getService().getBanks().get(position).getAccountFormat());
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

    public class RequestBanksTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BankListResponse>>
    {
        boolean showBankList = false;

        public RequestBanksTaskCompleteListener(){
        }

        public RequestBanksTaskCompleteListener(boolean showBankList){
            this.showBankList = showBankList;
        }


        @Override
        public void onTaskComplete(ResponseMessage<BankListResponse> bankListResponseMessage)
        {
            bankListResponse = bankListResponseMessage;
            if (bankListResponseMessage != null) {
                selectedBankTitle.setText(bankListResponseMessage.getService().getBanks().get(0).getTitle());
                selectedBankValue = bankListResponseMessage.getService().getBanks().get(0).getTitle();
                loading_rl.setVisibility(View.GONE);
            }
            if (bankListResponse != null) {
                if (bankListResponse.getService().getBanks().size() > 0)
                    if (showBankList) {
                        showListBankDialog();
                    }
            }else {
                Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onTaskPreRun() {
//            loading_rl.setVisibility(View.VISIBLE);
        }
    }


    public class RequestRegistrationEntryTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationEntryResponse>>
    {
        @Override
        public void onTaskComplete(ResponseMessage<RegistrationEntryResponse> registrationEntryResponse)
        {
            registrationEntryResponseMessage = registrationEntryResponse;
            if (registrationEntryResponse != null) {

                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putString("UserIdToken", registrationEntryResponse.getService().getUserIdToken());
                editor.commit();
                Intent intent = new Intent();
                intent.setClass(ProfileEntryActivity.this, VerificationActivity.class);
                startActivity(intent);

                loading_rl.setVisibility(View.GONE);
            }
        }

        @Override
        public void onTaskPreRun() {
            loading_rl.setVisibility(View.VISIBLE);
        }
    }

}

