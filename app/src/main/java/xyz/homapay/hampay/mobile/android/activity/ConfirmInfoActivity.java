package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.RegistrationConfirmUserDataRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationFetchUserDataRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyAccountRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationConfirmUserDataResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationFetchUserDataResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyAccountResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestConfirmUserData;
import xyz.homapay.hampay.mobile.android.async.RequestFetchUserData;
import xyz.homapay.hampay.mobile.android.async.RequestRegisterVerifyAccount;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.material.ButtonFlat;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.component.material.CheckBox;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.NetworkConnectivity;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class ConfirmInfoActivity extends Activity implements View.OnClickListener {

    Activity activity;

    ButtonRectangle correct_button;
    RelativeLayout correct_button_rl;
    ButtonRectangle keeOn_without_button;
    ButtonRectangle keeOn_with_button;

    Dialog confirm_info_dialog;
    LinearLayout confirm_check_ll;
    boolean confirm_check_value = true;
    CheckBox confirm_check;
    FacedEditText cellNumberValue;
    FacedEditText userFamilyValue;
    FacedEditText accountNumberValue;
    boolean accountNumberIsValid = true;
    String accountNumberFormat;
    String rawAccountNumberValue = "";
    int rawAccountNumberValueLength = 0;
    int rawAccountNumberValueLengthOffset = 0;
    String procAccountNumberValue = "";


    FacedEditText nationalCodeValue;
    ImageView userFamilyIcon;
    boolean userFamilyIsValid = true;
    ImageView accountNumberIcon;
    ImageView nationalCodeIcon;
    boolean nationalCodeIsValid = true;
    LinearLayout confirm_layout;

    Context context;

    HamPayDialog hamPayDialog;


    LinearLayout correct_info;

    private boolean oneConfirmUserData = false;

    Tracker hamPayGaTracker;

    public void contactUs(View view){
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/continue.html");
    }

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    NetworkConnectivity networkConnectivity;


    RequestFetchUserData requestFetchUserData;
    RegistrationFetchUserDataRequest registrationFetchUserDataRequest;

    RequestRegisterVerifyAccount requestRegisterVerifyAccount;
    RegistrationVerifyAccountRequest registrationVerifyAccountRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_info);

        activity = ConfirmInfoActivity.this;

        correct_info = (LinearLayout)findViewById(R.id.correct_info);

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, ConfirmInfoActivity.class.getName());
        editor.commit();

        accountNumberFormat = prefs.getString(Constants.REGISTERED_BANK_ACCOUNT_NO_FORMAT, "");

        context = this;

        networkConnectivity = new NetworkConnectivity(context);

        keeOn_without_button = (ButtonRectangle)findViewById(R.id.keeOn_without_button);
        keeOn_without_button.setOnClickListener(this);

        keeOn_with_button = (ButtonRectangle)findViewById(R.id.keeOn_with_button);
        keeOn_with_button.setOnClickListener(this);
        confirm_layout = (LinearLayout)findViewById(R.id.confirm_layout);
        confirm_check_ll = (LinearLayout)findViewById(R.id.confirm_check_ll);
        confirm_check = (CheckBox)findViewById(R.id.confirm_check);
        confirm_check.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(CheckBox view, boolean check) {

                confirm_check.setChecked(false);
                if (!oneConfirmUserData)
                    confirmUserData();
                else
                    confirm_check.setChecked(check);

            }
        });
        confirm_check_ll.setOnClickListener(this);

        cellNumberValue = (FacedEditText)findViewById(R.id.cellNumberValue);
        cellNumberValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cellNumberValue.removeTextChangedListener(this);
                cellNumberValue.setText(new PersianEnglishDigit(s.toString()).E2P());
                cellNumberValue.setSelection(s.toString().length());
                cellNumberValue.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        userFamilyValue = (FacedEditText)findViewById(R.id.userFamilyValue);
        userFamilyIcon = (ImageView)findViewById(R.id.userFamilyIcon);
