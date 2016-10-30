package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
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
import xyz.homapay.hampay.mobile.android.dialog.permission.ActionPermission;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionDeviceDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceName;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.util.CardNumberValidator;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.NationalCodeVerification;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.net.InternetConnectionStatus;

public class ProfileEntryActivity extends AppCompatActivity implements PermissionDeviceDialog.PermissionDeviceDialogListener {

    private Activity activity;
    private PersianEnglishDigit persianEnglishDigit;
    private FacedTextView keepOn_button;
    private FacedEditText cellNumberValue;
    private ImageView cellNumberIcon;
    private boolean cellNumberIsValid = false;
    private Preloader preloader;
    private FacedEditText nationalCodeValue;
    private ImageView nationalCodeIcon;
    private boolean nationalCodeIsValid = false;
    private FacedEditText cardNumberValue;
    private FacedTextView cardProfile;
    private boolean verifiedCardNumber = false;
    private ImageView cardNumberIcon;
    private ImageView userNameFamilyIcon;
    private FacedEditText userNameFamily;
    private boolean userNameFamilyIsValid = true;
    private EmailTextWatcher emailTextWatcher;
    private FacedEditText emailValue;
    private ImageView emailIcon;
    private Context context;
    private String rawCardNumberValue = "";
    private int rawCardNumberValueLength = 0;
    private int rawCardNumberValueLengthOffset = 0;
    private String procCardNumberValue = "";
    private String rawNationalCode = "";
    private int rawNationalCodeLength = 0;
    private int rawNationalCodeLengthOffset = 0;
    private String procNationalCode = "";
    private CardProfileResponse cardProfileResponse;
    private SharedPreferences.Editor editor;
    private RegistrationEntryRequest registrationEntryRequest;
    private RequestRegistrationEntry requestRegistrationEntry;
    private RequestCardProfile requestCardProfile;
    private HamPayDialog hamPayDialog;
    private CardNumberValidator cardNumberValidator;
    private ArrayList<PermissionListener> permissionListeners = new ArrayList<>();
    private final Handler handler = new Handler();

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        for (PermissionListener permissionListener : permissionListeners)
            if (permissionListener.onResult(requestCode, permissions, grantResults)) {
                permissionListeners.remove(permissionListener);
            }
    }

    private void requestAndLoadPhoneState() {
        String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
        permissionListeners = new RequestPermissions().request(activity, Constants.READ_PHONE_STATE, permissions, new PermissionListener() {
            @Override
            public boolean onResult(int requestCode, String[] requestPermissions, int[] grantResults) {
                if (requestCode == Constants.READ_PHONE_STATE) {
                    if (requestPermissions[0].equals(Manifest.permission.READ_PHONE_STATE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        registrationEntryRequest = new RegistrationEntryRequest();
                        registrationEntryRequest.setCellNumber(persianEnglishDigit.P2E(getString(R.string.iran_prefix_cell_number) + cellNumberValue.getText().toString()));
                        registrationEntryRequest.setCardNumber(persianEnglishDigit.P2E(cardNumberValue.getText().toString()));
                        registrationEntryRequest.setFullName(userNameFamily.getText().toString().trim());
                        registrationEntryRequest.setEmail(emailValue.getText().toString().trim());
                        registrationEntryRequest.setNationalCode(persianEnglishDigit.P2E(nationalCodeValue.getText().toString().replaceAll("-", "")));
                        requestRegistrationEntry = new RequestRegistrationEntry(activity, new RequestRegistrationEntryTaskCompleteListener());
                        requestRegistrationEntry.execute(registrationEntryRequest);

                    } else {
                        handler.post(new Runnable() {
                            public void run() {
                                FragmentManager fm = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                fragmentTransaction.commit();
                                PermissionDeviceDialog permissionDeviceDialog = new PermissionDeviceDialog();
                                permissionDeviceDialog.show(fm, "fragment_edit_name");
                            }
                        });
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

        persianEnglishDigit = new PersianEnglishDigit();
        cardNumberValidator = new CardNumberValidator();
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
                    if (userNameFamily.getText().toString().trim().length() <= 1){
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
                        && cellNumberValue.getText().toString().trim().length() > 0
                        && nationalCodeValue.getText().toString().trim().length() > 0
                        && cardNumberValue.getText().toString().trim().length() > 0
                        && userNameFamily.getText().toString().trim().length() >= 2
                        && emailTextWatcher.isValid()) {

                    requestAndLoadPhoneState();

                } else {

                    if (cellNumberValue.getText().toString().length() == 0 || !cellNumberIsValid){
                        Toast.makeText(context, getString(R.string.msg_cellNumber_invalid), Toast.LENGTH_SHORT).show();
                        cellNumberIcon.setImageResource(R.drawable.false_icon);
                        cellNumberValue.requestFocus();
                    }

                    else if (userNameFamily.getText().toString().trim().length() <= 1 || !userNameFamilyIsValid){
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

    @Override
    public void onFinishEditDialog(ActionPermission actionPermission) {
        switch (actionPermission){
            case GRANT:
                requestAndLoadPhoneState();
                break;
            case DENY:
                finish();
                break;
        }
    }

    public class RequestRegistrationEntryTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationEntryResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<RegistrationEntryResponse> registrationEntryResponse)
        {
            ServiceName serviceName;
            LogEvent logEvent = new LogEvent(context);

            hamPayDialog.dismisWaitingDialog();
            if (registrationEntryResponse != null) {

                if (registrationEntryResponse.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    editor.putString(Constants.REGISTERED_USER_NAME, userNameFamily.getText().toString().trim());
                    editor.putString(Constants.REGISTERED_USER_ID_TOKEN, registrationEntryResponse.getService().getUserIdToken());
                    editor.putString(Constants.REGISTERED_USER_EMAIL, emailValue.getText().toString().trim());
                    editor.commit();
                    hamPayDialog.smsConfirmDialog(getString(R.string.iran_prefix_cell_number) + cellNumberValue.getText().toString(),
                            cardNumberValue.getText().toString());
                    serviceName = ServiceName.REGISTRATION_ENTRY_SUCCESS;
                }
                else {
                    serviceName = ServiceName.REGISTRATION_ENTRY_FAILURE;
                    requestRegistrationEntry = new RequestRegistrationEntry(activity,
                            new RequestRegistrationEntryTaskCompleteListener());
                    new HamPayDialog(activity).showFailRegistrationEntryDialog(requestRegistrationEntry, registrationEntryRequest,
                            registrationEntryResponse.getService().getResultStatus().getCode(),
                            registrationEntryResponse.getService().getResultStatus().getDescription());
                }
            }else {
                serviceName = ServiceName.REGISTRATION_ENTRY_FAILURE;
                if (requestRegistrationEntry.internetConnectionStatus == InternetConnectionStatus.DISCONNECT){
                    requestRegistrationEntry = new RequestRegistrationEntry(activity, new RequestRegistrationEntryTaskCompleteListener());
                    new HamPayDialog(activity).showFailRegistrationEntryDialog(requestRegistrationEntry, registrationEntryRequest,
                            Constants.LOCAL_ERROR_CODE,
                            getString(R.string.msg_check_network_connectivity));
                }else {
                    requestRegistrationEntry = new RequestRegistrationEntry(activity, new RequestRegistrationEntryTaskCompleteListener());
                    new HamPayDialog(activity).showFailRegistrationEntryDialog(requestRegistrationEntry, registrationEntryRequest,
                            Constants.LOCAL_ERROR_CODE,
                            getString(R.string.msg_fail_registration_entry));
                }
            }
            logEvent.log(serviceName);
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog("");
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).exitRegistrationDialog();
    }

}

