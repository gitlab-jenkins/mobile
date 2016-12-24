package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
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
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.security.KeyExchange;
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
    private FacedTextView tac_privacy_text;
    private CheckBox confirmTacPrivacy;
    private KeyExchange keyExchange;
    private String fullUserName = "";

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
                    if (grantResults.length > 0 && requestPermissions[0].equals(Manifest.permission.READ_PHONE_STATE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        registrationEntryRequest = new RegistrationEntryRequest();
                        registrationEntryRequest.setCellNumber(persianEnglishDigit.P2E(getString(R.string.iran_prefix_cell_number) + cellNumberValue.getText().toString()));
                        registrationEntryRequest.setCardNumber(persianEnglishDigit.P2E(cardNumberValue.getText().toString()));
                        registrationEntryRequest.setFullName(fullUserName);
                        registrationEntryRequest.setEmail(emailValue.getText().toString().trim());
                        registrationEntryRequest.setNationalCode(persianEnglishDigit.P2E(nationalCodeValue.getText().toString().replaceAll("-", "")));
                        requestRegistrationEntry = new RequestRegistrationEntry(activity, new RequestRegistrationEntryTaskCompleteListener());
                        requestRegistrationEntry.execute(registrationEntryRequest);
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE);
                            if (showRationale){
                                handler.post(new Runnable() {
                                    public void run() {
                                        PermissionDeviceDialog permissionDeviceDialog = new PermissionDeviceDialog();
                                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                        fragmentTransaction.add(permissionDeviceDialog, null);
                                        fragmentTransaction.commitAllowingStateLoss();
                                    }
                                });
                            }else {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + context.getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                context.startActivity(intent);
                            }
                        }else {
                            handler.post(new Runnable() {
                                public void run() {
                                    PermissionDeviceDialog permissionDeviceDialog = new PermissionDeviceDialog();
                                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.add(permissionDeviceDialog, null);
                                    fragmentTransaction.commitAllowingStateLoss();
                                }
                            });
                        }
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

        keyExchange = new KeyExchange(activity);
        confirmTacPrivacy = (CheckBox)findViewById(R.id.confirmTacPrivacy);
        confirmTacPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (keyExchange.getKey() == null || keyExchange.getIv() == null) {
                        confirmTacPrivacy.setChecked(false);
                        new KeyExchangeTask().execute();
                    }else {
                        keepOn_button.setBackgroundResource(R.drawable.registration_button_style);
                    }
                }else {
                    keepOn_button.setBackgroundResource(R.drawable.disable_button_style);
                }
            }
        });

        tac_privacy_text = (FacedTextView) findViewById(R.id.tac_privacy_text);
        Spannable tcPrivacySpannable = new SpannableString(activity.getString(R.string.start_tac_privacy));
        ClickableSpan tcClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent();
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/users/tac-file");
                intent.putExtra(Constants.TAC_PRIVACY_TITLE, activity.getString(R.string.tac_title_activity));
                activity.startActivity(intent);
            }
        };
        tcPrivacySpannable.setSpan(tcClickableSpan, 17, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tac_privacy_text.setText(tcPrivacySpannable);
        tac_privacy_text.setMovementMethod(LinkMovementMethod.getInstance());
        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent();
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/users/privacy-file");
                intent.putExtra(Constants.TAC_PRIVACY_TITLE, activity.getString(R.string.privacy_title_activity));
                activity.startActivity(intent);
            }
        };

        tcPrivacySpannable.setSpan(privacySpan, 48, 75, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tac_privacy_text.setText(tcPrivacySpannable);
        tac_privacy_text.setMovementMethod(LinkMovementMethod.getInstance());

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
                    if (new NationalCodeVerification(persianEnglishDigit.P2E(nationalCodeValue.getText().toString().replaceAll("-", ""))).isValidCode()) {
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
                fullUserName = userNameFamily.getText().toString().trim().replaceAll("^ +| +$|( )+", "$1");
                if (!hasFocus) {
                    userNameFamilyIsValid = true;
                    if (fullUserName.length() <= 1){
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

                fullUserName = userNameFamily.getText().toString().trim().replaceAll("^ +| +$|( )+", "$1");

                if (!confirmTacPrivacy.isChecked()){
                    Toast.makeText(activity, getString(R.string.force_tac), Toast.LENGTH_SHORT).show();
                    return;
                }
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
                        && fullUserName.length() >= 2
                        && emailTextWatcher.isValid()) {

                    requestAndLoadPhoneState();

                } else {

                    if (cellNumberValue.getText().toString().length() == 0 || !cellNumberIsValid){
                        Toast.makeText(context, getString(R.string.msg_cellNumber_invalid), Toast.LENGTH_SHORT).show();
                        cellNumberIcon.setImageResource(R.drawable.false_icon);
                        cellNumberValue.requestFocus();
                    }

                    else if (fullUserName.length() <= 1 || !userNameFamilyIsValid){
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
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

            hamPayDialog.dismisWaitingDialog();
            if (registrationEntryResponse != null) {

                if (registrationEntryResponse.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    editor.putString(Constants.REGISTERED_USER_NAME, fullUserName);
                    editor.putString(Constants.REGISTERED_USER_ID_TOKEN, registrationEntryResponse.getService().getUserIdToken());
                    editor.putString(Constants.REGISTERED_USER_EMAIL, emailValue.getText().toString().trim());
                    editor.commit();
                    hamPayDialog.smsConfirmDialog(getString(R.string.iran_prefix_cell_number) + cellNumberValue.getText().toString(),
                            cardNumberValue.getText().toString());
                    serviceName = ServiceEvent.REGISTRATION_ENTRY_SUCCESS;
                }
                else {
                    serviceName = ServiceEvent.REGISTRATION_ENTRY_FAILURE;
                    requestRegistrationEntry = new RequestRegistrationEntry(activity,
                            new RequestRegistrationEntryTaskCompleteListener());
                    new HamPayDialog(activity).showFailRegistrationEntryDialog(requestRegistrationEntry, registrationEntryRequest,
                            registrationEntryResponse.getService().getResultStatus().getCode(),
                            registrationEntryResponse.getService().getResultStatus().getDescription());
                }
            }else {
                serviceName = ServiceEvent.REGISTRATION_ENTRY_FAILURE;
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

    private class KeyExchangeTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                keyExchange.exchange();
            } catch (EncryptionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            hamPayDialog.dismisWaitingDialog();
            if (keyExchange.getKey() != null && keyExchange.getIv() != null) {
                keepOn_button.setBackgroundResource(R.drawable.registration_button_style);
                confirmTacPrivacy.setChecked(true);
            }else {
                keepOn_button.setBackgroundResource(R.drawable.disable_button_style);
                Toast.makeText(activity, getString(R.string.system_connectivity), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            hamPayDialog.showHamPayCommunication();
        }

    }


    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).exitRegistrationDialog();
    }

}