//        userFamilyValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//
//                if (!hasFocus){
//                    if (userFamilyValue.getText().toString().length() > 0){
//                        userFamilyIcon.setImageResource(R.drawable.right_icon);
//                        userFamilyIsValid = true;
//                    }
//                    else {
//                        userFamilyIcon.setImageResource(R.drawable.false_icon);
//                        userFamilyIsValid = false;
//                    }
//                }
//            }
//        });


        accountNumberValue = (FacedEditText)findViewById(R.id.accountNumberValue);
        accountNumberValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(accountNumberFormat.length())});
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
//        nationalCodeValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus){
//
//                    if (new NationalCodeVerification(nationalCodeValue.getText().toString()).isValidCode()){
//                        nationalCodeIcon.setImageResource(R.drawable.right_icon);
//                        nationalCodeIsValid = true;
//                    }
//                    else {
//                        nationalCodeIcon.setImageResource(R.drawable.false_icon);
//                        nationalCodeIsValid = false;
//                    }
//
//                }
//            }
//        });


        registrationFetchUserDataRequest = new RegistrationFetchUserDataRequest();
        registrationFetchUserDataRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
        correct_button = (ButtonRectangle)findViewById(R.id.correct_button);
        correct_button_rl = (RelativeLayout)findViewById(R.id.correct_button_rl);
        correct_button.setOnClickListener(this);

        requestFetchUserData = new RequestFetchUserData(context, new RequestFetchUserDataTaskCompleteListener());
        requestFetchUserData.execute(registrationFetchUserDataRequest);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.keeOn_without_button:

                Rect displayRectangle = new Rect();
                Activity parent = (Activity) ConfirmInfoActivity.this;
                Window window = parent.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                View view = getLayoutInflater().inflate(R.layout.dialog_confirm_info, null);

                ButtonFlat check_account = (ButtonFlat) view.findViewById(R.id.check_account);
                ButtonFlat uncheck_account = (ButtonFlat) view.findViewById(R.id.uncheck_account);

                check_account.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        editor.putString(Constants.REGISTERED_CELL_NUMBER, new PersianEnglishDigit(cellNumberValue.getText().toString()).P2E());
                        editor.putString(Constants.REGISTERED_USER_FAMILY, new PersianEnglishDigit(userFamilyValue.getText().toString()).P2E());
                        editor.putString(Constants.REGISTERED_USER_NAME, userFamilyValue.getText().toString().split(" ")[0]);
                        editor.putString(Constants.REGISTERED_ACCOUNT_NO, new PersianEnglishDigit(accountNumberValue.getText().toString()).P2E());
//                        editor.putString(Constants.REGISTERED_NATIONAL_CODE, nationalCodeValue.getText().toString());
                        editor.commit();

                        confirm_info_dialog.dismiss();

                        registrationVerifyAccountRequest = new RegistrationVerifyAccountRequest();
                        registrationVerifyAccountRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));

                        requestRegisterVerifyAccount = new RequestRegisterVerifyAccount(context, new RequestRegistrationVerifyAccountResponseTaskCompleteListener());
                        requestRegisterVerifyAccount.execute(registrationVerifyAccountRequest);

                    }
                });


                uncheck_account.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, ConfirmInfoActivity.class.getName());
                        editor.putString(Constants.REGISTERED_CELL_NUMBER, new PersianEnglishDigit(cellNumberValue.getText().toString()).P2E());
                        editor.putString(Constants.REGISTERED_USER_FAMILY, userFamilyValue.getText().toString());
                        editor.putString(Constants.REGISTERED_USER_NAME, userFamilyValue.getText().toString().split(" ")[0]);
                        editor.putString(Constants.REGISTERED_ACCOUNT_NO, accountNumberValue.getText().toString());
//                        editor.putString(Constants.REGISTERED_NATIONAL_CODE, nationalCodeValue.getText().toString());
                        editor.commit();

                        confirm_info_dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setClass(ConfirmInfoActivity.this, PasswordEntryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }
                });

                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
                confirm_info_dialog = new Dialog(ConfirmInfoActivity.this);
                confirm_info_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                confirm_info_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirm_info_dialog.setContentView(view);
                confirm_info_dialog.setTitle(null);
                confirm_info_dialog.setCanceledOnTouchOutside(false);

                confirm_info_dialog.show();

                break;

            case R.id.keeOn_with_button:


                editor.putString(Constants.REGISTERED_ACTIVITY_DATA, ConfirmInfoActivity.class.getName());
                editor.putString(Constants.REGISTERED_CELL_NUMBER, new PersianEnglishDigit(cellNumberValue.getText().toString()).P2E());
                editor.putString(Constants.REGISTERED_USER_FAMILY, userFamilyValue.getText().toString());
                editor.putString(Constants.REGISTERED_USER_NAME, userFamilyValue.getText().toString().split(" ")[0]);
                editor.putString(Constants.REGISTERED_ACCOUNT_NO, accountNumberValue.getText().toString());
//                editor.putString(Constants.REGISTERED_NATIONAL_CODE, nationalCodeValue.getText().toString());
                editor.commit();

                registrationVerifyAccountRequest = new RegistrationVerifyAccountRequest();
                registrationVerifyAccountRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));

                requestRegisterVerifyAccount = new RequestRegisterVerifyAccount(context, new RequestRegistrationVerifyAccountResponseTaskCompleteListener());
                requestRegisterVerifyAccount.execute(registrationVerifyAccountRequest);
                break;

            case R.id.correct_button:
