package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.CardProfileRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.common.core.model.response.CardProfileResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCardProfile;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationEntry;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.EmailTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.location.BestLocationListener;
import xyz.homapay.hampay.mobile.android.location.BestLocationProvider;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.NationalCodeVerification;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class ProfileEntryActivity extends AppCompatActivity {

    Activity activity;

    ButtonRectangle keepOn_button;
    //    RelativeLayout bankSelection;
    Dialog bankSelectionDialog;

    FacedEditText cellNumberValue;
    ImageView cellNumberIcon;
    boolean cellNumberIsValid = false;

    FacedEditText nationalCodeValue;
    ImageView nationalCodeIcon;
    boolean nationalCodeIsValid = false;

    FacedEditText accountNumberValue;
    FacedTextView cardProfile;
    boolean accountNumberIsValid = true;
    boolean verifiedCardNumber = false;
    String accountNumberFormat = "####-####-####-####";
    ImageView accountNumberIcon;

    ImageView userNameFamilyIcon;
    FacedEditText userNameFamily;
    boolean userNameFamilyIsValid = true;

    String selectedBankCode;

    EmailTextWatcher emailTextWatcher;
    FacedEditText emailValue;
    ImageView emailIcon;

    Context context;

    String rawAccountNumberValue = "";
    int rawAccountNumberValueLength = 0;
    int rawAccountNumberValueLengthOffset = 0;
    String procAccountNumberValue = "";

    private CardProfileResponse cardProfileResponse;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    RegistrationEntryRequest registrationEntryRequest;
    RequestRegistrationEntry requestRegistrationEntry;

    double latitute = 0.0;
    double longitude = 0.0;

    private static String TAG = "BestLocationProvider";
    private BestLocationProvider mBestLocationProvider;
    private BestLocationListener mBestLocationListener;

    CardProfileRequest cardProfileRequest;
    RequestCardProfile requestCardProfile;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;

    DeviceInfo deviceInfo;

    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);

        initLocation();
        mBestLocationProvider.stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HamPayApplication.setAppSate(AppState.Resumed);
        initLocation();
        mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_entry);

        context = this;

        activity = this;

        deviceInfo = new DeviceInfo(activity);

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

        userNameFamily = (FacedEditText)findViewById(R.id.userNameFamily);
        userNameFamilyIcon = (ImageView)findViewById(R.id.userNameFamilyIcon);
        userNameFamily.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {

                    userNameFamilyIsValid = true;

                    if (userNameFamily.getText().toString().length() == 0){
                        userNameFamilyIsValid = false;
                        userNameFamilyIcon.setImageResource(R.drawable.false_icon);

                    }else {
                        userNameFamilyIsValid = true;
                        userNameFamilyIcon.setImageResource(R.drawable.right_icon);
                    }
                }
            }
        });

        accountNumberValue = (FacedEditText)findViewById(R.id.accountNumberValue);
        cardProfile = (FacedTextView)findViewById(R.id.cardProfile);
        accountNumberIcon = (ImageView)findViewById(R.id.accountNumberIcon);
        accountNumberValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {

                    accountNumberIsValid = true;

                    String splitedFormat[] = accountNumberFormat.split("-");
                    String splitedAccountNo[] = accountNumberValue.getText().toString().split("-");

                    if (splitedAccountNo.length != splitedFormat.length) {
                        accountNumberIsValid = false;

                    } else {
                        for (int i = 0; i < splitedAccountNo.length; i++) {
                            if (splitedAccountNo[i].length() != splitedFormat[i].length()) {
                                accountNumberIsValid = false;
                            }
                        }
                    }

                    if (accountNumberIsValid) {
                        cardProfileRequest = new CardProfileRequest();
                        cardProfileRequest.setCardNumber(new PersianEnglishDigit().P2E(accountNumberValue.getText().toString()));
                        requestCardProfile = new RequestCardProfile(activity, new RequestCardProfileTaskCompleteListener());
                        requestCardProfile.execute(cardProfileRequest);
                    } else {
                        accountNumberIcon.setImageResource(R.drawable.false_icon);
                    }
                }
                else{

                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

                if (accountNumberValue.getText().toString().length() <= 19) {

                    rawAccountNumberValue = s.toString().replace("-", "");
                    rawAccountNumberValueLength = rawAccountNumberValue.length();
                    rawAccountNumberValueLengthOffset = 0;
                    procAccountNumberValue = "";
                    if (rawAccountNumberValue.length() > 0) {
                        for (int i = 0; i < rawAccountNumberValueLength; i++) {
                            if (accountNumberFormat.charAt(i + rawAccountNumberValueLengthOffset) == '-') {
                                procAccountNumberValue += "-" + rawAccountNumberValue.charAt(i);
                                rawAccountNumberValueLengthOffset++;
                            } else {
                                procAccountNumberValue += rawAccountNumberValue.charAt(i);
                            }
                        }

                        procAccountNumberValue = new PersianEnglishDigit(procAccountNumberValue).E2P();

                        accountNumberValue.setText(procAccountNumberValue);
                        accountNumberValue.setSelection(accountNumberValue.getText().toString().length());
                    }
                }
                accountNumberValue.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
                accountNumberValue.removeTextChangedListener(this);
                accountNumberValue.addTextChangedListener(this);
            }
        });



