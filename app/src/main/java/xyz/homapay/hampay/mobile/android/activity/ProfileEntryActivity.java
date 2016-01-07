package xyz.homapay.hampay.mobile.android.activity;

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
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.BankListRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.common.core.model.response.BankListResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.BankListAdapter;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestBankList;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationEntry;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.EmailTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.component.material.RippleView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.location.BestLocationListener;
import xyz.homapay.hampay.mobile.android.location.BestLocationProvider;
import xyz.homapay.hampay.mobile.android.util.AESHelper;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.EmailVerification;
import xyz.homapay.hampay.mobile.android.util.NationalCodeVerification;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.SecurityUtils;

public class ProfileEntryActivity extends Activity implements View.OnClickListener {

    RippleView digit_1;
    RippleView digit_2;
    RippleView digit_3;
    RippleView digit_4;
    RippleView digit_5;
    RippleView digit_6;
    RippleView digit_7;
    RippleView digit_8;
    RippleView digit_9;
    RippleView digit_0;
    RippleView delimiter;
    RippleView backspace;

    Activity activity;

    ButtonRectangle keepOn_button;
    RelativeLayout bankSelection;
    Dialog bankSelectionDialog;

    FacedEditText cellNumberValue;
    ImageView cellNumberIcon;
    boolean cellNumberIsValid = false;

    FacedEditText nationalCodeValue;
    ImageView nationalCodeIcon;
    boolean nationalCodeIsValid = false;

    FacedEditText accountNumberValue;
    boolean accountNumberIsValid = true;
    String accountNumberFormat;

    ImageView accountNumberIcon;
    FacedTextView selectedBankTitle;
    String selectedBankCode;

    FacedEditText emailValue;
    ImageView emailIcon;
    boolean emailIsValid = false;
    CheckBox email_confirm_check;

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

    Tracker hamPayGaTracker;

    DeviceInfo deviceInfo;

    LinearLayout bank_account_keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_entry);

        context = this;

        activity = this;

        deviceInfo = new DeviceInfo(context);

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

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

                if (!hasFocus) {
                    if (cellNumberValue.getText().toString().length() == 11
                            && cellNumberValue.getText().toString().startsWith("۰۹")) {
                        cellNumberIcon.setImageResource(R.drawable.right_icon);
                        cellNumberIsValid = true;
                    } else {
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nationalCodeValue.removeTextChangedListener(this);

                nationalCodeValue.setText(new PersianEnglishDigit(s.toString()).E2P());
                nationalCodeValue.setSelection(s.toString().length());
                nationalCodeValue.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        nationalCodeValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (new NationalCodeVerification(nationalCodeValue.getText().toString()).isValidCode()) {
                        nationalCodeIcon.setImageResource(R.drawable.right_icon);
                        nationalCodeIsValid = true;
                    } else {
                        nationalCodeIcon.setImageResource(R.drawable.false_icon);
                        nationalCodeIsValid = false;
                    }
                }
            }
        });


        digit_1 = (RippleView)findViewById(R.id.digit_1);
        digit_1.setOnClickListener(this);
        digit_2 = (RippleView)findViewById(R.id.digit_2);
        digit_2.setOnClickListener(this);
        digit_3 = (RippleView)findViewById(R.id.digit_3);
        digit_3.setOnClickListener(this);
        digit_4 = (RippleView)findViewById(R.id.digit_4);
        digit_4.setOnClickListener(this);
        digit_5 = (RippleView)findViewById(R.id.digit_5);
        digit_5.setOnClickListener(this);
        digit_6 = (RippleView)findViewById(R.id.digit_6);
        digit_6.setOnClickListener(this);
        digit_7 = (RippleView)findViewById(R.id.digit_7);
        digit_7.setOnClickListener(this);
        digit_8 = (RippleView)findViewById(R.id.digit_8);
        digit_8.setOnClickListener(this);
        digit_9 = (RippleView)findViewById(R.id.digit_9);
        digit_9.setOnClickListener(this);
        digit_0 = (RippleView)findViewById(R.id.digit_0);
        digit_0.setOnClickListener(this);
        delimiter = (RippleView)findViewById(R.id.delimiter);
        delimiter.setOnClickListener(this);
        backspace = (RippleView)findViewById(R.id.backspace);
        backspace.setOnClickListener(this);

        bank_account_keyboard = (LinearLayout)findViewById(R.id.bank_account_keyboard);

        accountNumberValue = (FacedEditText)findViewById(R.id.accountNumberValue);
        accountNumberValue.setInputType(InputType.TYPE_NULL);
        accountNumberIcon = (ImageView)findViewById(R.id.accountNumberIcon);