//                correct_button.setBackgroundColor(getResources().getColor(R.color.normal_text));
//                user_phone.setFocusableInTouchMode(true);
//                userFamilyValue.setFocusableInTouchMode(true);
//                accountNumberValue.setFocusableInTouchMode(true);
//                nationalCodeValue.setFocusableInTouchMode(true);

                new HamPayDialog(activity).showBackStartRegisterDialog();


                break;

            case R.id.confirm_check_ll:

                if (!oneConfirmUserData)
                    confirmUserData();
                else
                    confirm_check.setChecked(!confirm_check.isCheck());

                break;
        }
    }



    RegistrationConfirmUserDataRequest registrationConfirmUserDataRequest;
    RequestConfirmUserData requestConfirmUserData;

    private void confirmUserData(){

//                userFamilyValue.clearFocus();
//                nationalCodeValue.clearFocus();
        accountNumberValue.clearFocus();


        if (confirm_check_value) {

            if (/*userFamilyIsValid &&*/ /*nationalCodeIsValid &&*/ accountNumberIsValid
                               /* && userFamilyValue.getText().toString().length() > 0*/
                                /*&& nationalCodeValue.getText().toString().length() > 0*/
                    && accountNumberValue.getText().toString().length() > 0) {

                registrationConfirmUserDataRequest = new RegistrationConfirmUserDataRequest();
                registrationConfirmUserDataRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                registrationConfirmUserDataRequest.setImei(new DeviceInfo(getApplicationContext()).getIMEI());
                registrationConfirmUserDataRequest.setIsVerified(confirm_check_value);
                registrationConfirmUserDataRequest.setIp(new DeviceInfo(context).getNetworkIp());
                registrationConfirmUserDataRequest.setDeviceId(new DeviceInfo(getApplicationContext()).getAndroidId());


                requestConfirmUserData = new RequestConfirmUserData(context, new RequestConfirmUserDataTaskCompleteListener());
                requestConfirmUserData.execute(registrationConfirmUserDataRequest);

                confirm_check_value = !confirm_check_value;

            }else {

                if (userFamilyValue.getText().toString().length() == 0 || !userFamilyIsValid){
                    Toast.makeText(context, getString(R.string.msg_family_invalid), Toast.LENGTH_SHORT).show();
                    userFamilyIcon.setImageResource(R.drawable.false_icon);
                    userFamilyValue.requestFocus();
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
        } else {
            confirm_check.setChecked(false);
            correct_button.setBackgroundColor(getResources().getColor(R.color.register_btn_color));
            correct_button.setVisibility(View.VISIBLE);
            correct_button_rl.setVisibility(View.VISIBLE);
            confirm_layout.setVisibility(View.GONE);
            confirm_check_value = !confirm_check_value;
        }


        confirm_check_ll.requestFocus();
    }


    public class RequestRegistrationVerifyAccountResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyAccountResponse>>
    {
        public RequestRegistrationVerifyAccountResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationVerifyAccountResponse> verifyAccountResponseMessage)
        {
            hamPayDialog.dismisWaitingDialog();
            if (verifyAccountResponseMessage != null) {

                if (verifyAccountResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    Intent intent = new Intent();
                    intent.setClass(ConfirmInfoActivity.this, RegVerifyAccountNoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(Constants.TRANSFER_MONEY_COMMENT, verifyAccountResponseMessage.getService().getTransferMoneyComment());
                    finish();
                    startActivity(intent);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Registration Verify Account")
                            .setAction("Verify")
                            .setLabel("Success")
                            .build());

                }else if (verifyAccountResponseMessage.getService().getResultStatus() == ResultStatus.REGISTRATION_INVALID_STEP){
                    new HamPayDialog(activity).showInvalidStepDialog();
                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Registration Verify Account")
                            .setAction("Verify")
                            .setLabel("Success(Invalid)")
                            .build());
                }
                else{
                    requestRegisterVerifyAccount = new RequestRegisterVerifyAccount(context, new RequestRegistrationVerifyAccountResponseTaskCompleteListener());
                    new HamPayDialog(activity).showFailRegisterVerifyAccountDialog(requestRegisterVerifyAccount, registrationVerifyAccountRequest,
                            verifyAccountResponseMessage.getService().getResultStatus().getCode(),
                            verifyAccountResponseMessage.getService().getResultStatus().getDescription());
                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Registration Verify Account")
                            .setAction("Verify")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }
            else {
                requestRegisterVerifyAccount = new RequestRegisterVerifyAccount(context, new RequestRegistrationVerifyAccountResponseTaskCompleteListener());
                new HamPayDialog(activity).showFailRegisterVerifyAccountDialog(requestRegisterVerifyAccount, registrationVerifyAccountRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_verify_account));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Registration Verify Account")
                        .setAction("Verify")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog("");
        }
    }

    public class RequestFetchUserDataTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationFetchUserDataResponse>>
    {
        public RequestFetchUserDataTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationFetchUserDataResponse> registrationFetchUserDataResponseMessage)
        {
            hamPayDialog.dismisWaitingDialog();

            if (registrationFetchUserDataResponseMessage != null) {
                if (registrationFetchUserDataResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    correct_info.setVisibility(View.VISIBLE);
                    cellNumberValue.setText(registrationFetchUserDataResponseMessage.getService().getCellNumber());
                    userFamilyValue.setText(registrationFetchUserDataResponseMessage.getService().getFulName());
                    accountNumberValue.setText(registrationFetchUserDataResponseMessage.getService().getAccountNumber());
                    nationalCodeValue.setText(registrationFetchUserDataResponseMessage.getService().getNationalCode());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Fetch User Data")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());
                }else if (registrationFetchUserDataResponseMessage.getService().getResultStatus() == ResultStatus.REGISTRATION_INVALID_STEP){
                    new HamPayDialog(activity).showInvalidStepDialog();

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Fetch User Data")
                            .setAction("Fetch")
                            .setLabel("Success(Invalid)")
                            .build());
                }
                else {
                    requestFetchUserData = new RequestFetchUserData(context, new RequestFetchUserDataTaskCompleteListener());
                    registrationFetchUserDataRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                    new HamPayDialog(activity).showFailFetchUserDataDialog(requestFetchUserData, registrationFetchUserDataRequest,
                            registrationFetchUserDataResponseMessage.getService().getResultStatus().getCode(),
                            registrationFetchUserDataResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Fetch User Data")
                            .setAction("Fetch")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestFetchUserData = new RequestFetchUserData(context, new RequestFetchUserDataTaskCompleteListener());
                registrationFetchUserDataRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                new HamPayDialog(activity).showFailFetchUserDataDialog(requestFetchUserData, registrationFetchUserDataRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_registration_fetch_user_data));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Fetch User Data")
                        .setAction("Fetch")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

        }

        @Override
        public void onTaskPreRun() {
//            loading_rl.setVisibility(View.VISIBLE);
            hamPayDialog.showWaitingdDialog("");
        }
    }




    public class RequestConfirmUserDataTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationConfirmUserDataResponse>>
    {

        public RequestConfirmUserDataTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationConfirmUserDataResponse> registrationConfirmUserDataResponseMessage)
        {
            hamPayDialog.dismisWaitingDialog();

            if (registrationConfirmUserDataResponseMessage != null) {

                if (registrationConfirmUserDataResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    oneConfirmUserData = true;
                    confirm_check.setChecked(true);
                    correct_button.setVisibility(View.GONE);
                    correct_button_rl.setVisibility(View.GONE);
                    accountNumberValue.setFocusable(false);
                    confirm_layout.setVisibility(View.VISIBLE);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Confirm User Data")
                            .setAction("Confirm")
                            .setLabel("Success")
                            .build());
                }else if (registrationConfirmUserDataResponseMessage.getService().getResultStatus() == ResultStatus.REGISTRATION_INVALID_STEP){
                    new HamPayDialog(activity).showInvalidStepDialog();

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Confirm User Data")
                            .setAction("Confirm")
                            .setLabel("Success(Invalid)")
                            .build());
                }
                else {
                    requestConfirmUserData = new RequestConfirmUserData(context, new RequestConfirmUserDataTaskCompleteListener());
                    new HamPayDialog(activity).showFailConfirmUserDataDialog(requestConfirmUserData, registrationConfirmUserDataRequest,
                            registrationConfirmUserDataResponseMessage.getService().getResultStatus().getCode(),
                            registrationConfirmUserDataResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Confirm User Data")
                            .setAction("Confirm")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestConfirmUserData = new RequestConfirmUserData(context, new RequestConfirmUserDataTaskCompleteListener());
                new HamPayDialog(activity).showFailConfirmUserDataDialog(requestConfirmUserData, registrationConfirmUserDataRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_registration_confirm_user_data));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Confirm User Data")
                        .setAction("Confirm")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog("");
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }

}
