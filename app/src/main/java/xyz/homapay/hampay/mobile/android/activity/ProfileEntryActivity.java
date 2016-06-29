package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.CardProfileRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.common.core.model.response.CardProfileResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Manifest;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCardProfile;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationEntry;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.EmailTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.preloader.Preloader;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.location.BestLocationListener;
import xyz.homapay.hampay.mobile.android.location.BestLocationProvider;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.util.CardNumberValidator;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.NationalCodeVerification;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class ProfileEntryActivity extends AppCompatActivity {

    Activity activity;

    PersianEnglishDigit persianEnglishDigit;

    FacedTextView keepOn_button;

    FacedEditText cellNumberValue;
    ImageView cellNumberIcon;
    boolean cellNumberIsValid = false;
    Preloader preloader;

    FacedEditText nationalCodeValue;
    ImageView nationalCodeIcon;
    boolean nationalCodeIsValid = false;

    FacedEditText cardNumberValue;
    FacedTextView cardProfile;
    boolean verifiedCardNumber = false;
    ImageView cardNumberIcon;

    ImageView userNameFamilyIcon;
    FacedEditText userNameFamily;
    boolean userNameFamilyIsValid = true;

    String selectedBankCode;

    EmailTextWatcher emailTextWatcher;
    FacedEditText emailValue;
    ImageView emailIcon;

    Context context;

    String rawCardNumberValue = "";
    int rawCardNumberValueLength = 0;
    int rawCardNumberValueLengthOffset = 0;
    String procCardNumberValue = "";


    String rawNationalCode = "";
    int rawNationalCodeLength = 0;
    int rawNationalCodeLengthOffset = 0;
    String procNationalCode = "";

    private CardProfileResponse cardProfileResponse;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    RegistrationEntryRequest registrationEntryRequest;
    RequestRegistrationEntry requestRegistrationEntry;

    double latitude = 0.0;
    double longitude = 0.0;

    private static String TAG = "BestLocationProvider";
    private BestLocationProvider mBestLocationProvider;
    private BestLocationListener mBestLocationListener;

    CardProfileRequest cardProfileRequest;
    RequestCardProfile requestCardProfile;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;

    DeviceInfo deviceInfo;

    private CardNumberValidator cardNumberValidator;

    private ArrayList<PermissionListener> permissionListeners = new ArrayList<>();

    public void userManual(View view){
        Intent intent = new Intent();
        intent.setClass(activity, UserManualActivity.class);
        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_profile_entry);
        intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_profile_entry);
        startActivity(intent);
    }

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
//        initLocation();
//        requestReadFineLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        for (PermissionListener permissionListener : permissionListeners)
            if (permissionListener.onResult(requestCode, permissions, grantResults)) {
                permissionListeners.remove(permissionListener);
            }
    }

    private void requestReadFineLocation() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        permissionListeners = new RequestPermissions().request(activity, Constants.ACCESS_FINE_LOCATION, permissions, new PermissionListener() {
            @Override
            public boolean onResult(int requestCode, String[] requestPermissions, int[] grantResults) {
                if (requestCode == Constants.ACCESS_FINE_LOCATION) {
                    if (requestPermissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);
                    } else {
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void requestAndLoadPhoneState() {
        String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
        permissionListeners = new RequestPermissions().request(activity, Constants.READ_PHONE_STATE, permissions, new PermissionListener() {
            @Override
            public boolean onResult(int requestCode, String[] requestPermissions, int[] grantResults) {
                if (requestCode == Constants.READ_PHONE_STATE) {
                    // Check if the permission is correct and is granted
                    if (requestPermissions[0].equals(Manifest.permission.READ_PHONE_STATE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission granted

//                        Toast.makeText(activity, "Access allowed!", Toast.LENGTH_SHORT).show();

                        requestUserAccount();

                    } else {
                        // Permission not granted
//                        Toast.makeText(activity, "Access denied!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void requestUserAccount() {
        String[] permissions = new String[]{Manifest.permission.GET_ACCOUNTS};
        permissionListeners = new RequestPermissions().request(activity, Constants.GET_ACCOUNTS, permissions, new PermissionListener() {
            @Override
            public boolean onResult(int requestCode, String[] requestPermissions, int[] grantResults) {
                if (requestCode == Constants.GET_ACCOUNTS) {
                    if (requestPermissions[0].equals(Manifest.permission.GET_ACCOUNTS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        emailValue.setText(new DeviceInfo(activity).getDeviceEmailAccount());
                        requestReadFineLocation();
                        initLocation();
                    } else {
                    }
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_entry);

        context = this;

        activity = this;

        deviceInfo = new DeviceInfo(activity);

        persianEnglishDigit = new PersianEnglishDigit();

        cardNumberValidator = new CardNumberValidator();

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        requestAndLoadPhoneState();

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
                cellNumberValue.setText(persianEnglishDigit.E2P(s.toString()));
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
                    if (cellNumberValue.getText().toString().length() == 9) {
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
                if (nationalCodeValue.getText().toString().length() <= 12) {
                    rawNationalCode = s.toString().replace("-", "");
                    rawNationalCodeLength = rawNationalCode.length();
                    rawNationalCodeLengthOffset = 0;
                    procNationalCode = "";
                    if (rawNationalCode.length() > 0) {
                        for (int i = 0; i < rawNationalCodeLength; i++) {
                            if (Constants.NATIONAL_CODE_FORMAT.charAt(i + rawNationalCodeLengthOffset) == '-') {
                                procNationalCode += "-" + rawNationalCode.charAt(i);
                                rawNationalCodeLengthOffset++;
                            } else {
                                procNationalCode += rawNationalCode.charAt(i);
                            }
                        }
                        procNationalCode = persianEnglishDigit.E2P(procNationalCode);
                        nationalCodeValue.setText(procNationalCode);
                        nationalCodeValue.setSelection(nationalCodeValue.getText().toString().length());
                    }
                }


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
                    if (new NationalCodeVerification(nationalCodeValue.getText().toString().replaceAll("-", "")).isValidCode()) {
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
                    if (userNameFamily.getText().toString().length() <= 1){
                        userNameFamilyIsValid = false;
                        userNameFamilyIcon.setImageResource(R.drawable.false_icon);
                    }else {
                        userNameFamilyIsValid = true;
                        userNameFamilyIcon.setImageResource(R.drawable.right_icon);
                    }
                }
            }
        });

        preloader = (Preloader)findViewById(R.id.preloader);

        cardNumberValue = (FacedEditText)findViewById(R.id.cardNumberValue);
        cardProfile = (FacedTextView)findViewById(R.id.cardProfile);
        cardNumberIcon = (ImageView)findViewById(R.id.cardNumberIcon);
        cardNumberValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                String cardNumber = persianEnglishDigit.P2E(cardNumberValue.getText().toString().replaceAll("-", ""));

                cardNumberIcon.setVisibility(View.GONE);
                preloader.setVisibility(View.GONE);

                if (!hasFocus) {
                    if (cardNumberValidator.validate(cardNumber)){
                        verifiedCardNumber = true;
                        cardNumberIcon.setImageResource(R.drawable.right_icon);
                        cardNumberIcon.setVisibility(View.VISIBLE);
//                        preloader.setVisibility(View.VISIBLE);
//                        cardProfileRequest = new CardProfileRequest();
//                        cardProfileRequest.setCardNumber(cardNumber);
//                        requestCardProfile = new RequestCardProfile(activity, new RequestCardProfileTaskCompleteListener());
//                        requestCardProfile.execute(cardProfileRequest);
                    }else {
                        cardNumberIcon.setVisibility(View.VISIBLE);
                        cardNumberIcon.setImageResource(R.drawable.false_icon);
                    }
                } else {
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });

        cardNumberValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cardNumberValue.removeTextChangedListener(this);
                if (cardNumberValue.getText().toString().length() <= 19) {
                    rawCardNumberValue = s.toString().replace("-", "");
                    rawCardNumberValueLength = rawCardNumberValue.length();
                    rawCardNumberValueLengthOffset = 0;
                    procCardNumberValue = "";
                    if (rawCardNumberValue.length() > 0) {
                        for (int i = 0; i < rawCardNumberValueLength; i++) {
                            if (Constants.CARD_NUMBER_FORMAT.charAt(i + rawCardNumberValueLengthOffset) == '-') {
                                procCardNumberValue += "-" + rawCardNumberValue.charAt(i);
                                rawCardNumberValueLengthOffset++;
                            } else {
                                procCardNumberValue += rawCardNumberValue.charAt(i);
                            }
                        }
                        procCardNumberValue = persianEnglishDigit.E2P(procCardNumberValue);
                        cardNumberValue.setText(procCardNumberValue);
                        cardNumberValue.setSelection(cardNumberValue.getText().toString().length());
                    }
                }
                cardNumberValue.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (cardNumberValue.getText().length() < 19){
                    cardProfile.setText("");
                    userNameFamily.setEnabled(true);
                }
            }
        });


        emailValue = (FacedEditText)findViewById(R.id.emailValue);
        emailIcon = (ImageView)findViewById(R.id.emailIcon);
        emailTextWatcher = new EmailTextWatcher(emailValue, emailIcon);

        emailValue.addTextChangedListener(emailTextWatcher);
//        emailValue.setText(new DeviceInfo(activity).getDeviceEmailAccount());

        keepOn_button = (FacedTextView) findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                keepOn_button.requestFocus();

                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                cellNumberValue.clearFocus();
                userNameFamily.clearFocus();
                nationalCodeValue.clearFocus();
                cardNumberValue.clearFocus();


                if (cellNumberIsValid && nationalCodeIsValid && verifiedCardNumber
                        && cellNumberValue.getText().toString().length() > 0
                        && nationalCodeValue.getText().toString().length() > 0
                        && cardNumberValue.getText().toString().length() > 0
                        && userNameFamily.getText().toString().length() > 0
                        && emailTextWatcher.isValid()) {

                    keepOn_button.setEnabled(false);

                    registrationEntryRequest = new RegistrationEntryRequest();

                    registrationEntryRequest.setCellNumber(persianEnglishDigit.P2E(getString(R.string.iran_prefix_cell_number) + cellNumberValue.getText().toString()));
                    registrationEntryRequest.setCardNumber(persianEnglishDigit.P2E(cardNumberValue.getText().toString()));
                    registrationEntryRequest.setFullName(userNameFamily.getText().toString());
                    registrationEntryRequest.setEmail(emailValue.getText().toString());
                    registrationEntryRequest.setNationalCode(persianEnglishDigit.P2E(nationalCodeValue.getText().toString().replaceAll("-", "")));

                    requestRegistrationEntry = new RequestRegistrationEntry(activity,
                            new RequestRegistrationEntryTaskCompleteListener(),
                            latitude + "," + longitude);

                    requestRegistrationEntry.execute(registrationEntryRequest);
                } else {

                    if (cellNumberValue.getText().toString().length() == 0 || !cellNumberIsValid){
                        Toast.makeText(context, getString(R.string.msg_cellNumber_invalid), Toast.LENGTH_SHORT).show();
                        cellNumberIcon.setImageResource(R.drawable.false_icon);
                        cellNumberValue.requestFocus();
                    }

                    else if (userNameFamily.getText().toString().length() <= 1 || !userNameFamilyIsValid){
                        Toast.makeText(context, getString(R.string.msg_username_invalid), Toast.LENGTH_SHORT).show();
                        userNameFamilyIcon.setImageResource(R.drawable.false_icon);
                        userNameFamily.requestFocus();
                    }

                    else if (cardNumberValue.getText().toString().length() == 0 || !verifiedCardNumber){
                        Toast.makeText(context, getString(R.string.msg_CardNo_invalid), Toast.LENGTH_SHORT).show();
                        cardNumberIcon.setVisibility(View.VISIBLE);
                        cardNumberIcon.setImageResource(R.drawable.false_icon);
                        cardNumberValue.requestFocus();
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
            }
        });
    }

    public class RequestCardProfileTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<CardProfileResponse>>
    {

        public RequestCardProfileTaskCompleteListener(){

        }


        @Override
        public void onTaskComplete(ResponseMessage<CardProfileResponse> cardProfileResponseMessage)
        {

            preloader.setVisibility(View.GONE);

            if (cardProfileResponseMessage != null) {

                cardProfileResponse = cardProfileResponseMessage.getService();

                if (cardProfileResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    verifiedCardNumber = true;

                    cardProfile.setText(cardProfileResponse.getBankName());
                    userNameFamily.setText(cardProfileResponse.getFullName());
                    userNameFamily.setEnabled(false);


                    if (cardProfileResponse.getBankLogo() != null) {
                        byte[] bytes;
                        bytes = Base64.decode(cardProfileResponse.getBankLogo(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        cardNumberIcon.setVisibility(View.VISIBLE);
                        cardNumberIcon.setImageBitmap(bitmap);
                    }

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
                    editor.putString(Constants.REGISTERED_CELL_NUMBER, persianEnglishDigit.P2E(getString(R.string.iran_prefix_cell_number) + cellNumberValue.getText().toString()));
                    editor.putString(Constants.REGISTERED_BANK_ID, selectedBankCode);
                    editor.putString(Constants.REGISTERED_USER_NAME, userNameFamily.getText().toString());
                    editor.putString(Constants.REGISTERED_CARD_NO, cardNumberValue.getText().toString());
                    editor.putString(Constants.REGISTERED_NATIONAL_CODE, persianEnglishDigit.P2E(nationalCodeValue.getText().toString().replaceAll("-", "")));
                    editor.putString(Constants.REGISTERED_USER_ID_TOKEN, registrationEntryResponse.getService().getUserIdToken());
                    editor.putString(Constants.REGISTERED_USER_EMAIL, emailValue.getText().toString());
                    editor.commit();


                    hamPayDialog.smsConfirmDialog(getString(R.string.iran_prefix_cell_number) + cellNumberValue.getText().toString(),
                            cardNumberValue.getText().toString());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Registration Entry")
                            .setAction("Entry")
                            .setLabel("Success")
                            .build());

                }
                else {
                    requestRegistrationEntry = new RequestRegistrationEntry(activity,
                            new RequestRegistrationEntryTaskCompleteListener(),
                            latitude + "," + longitude);
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
                        latitude + "," + longitude);
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
            hamPayDialog.showWaitingDialog("");
        }
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
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                }
            };

            if(mBestLocationProvider == null){
                mBestLocationProvider = new BestLocationProvider(this, true, true, 0, 0, 0, 0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).exitRegistrationDialog();
    }

}

