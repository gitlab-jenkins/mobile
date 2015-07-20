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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.DeviceInfo;
import com.hampay.mobile.android.util.NationalCodeVerification;
import com.hampay.mobile.android.util.NetworkConnectivity;

public class ProfileEntryActivity extends ActionBarActivity {

    Activity activity;

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
    boolean accountNumberIsValid = true;
    String accountNumberFormat;

    ImageView accountNumberIcon;
    FacedTextView selectedBankTitle;
    String selectedBankCode;
    RelativeLayout loading_rl;

    Context context;

    NetworkConnectivity networkConnectivity;

    String rawAccountNumberValue = "";
    int rawAccountNumberValueLength = 0;
    int rawAccountNumberValueLengthOffset = 0;
    String procAccountNumberValue = "";

    private ResponseMessage<BankListResponse> bankListResponse;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_entry);

        context = this;

        activity = this;

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        networkConnectivity = new NetworkConnectivity(this);

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);


        cellNumberValue = (FacedEditText)findViewById(R.id.cellNumberValue);
        cellNumberIcon = (ImageView)findViewById(R.id.cellNumberIcon);
        cellNumberValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus){
                    if (cellNumberValue.getText().toString().length() == 11
                            && cellNumberValue.getText().toString().startsWith("09")){
                        cellNumberIcon.setImageResource(R.drawable.right_icon);
                        cellNumberIsValid = true;
                    }
                    else {
                        cellNumberIcon.setImageResource(R.drawable.false_icon);
                        cellNumberIsValid = false;
                    }
                }
//                else {
//                    cellNumberIcon.setImageDrawable(null);
//                }
            }
        });


        nationalCodeValue = (FacedEditText)findViewById(R.id.nationalCodeValue);
        nationalCodeIcon = (ImageView)findViewById(R.id.nationalCodeIcon);
        nationalCodeValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){

                    if (new NationalCodeVerification(nationalCodeValue.getText().toString()).isValidCode()){
                        nationalCodeIcon.setImageResource(R.drawable.right_icon);
                        nationalCodeIsValid = true;
                    }
                    else {
                        nationalCodeIcon.setImageResource(R.drawable.false_icon);
                        nationalCodeIsValid = false;
                    }

                }
//                else {
//                    nationalCodeIcon.setImageDrawable(null);
//                }
            }
        });

        accountNumberValue = (FacedEditText)findViewById(R.id.accountNumberValue);
        accountNumberIcon = (ImageView)findViewById(R.id.accountNumberIcon);
        accountNumberValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus){

                    accountNumberIsValid = true;

                    String splitedFormat[] = accountNumberFormat.split("/");
                    String splitedAccountNo[] = accountNumberValue.getText().toString().split("/");

                    if (splitedAccountNo.length != splitedFormat.length){
                        accountNumberIsValid = false;

                    }else{
                        for (int i = 0; i < splitedAccountNo.length; i++){
                            if (splitedAccountNo[i].length() != splitedFormat[i].length()){
                                accountNumberIsValid = false;
                            }
                        }
                    }

                    if (accountNumberIsValid){
                        accountNumberIcon.setImageResource(R.drawable.right_icon);
                    }else {
                        accountNumberIcon.setImageResource(R.drawable.false_icon);
                    }

                }
