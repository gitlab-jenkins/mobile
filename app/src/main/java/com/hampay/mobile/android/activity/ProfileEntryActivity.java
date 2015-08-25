package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
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
import com.hampay.mobile.android.location.BestLocationListener;
import com.hampay.mobile.android.location.BestLocationProvider;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.NationalCodeVerification;
import com.hampay.mobile.android.util.PersianEnglishDigit;

public class ProfileEntryActivity extends Activity {

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

    Context context;

    String rawAccountNumberValue = "";
    int rawAccountNumberValueLength = 0;
    int rawAccountNumberValueLengthOffset = 0;
    String procAccountNumberValue = "";

    private BankListResponse bankListResponse;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    RegistrationEntryRequest registrationEntryRequest;
    RequestRegistrationEntry requestRegistrationEntry;

    double latitute = 0.0;
    double longitude = 0.0;

    private static String TAG = "BestLocationProvider";
    private BestLocationProvider mBestLocationProvider;
    private BestLocationListener mBestLocationListener;

    BankListRequest bankListRequest;
    RequestBankList requestBankList;

    HamPayDialog hamPayDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_entry);

        context = this;

        activity = this;

        initLocation();

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, ProfileEntryActivity.class.getName());
        editor.commit();

        hamPayDialog = new HamPayDialog(activity);

        cellNumberValue = (FacedEditText)findViewById(R.id.cellNumberValue);
        cellNumberValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cellNumberValue.removeTextChangedListener(this);
                cellNumberValue.setText(new PersianEnglishDigit(s.toString()).E2P());
                cellNumberValue.setSelection(s.toString().length());
                cellNumberValue.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cellNumberIcon = (ImageView)findViewById(R.id.cellNumberIcon);
        cellNumberValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus){
                    if (cellNumberValue.getText().toString().length() == 11
                            && cellNumberValue.getText().toString().startsWith("۰۹")){
                        cellNumberIcon.setImageResource(R.drawable.right_icon);
                        cellNumberIsValid = true;
                    }
                    else {
                        cellNumberIcon.setImageResource(R.drawable.false_icon);
                        cellNumberIsValid = false;
                    }
                }
            }
        });


        nationalCodeValue = (FacedEditText)findViewById(R.id.nationalCodeValue);
        nationalCodeIcon = (ImageView)findViewById(R.id.nationalCodeIcon);
        nationalCodeValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nationalCodeValue.removeTextChangedListener(this);

                nationalCodeValue.setText(new PersianEnglishDigit(s.toString()).E2P());
                nationalCodeValue.setSelection(s.toString().length());
                nationalCodeValue.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

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

                    procAccountNumberValue = new PersianEnglishDigit(procAccountNumberValue).E2P();

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

                if (bankListResponse != null) {
                    if (bankListResponse.getBanks().size() > 0)
                        showListBankDialog();
                }else {
                    hamPayDialog.showWaitingdDialog("");
                    bankListRequest = new BankListRequest();
                    requestBankList = new RequestBankList(context, new RequestBanksTaskCompleteListener(true));
                    requestBankList.execute(bankListRequest);
                }
            }
        });

        bankListRequest = new BankListRequest();
        requestBankList = new RequestBankList(this, new RequestBanksTaskCompleteListener(false));
        requestBankList.execute(bankListRequest);

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

                if (cellNumberIsValid && nationalCodeIsValid && accountNumberIsValid
                        && cellNumberValue.getText().toString().length() > 0
                        && nationalCodeValue.getText().toString().length() > 0
                        && accountNumberValue.getText().toString().length() > 0) {

                    keepOn_button.setEnabled(false);

                    registrationEntryRequest = new RegistrationEntryRequest();

                    registrationEntryRequest.setCellNumber(new PersianEnglishDigit(cellNumberValue.getText().toString()).P2E());
                    registrationEntryRequest.setAccountNumber(new PersianEnglishDigit(accountNumberValue.getText().toString()).P2E());
                    registrationEntryRequest.setBankCode(selectedBankCode);
                    registrationEntryRequest.setNationalCode(new PersianEnglishDigit(nationalCodeValue.getText().toString()).P2E());

                    requestRegistrationEntry = new RequestRegistrationEntry(context,
                            new RequestRegistrationEntryTaskCompleteListener(),
                            latitute + "," + longitude);

                    requestRegistrationEntry.execute(registrationEntryRequest);
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

                keepOn_button.requestFocus();

            }
        });
    }

    public void contactUs(View view){
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/reg-userInfo.html");
    }


    private void showListBankDialog(){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) ProfileEntryActivity.this;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = getLayoutInflater().inflate(R.layout.dialog_bank_select, null);

        ListView bankListView = (ListView) view.findViewById(R.id.bankListView);

        BankListAdapter bankListAdapter = new BankListAdapter(getApplicationContext(), bankListResponse.getBanks());

        bankListView.setAdapter(bankListAdapter);
        bankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBankTitle.setText(bankListResponse.getBanks().get(position).getTitle());
                selectedBankCode = bankListResponse.getBanks().get(position).getCode();
                if (accountNumberValue.getText().toString().length() > 0)
                    accountNumberValue.setText("");
                accountNumberFormat = bankListResponse.getBanks().get(position).getAccountFormat();
                accountNumberIsValid = false;
                accountNumberIcon.setImageDrawable(null);
                accountNumberValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(accountNumberFormat.length())});
                bankSelectionDialog.dismiss();
                accountNumberValue.setFocusableInTouchMode(true);
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

        public RequestBanksTaskCompleteListener(boolean showBankList){
            this.showBankList = showBankList;
        }


        @Override
        public void onTaskComplete(ResponseMessage<BankListResponse> bankListResponseMessage)
        {

            hamPayDialog.dismisWaitingDialog();

            if (bankListResponseMessage != null) {

                if (bankListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    bankListResponse = bankListResponseMessage.getService();
                    if (showBankList) {
                        showListBankDialog();
                    }
                }else {
                    bankListResponseMessage.getService().getResultStatus().getDescription();
                    requestBankList = new RequestBankList(context, new RequestBanksTaskCompleteListener(true));
                    new HamPayDialog(activity).showFailBankListDialog(requestBankList, bankListRequest,
                            bankListResponse.getResultStatus().getCode(),
                            bankListResponse.getResultStatus().getDescription());
                }
            }else {
                requestBankList = new RequestBankList(context, new RequestBanksTaskCompleteListener(true));
                new HamPayDialog(activity).showFailBankListDialog(requestBankList, bankListRequest,
                        "2000",
                        getString(R.string.msg_fail_fetch_bank_list));
            }
        }

        @Override
        public void onTaskPreRun() {}
    }


    public class RequestRegistrationEntryTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationEntryResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<RegistrationEntryResponse> registrationEntryResponse)
        {

            keepOn_button.setEnabled(true);
            hamPayDialog.dismisWaitingDialog();
            if (registrationEntryResponse != null) {

                if (registrationEntryResponse.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    editor.putString(Constants.REGISTERED_CELL_NUMBER, cellNumberValue.getText().toString());
                    editor.putString(Constants.REGISTERED_BANK_ID, selectedBankCode);
                    editor.putString(Constants.REGISTERED_BANK_ACCOUNT_NO_FORMAT, accountNumberFormat);
                    editor.putString(Constants.REGISTERED_ACCOUNT_NO, accountNumberValue.getText().toString());
                    editor.putString(Constants.REGISTERED_NATIONAL_CODE, new PersianEnglishDigit(nationalCodeValue.getText().toString()).P2E());
                    editor.putString(Constants.REGISTERED_USER_ID_TOKEN, registrationEntryResponse.getService().getUserIdToken());
                    editor.commit();

                    Intent intent = new Intent();
                    intent.setClass(ProfileEntryActivity.this, VerificationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }else if (registrationEntryResponse.getService().getResultStatus() == ResultStatus.REGISTRATION_INVALID_STEP){
                    new HamPayDialog(activity).showInvalidStepDialog();
                }
                else {
                    requestRegistrationEntry = new RequestRegistrationEntry(context,
                            new RequestRegistrationEntryTaskCompleteListener(),
                            latitute + "," + longitude);
                    new HamPayDialog(activity).showFailRegistrationEntryDialog(requestRegistrationEntry, registrationEntryRequest,
                            registrationEntryResponse.getService().getResultStatus().getCode(),
                            registrationEntryResponse.getService().getResultStatus().getDescription());
                }
            }else {
                requestRegistrationEntry = new RequestRegistrationEntry(context, new RequestRegistrationEntryTaskCompleteListener(),
                        latitute + "," + longitude);
                new HamPayDialog(activity).showFailRegistrationEntryDialog(requestRegistrationEntry, registrationEntryRequest,
                        "2000",
                        getString(R.string.msg_fail_registration_entry));
            }
        }

        @Override
        public void onTaskPreRun() {
            keepOn_button.setEnabled(false);
//            loading_rl.setVisibility(View.VISIBLE);
            hamPayDialog.showWaitingdDialog("");
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }


    @Override
    protected void onResume() {
        initLocation();
        mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);

        super.onResume();
    }

    @Override
    protected void onPause() {
        initLocation();
        mBestLocationProvider.stopLocationUpdates();

        super.onPause();
    }


    private void initLocation(){
        if(mBestLocationListener == null){
            mBestLocationListener = new BestLocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.i(TAG, "onStatusChanged PROVIDER:" + provider + " STATUS:" + String.valueOf(status));
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.i(TAG, "onProviderEnabled PROVIDER:" + provider);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.i(TAG, "onProviderDisabled PROVIDER:" + provider);
                }

                @Override
                public void onLocationUpdateTimeoutExceeded(BestLocationProvider.LocationType type) {
                    Log.w(TAG, "onLocationUpdateTimeoutExceeded PROVIDER:" + type);
                }

                @Override
                public void onLocationUpdate(Location location, BestLocationProvider.LocationType type,
                                             boolean isFresh) {
                    latitute = location.getLatitude();
                    longitude = location.getLongitude();

                }
            };

            if(mBestLocationProvider == null){
                mBestLocationProvider = new BestLocationProvider(this, true, true, 0, 0, 0, 0);
            }
        }
    }

}