//        bankSelection = (RelativeLayout)findViewById(R.id.bankSelection);
//        bankSelection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                cellNumberValue.clearFocus();
//                accountNumberValue.clearFocus();
//                nationalCodeValue.clearFocus();
//                bankSelection.requestFocus();
//
//                if (bankListResponse != null) {
//                    if (bankListResponse.getBanks().size() > 0)
//                        showListBankDialog();
//                }else {
//                    hamPayDialog.showWaitingdDialog("");
//                    bankListRequest = new BankListRequest();
//                    requestBankList = new RequestBankList(context, new RequestBanksTaskCompleteListener(true));
//                    requestBankList.execute(bankListRequest);
//                }
//            }
//        });


        emailValue = (FacedEditText)findViewById(R.id.emailValue);
        emailIcon = (ImageView)findViewById(R.id.emailIcon);
        emailTextWatcher = new EmailTextWatcher(emailValue, emailIcon);

        emailValue.addTextChangedListener(emailTextWatcher);
        emailValue.setText(new DeviceInfo(activity).getDeviceEmailAccount());

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
                userNameFamily.clearFocus();
                nationalCodeValue.clearFocus();
                accountNumberValue.clearFocus();


                if (cellNumberIsValid && nationalCodeIsValid && verifiedCardNumber
                        && cellNumberValue.getText().toString().length() > 0
                        && nationalCodeValue.getText().toString().length() > 0
                        && accountNumberValue.getText().toString().length() > 0
                        && emailTextWatcher.isValid()) {

                    keepOn_button.setEnabled(false);

                    registrationEntryRequest = new RegistrationEntryRequest();

                    registrationEntryRequest.setCellNumber(new PersianEnglishDigit(cellNumberValue.getText().toString()).P2E());
                    registrationEntryRequest.setCardNumber(new PersianEnglishDigit(accountNumberValue.getText().toString()).P2E());
                    registrationEntryRequest.setFullName(userNameFamily.getText().toString());
                    registrationEntryRequest.setEmail(emailValue.getText().toString());
                    registrationEntryRequest.setNationalCode(new PersianEnglishDigit(nationalCodeValue.getText().toString()).P2E());

                    requestRegistrationEntry = new RequestRegistrationEntry(activity,
                            new RequestRegistrationEntryTaskCompleteListener(),
                            latitute + "," + longitude);

                    requestRegistrationEntry.execute(registrationEntryRequest);
                } else {

                    if (cellNumberValue.getText().toString().length() == 0 || !cellNumberIsValid){
                        Toast.makeText(context, getString(R.string.msg_cellNumber_invalid), Toast.LENGTH_SHORT).show();
                        cellNumberIcon.setImageResource(R.drawable.false_icon);
                        cellNumberValue.requestFocus();
                    }

                    else if (userNameFamily.getText().toString().length() == 0 || !userNameFamilyIsValid){
                        Toast.makeText(context, getString(R.string.msg_username_invalid), Toast.LENGTH_SHORT).show();
                        userNameFamilyIcon.setImageResource(R.drawable.false_icon);
                        userNameFamily.requestFocus();
                    }

                    else if (accountNumberValue.getText().toString().length() == 0 || !verifiedCardNumber){
                        Toast.makeText(context, getString(R.string.msg_accountNo_invalid), Toast.LENGTH_SHORT).show();
                        accountNumberIcon.setImageResource(R.drawable.false_icon);
                        accountNumberValue.requestFocus();
                    }

                    else if (nationalCodeValue.getText().toString().length() == 0 || !nationalCodeIsValid){
                        Toast.makeText(context, getString(R.string.msg_nationalCode_invalid), Toast.LENGTH_SHORT).show();
                        nationalCodeIcon.setImageResource(R.drawable.false_icon);
                        nationalCodeValue.requestFocus();
                    }
                    else if (!emailTextWatcher.isValid()){
                        Toast.makeText(context, getString(R.string.msg_invalid_email), Toast.LENGTH_SHORT).show();
                        emailValue.requestFocus();
                    }
                }

                keepOn_button.requestFocus();

            }
        });
    }

    public void contactUs(View view){
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/reg-userInfo.html");
    }



    public class RequestCardProfileTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<CardProfileResponse>>
    {

        public RequestCardProfileTaskCompleteListener(){

        }


        @Override
        public void onTaskComplete(ResponseMessage<CardProfileResponse> cardProfileResponseMessage)
        {
            if (cardProfileResponseMessage != null) {

                cardProfileResponse = cardProfileResponseMessage.getService();

                if (cardProfileResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    verifiedCardNumber = true;

                    cardProfile.setText(cardProfileResponse.getFullName() + " - " + cardProfileResponse.getBankName());

                    byte[] bytes = new byte[0];

                    bytes = Base64.decode(cardProfileResponse.getBankLogo(), Base64.DEFAULT);

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    accountNumberIcon.setImageBitmap(bitmap);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Bank List")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());
                }else {
                    requestCardProfile = new RequestCardProfile(context, new RequestCardProfileTaskCompleteListener());
                    new HamPayDialog(activity).showFailCardProfileDialog(requestCardProfile, cardProfileRequest,
                            cardProfileResponse.getResultStatus().getCode(),
                            cardProfileResponse.getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Bank List")
                            .setAction("Fetch")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestCardProfile = new RequestCardProfile(context, new RequestCardProfileTaskCompleteListener());
                new HamPayDialog(activity).showFailCardProfileDialog(requestCardProfile, cardProfileRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_fetch_card_profile));

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
                    editor.putString(Constants.REGISTERED_USER_NAME, userNameFamily.getText().toString());
                    editor.putString(Constants.REGISTERED_ACCOUNT_NO, accountNumberValue.getText().toString());
                    editor.putString(Constants.REGISTERED_NATIONAL_CODE, new PersianEnglishDigit(nationalCodeValue.getText().toString()).P2E());
                    editor.putString(Constants.REGISTERED_USER_ID_TOKEN, registrationEntryResponse.getService().getUserIdToken());
                    editor.putString(Constants.REGISTERED_USER_EMAIL, emailValue.getText().toString());
                    editor.commit();


                    hamPayDialog.smsConfirmDialog(cellNumberValue.getText().toString(),
                            accountNumberValue.getText().toString());

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
                    requestRegistrationEntry = new RequestRegistrationEntry(activity,
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
                requestRegistrationEntry = new RequestRegistrationEntry(activity, new RequestRegistrationEntryTaskCompleteListener(),
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