//                else {
//                    accountNumberIcon.setImageDrawable(null);
//                }
            }
        });


        accountNumberValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                accountNumberValue.removeTextChangedListener(this);
                accountNumberValue.addTextChangedListener(this);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                accountNumberValue.removeTextChangedListener(this);
                rawAccountNumberValue = s.toString().replace("/", "");
                rawAccountNumberValueLength = rawAccountNumberValue.length();
                rawAccountNumberValueLengthOffset = 0;
                procAccountNumberValue = "";
                if (rawAccountNumberValue.length() > 0) {
                    for (int i = 0; i < rawAccountNumberValueLength; i++) {
                        if (accountNumberFormat.charAt(i + rawAccountNumberValueLengthOffset) == '/') {
                            procAccountNumberValue += "/" + rawAccountNumberValue.charAt(i);
                            rawAccountNumberValueLengthOffset++;
                        } else {
                            procAccountNumberValue += rawAccountNumberValue.charAt(i);
                        }
                    }
                    accountNumberValue.setText(procAccountNumberValue);
                    accountNumberValue.setSelection(accountNumberValue.getText().toString().length());
                }
                accountNumberValue.addTextChangedListener(this);
            }
            @Override
            public void afterTextChanged(Editable s) {
                accountNumberValue.removeTextChangedListener(this);
                accountNumberValue.addTextChangedListener(this);
            }
        });

        selectedBankTitle = (FacedTextView)findViewById(R.id.selectedBankText);

        bankSelection = (RelativeLayout)findViewById(R.id.bankSelection);
        bankSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cellNumberValue.clearFocus();
                accountNumberValue.clearFocus();
                nationalCodeValue.clearFocus();
                bankSelection.requestFocus();

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

                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                cellNumberValue.clearFocus();
                nationalCodeValue.clearFocus();
                accountNumberValue.clearFocus();

                if (networkConnectivity.isNetworkConnected()) {

                    if (cellNumberIsValid && nationalCodeIsValid && accountNumberIsValid
                            && cellNumberValue.getText().toString().length() > 0
                            && nationalCodeValue.getText().toString().length() > 0
                            && accountNumberValue.getText().toString().length() > 0) {

                        keepOn_button.setEnabled(false);

                        RegistrationEntryRequest registrationEntryRequest = new RegistrationEntryRequest();

                        registrationEntryRequest.setCellNumber(cellNumberValue.getText().toString());
                        registrationEntryRequest.setAccountNumber(accountNumberValue.getText().toString());
                        registrationEntryRequest.setBankCode(selectedBankCode);
                        registrationEntryRequest.setNationalCode(nationalCodeValue.getText().toString());
                        registrationEntryRequest.setImei(new DeviceInfo(getApplicationContext()).getIMEI());

                        new RequestRegistrationEntry(context, new RequestRegistrationEntryTaskCompleteListener()).execute(registrationEntryRequest);

                    } else {

                        if (cellNumberValue.getText().toString().length() == 0 || !cellNumberIsValid){
                            Toast.makeText(context, getString(R.string.msg_cellNumber_invalid), Toast.LENGTH_SHORT).show();
                            cellNumberIcon.setImageResource(R.drawable.false_icon);
                            cellNumberValue.requestFocus();
                        }


                        else if (accountNumberValue.getText().toString().length() == 0 || !accountNumberIsValid){
                            Toast.makeText(context, getString(R.string.msg_accountNo_invalid), Toast.LENGTH_SHORT).show();
                            accountNumberIcon.setImageResource(R.drawable.false_icon);
                            accountNumberValue.requestFocus();
                        }

                        else if (nationalCodeValue.getText().toString().length() == 0 || !nationalCodeIsValid){
                            Toast.makeText(context, getString(R.string.msg_nationalCode_invalid), Toast.LENGTH_SHORT).show();
                            nationalCodeIcon.setImageResource(R.drawable.false_icon);
                            nationalCodeValue.requestFocus();
                        }
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }

                keepOn_button.requestFocus();

            }
        });
    }

    public void contactUs(View view){
        new HamPayDialog(this).showContactUsDialog();
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
                selectedBankCode = bankListResponse.getService().getBanks().get(position).getCode();
                if (accountNumberValue.getText().toString().length() > 0)
                    accountNumberValue.setText("");
                accountNumberFormat = bankListResponse.getService().getBanks().get(position).getAccountFormat();
                accountNumberIsValid = false;
                accountNumberIcon.setImageDrawable(null);
                accountNumberValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(accountNumberFormat.length())});
//                accountNumberHint.setHint(bankListResponse.getService().getBanks().get(position).getAccountFormat());
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
                selectedBankCode = bankListResponseMessage.getService().getBanks().get(0).getCode();
                accountNumberFormat = bankListResponse.getService().getBanks().get(0).getAccountFormat();
                accountNumberValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(accountNumberFormat.length())});

//                accountNumberHint.setHint(bankListResponse.getService().getBanks().get(0).getAccountFormat());
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
            if (registrationEntryResponse != null) {


                editor.putString(Constants.REGISTERED_ACTIVITY_DATA, ProfileEntryActivity.class.toString());
                editor.putString(Constants.REGISTERED_CELL_NUMBER, cellNumberValue.getText().toString());
                editor.putString(Constants.REGISTERED_BANK_ID, selectedBankCode);
                editor.putString(Constants.REGISTERED_BANK_ACCOUNT_NO_FORMAT, accountNumberFormat);
                editor.putString(Constants.REGISTERED_ACCOUNT_NO, accountNumberValue.getText().toString());
                editor.putString(Constants.REGISTERED_NATIONAL_CODE, nationalCodeValue.getText().toString());
                editor.putString(Constants.REGISTERED_USER_ID_TOKEN, registrationEntryResponse.getService().getUserIdToken());
                editor.commit();

                Intent intent = new Intent();
                intent.setClass(ProfileEntryActivity.this, VerificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

                keepOn_button.setEnabled(true);
                loading_rl.setVisibility(View.GONE);
            }
        }

        @Override
        public void onTaskPreRun() {
            keepOn_button.setEnabled(false);
            loading_rl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }

}