//        accountNumberValue.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                new Expand(bank_account_keyboard).animate();
//
//                return true;
//            }
//        });
        accountNumberValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {

                    new Collapse(bank_account_keyboard).animate();

                    accountNumberIsValid = true;

//                    String splitedFormat[] = accountNumberFormat.split("/");
//                    String splitedAccountNo[] = accountNumberValue.getText().toString().split("/");
//
//                    if (splitedAccountNo.length != splitedFormat.length) {
//                        accountNumberIsValid = false;
//
//                    } else {
//                        for (int i = 0; i < splitedAccountNo.length; i++) {
//                            if (splitedAccountNo[i].length() != splitedFormat[i].length()) {
//                                accountNumberIsValid = false;
//                            }
//                        }
//                    }
//
//                    if (accountNumberIsValid) {
//                        accountNumberIcon.setImageResource(R.drawable.right_icon);
//                    } else {
//                        accountNumberIcon.setImageResource(R.drawable.false_icon);
//                    }
                }
                else{

                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    new Expand(bank_account_keyboard).animate();
                }
            }
        });


//        accountNumberValue.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                accountNumberValue.removeTextChangedListener(this);
//                accountNumberValue.addTextChangedListener(this);
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                accountNumberValue.removeTextChangedListener(this);
//
//
//                rawAccountNumberValue = s.toString().replace("/", "");
//                rawAccountNumberValueLength = rawAccountNumberValue.length();
//                rawAccountNumberValueLengthOffset = 0;
//                procAccountNumberValue = "";
//                if (rawAccountNumberValue.length() > 0) {
//                    for (int i = 0; i < rawAccountNumberValueLength; i++) {
//                        if (accountNumberFormat.charAt(i + rawAccountNumberValueLengthOffset) == '/') {
//                            procAccountNumberValue += "/" + rawAccountNumberValue.charAt(i);
//                            rawAccountNumberValueLengthOffset++;
//                        } else {
//                            procAccountNumberValue += rawAccountNumberValue.charAt(i);
//                        }
//                    }
//
//                    procAccountNumberValue = new PersianEnglishDigit(procAccountNumberValue).E2P();
//
//                    accountNumberValue.setText(procAccountNumberValue);
//                    accountNumberValue.setSelection(accountNumberValue.getText().toString().length());
//                }
//                accountNumberValue.addTextChangedListener(this);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                accountNumberValue.removeTextChangedListener(this);
//                accountNumberValue.addTextChangedListener(this);
//            }
//        });

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


        emailValue = (FacedEditText)findViewById(R.id.emailValue);
        email_confirm_check = (CheckBox)findViewById(R.id.email_confirm_check);
        emailIcon = (ImageView)findViewById(R.id.emailIcon);
        email_confirm_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!new EmailVerification().isValid(emailValue.getText().toString())) {
                        email_confirm_check.setChecked(false);
                    }else {
                        emailIsValid = true;
                    }
                }
            }
        });
        emailValue.addTextChangedListener(new EmailTextWatcher(emailValue, emailIcon, email_confirm_check));
        emailValue.setText(new DeviceInfo(context).getDeviceEmailAccount());



        hamPayDialog.showWaitingdDialog("");
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

                if (email_confirm_check.isChecked()){
                    if (!emailIsValid){
                        Toast.makeText(context, getString(R.string.msg_email_invalid), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (cellNumberIsValid && nationalCodeIsValid && accountNumberIsValid
                        && cellNumberValue.getText().toString().length() > 0
                        && nationalCodeValue.getText().toString().length() > 0
                        && accountNumberValue.getText().toString().length() > 0) {

                    keepOn_button.setEnabled(false);

                    registrationEntryRequest = new RegistrationEntryRequest();

                    registrationEntryRequest.setCellNumber(new PersianEnglishDigit(cellNumberValue.getText().toString()).P2E());
                    registrationEntryRequest.setAccountNumber(new PersianEnglishDigit(accountNumberValue.getText().toString()).P2E());
                    registrationEntryRequest.setBankCode(selectedBankCode);
                    registrationEntryRequest.setEmail(emailValue.getText().toString());
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.digit_1:
                inputDigit("۱");
                break;

            case R.id.digit_2:
                inputDigit("۲");
                break;

            case R.id.digit_3:
                inputDigit("۳");
                break;

            case R.id.digit_4:
                inputDigit("۴");
                break;

            case R.id.digit_5:
                inputDigit("۵");
                break;

            case R.id.digit_6:
                inputDigit("۶");
                break;

            case R.id.digit_7:
                inputDigit("۷");
                break;

            case R.id.digit_8:
                inputDigit("۸");
                break;

            case R.id.digit_9:
                inputDigit("۹");
                break;

            case R.id.digit_0:
                inputDigit("۰");
                break;

            case R.id.backspace:
                inputDigit("d");
                break;

            case R.id.delimiter:
                inputDigit("/");
                break;
        }
    }

    private void inputDigit(String digit) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(20);
        if (digit.contains("d")) {
            if (accountNumberValue.getText().toString().length() > 0) {
                accountNumberValue.setText(accountNumberValue.getText().toString().substring(0, accountNumberValue.getText().toString().length() - 1));
            }
        }else {
            accountNumberValue.setText(accountNumberValue.getText().toString() + digit);
        }
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

                bankListResponse = bankListResponseMessage.getService();

                if (bankListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    if (showBankList) {
                        showListBankDialog();
                    }
                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Bank List")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());
                }else {
                    bankListResponseMessage.getService().getResultStatus().getDescription();
                    requestBankList = new RequestBankList(context, new RequestBanksTaskCompleteListener(true));
                    new HamPayDialog(activity).showFailBankListDialog(requestBankList, bankListRequest,
                            bankListResponse.getResultStatus().getCode(),
                            bankListResponse.getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Bank List")
                            .setAction("Fetch")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestBankList = new RequestBankList(context, new RequestBanksTaskCompleteListener(true));
                new HamPayDialog(activity).showFailBankListDialog(requestBankList, bankListRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_fetch_bank_list));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Bank List")
                        .setAction("Fetch")
                        .setLabel("Fail(Mobile)")
                        .build());
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
                    editor.putString(Constants.REGISTERED_CELL_NUMBER, new PersianEnglishDigit(cellNumberValue.getText().toString()).P2E());
                    editor.putString(Constants.REGISTERED_BANK_ID, selectedBankCode);
                    editor.putString(Constants.REGISTERED_BANK_ACCOUNT_NO_FORMAT, accountNumberFormat);
                    editor.putString(Constants.REGISTERED_ACCOUNT_NO, accountNumberValue.getText().toString());
                    editor.putString(Constants.REGISTERED_NATIONAL_CODE, new PersianEnglishDigit(nationalCodeValue.getText().toString()).P2E());
                    editor.putString(Constants.REGISTERED_USER_ID_TOKEN, registrationEntryResponse.getService().getUserIdToken());
                    editor.putString(Constants.REGISTERED_USER_EMAIL, emailValue.getText().toString());
                    editor.commit();

                    Intent intent = new Intent();
                    intent.setClass(ProfileEntryActivity.this, VerificationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Registration Entry")
                            .setAction("Entry")
                            .setLabel("Success")
                            .build());

                }else if (registrationEntryResponse.getService().getResultStatus() == ResultStatus.REGISTRATION_INVALID_STEP){
                    new HamPayDialog(activity).showInvalidStepDialog();

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Registration Entry")
                            .setAction("Entry")
                            .setLabel("Success(Invalid)")
                            .build());
                }
                else {
                    requestRegistrationEntry = new RequestRegistrationEntry(context,
                            new RequestRegistrationEntryTaskCompleteListener(),
                            latitute + "," + longitude);
                    new HamPayDialog(activity).showFailRegistrationEntryDialog(requestRegistrationEntry, registrationEntryRequest,
                            registrationEntryResponse.getService().getResultStatus().getCode(),
                            registrationEntryResponse.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Registration Entry")
                            .setAction("Entry")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestRegistrationEntry = new RequestRegistrationEntry(context, new RequestRegistrationEntryTaskCompleteListener(),
                        latitute + "," + longitude);
                new HamPayDialog(activity).showFailRegistrationEntryDialog(requestRegistrationEntry, registrationEntryRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_registration_entry));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Registration Entry")
                        .setAction("Entry")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() {
            keepOn_button.setEnabled(false);
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

