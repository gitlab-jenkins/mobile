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
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationConfirmUserDataRequest;
import com.hampay.common.core.model.request.RegistrationFetchUserDataRequest;
import com.hampay.common.core.model.request.RegistrationVerifyAccountRequest;
import com.hampay.common.core.model.response.RegistrationConfirmUserDataResponse;
import com.hampay.common.core.model.response.RegistrationFetchUserDataResponse;
import com.hampay.common.core.model.response.RegistrationVerifyAccountResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestConfirmUserData;
import com.hampay.mobile.android.async.RequestFetchUserData;
import com.hampay.mobile.android.async.RequestRegisterVerifyAccount;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.component.material.ButtonFlat;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.component.material.CheckBox;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constant;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.DeviceInfo;
import com.hampay.mobile.android.util.NationalCodeVerification;
import com.hampay.mobile.android.util.NetworkConnectivity;

public class ConfirmInfoActivity extends ActionBarActivity implements View.OnClickListener {

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

    RelativeLayout loading_rl;

    private ResponseMessage<RegistrationConfirmUserDataResponse> registrationConfirmUserDataResponse;

    public void contactUs(View view){
        new HamPayDialog(this).showContactUsDialog();
    }

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    NetworkConnectivity networkConnectivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_info);

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

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
//        confirm_check.setEnabled(false);
//        confirm_check.setOncheckListener(new CheckBox.OnCheckListener() {
//            @Override
//            public void onCheck(CheckBox view, boolean check) {
//
//                if (check) {
//
//                    RegistrationConfirmUserDataRequest registrationConfirmUserDataRequest = new RegistrationConfirmUserDataRequest();
//                    registrationConfirmUserDataRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
//                    registrationConfirmUserDataRequest.setImei(new DeviceInfo(getApplicationContext()).getIMEI());
//                    registrationConfirmUserDataRequest.setIsVerified(confirm_check_value);
//                    registrationConfirmUserDataRequest.setIp("192.168.1.1");
//                    registrationConfirmUserDataRequest.setDeviceId(new DeviceInfo(getApplicationContext()).getDeviceId());
//
//                    new RequestConfirmUserData(context, new RequestConfirmUserDataTaskCompleteListener()).execute(registrationConfirmUserDataRequest);
//
//                } else {
////                    confirm_check_img.setImageDrawable(null);
//                    confirm_check.setChecked(false);
//                    correct_button.setBackgroundColor(getResources().getColor(R.color.register_btn_color));
//                    correct_button.setVisibility(View.VISIBLE);
//                    correct_button_rl.setVisibility(View.VISIBLE);
//                    confirm_layout.setVisibility(View.GONE);
//                }
//            }
//        });
        confirm_check_ll.setOnClickListener(this);

        cellNumberValue = (FacedEditText)findViewById(R.id.cellNumberValue);
        userFamilyValue = (FacedEditText)findViewById(R.id.userFamilyValue);
        userFamilyIcon = (ImageView)findViewById(R.id.userFamilyIcon);
        userFamilyValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus){
                    if (userFamilyValue.getText().toString().length() > 0){
                        userFamilyIcon.setImageResource(R.drawable.right_icon);
                        userFamilyIsValid = true;
                    }
                    else {
                        userFamilyIcon.setImageResource(R.drawable.false_icon);
                        userFamilyIsValid = false;
                    }
                }
            }
        });


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


        RegistrationFetchUserDataRequest registrationFetchUserDataRequest = new RegistrationFetchUserDataRequest();
        registrationFetchUserDataRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
        correct_button = (ButtonRectangle)findViewById(R.id.correct_button);
        correct_button_rl = (RelativeLayout)findViewById(R.id.correct_button_rl);
        correct_button.setOnClickListener(this);

        new RequestFetchUserData(context, new RequestFetchUserDataTaskCompleteListener()).execute(registrationFetchUserDataRequest);

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


                        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, ConfirmInfoActivity.class.toString());
                        editor.putString(Constants.REGISTERED_CELL_NUMBER, cellNumberValue.getText().toString());
                        editor.putString(Constants.REGISTERED_USER_FAMILY, userFamilyValue.getText().toString());
                        editor.putString(Constants.REGISTERED_ACCOUNT_NO, accountNumberValue.getText().toString());
                        editor.putString(Constants.REGISTERED_NATIONAL_CODE, nationalCodeValue.getText().toString());

                        confirm_info_dialog.dismiss();

                        RegistrationVerifyAccountRequest registrationVerifyAccountRequest = new RegistrationVerifyAccountRequest();
                        registrationVerifyAccountRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));

                        new RequestRegisterVerifyAccount(context, new RequestRegistrationVerifyAccountResponseTaskCompleteListener()).execute(registrationVerifyAccountRequest);

                    }
                });


                uncheck_account.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, ConfirmInfoActivity.class.toString());
                        editor.putString(Constants.REGISTERED_CELL_NUMBER, cellNumberValue.getText().toString());
                        editor.putString(Constants.REGISTERED_USER_FAMILY, userFamilyValue.getText().toString());
                        editor.putString(Constants.REGISTERED_ACCOUNT_NO, accountNumberValue.getText().toString());
                        editor.putString(Constants.REGISTERED_NATIONAL_CODE, nationalCodeValue.getText().toString());
                        editor.commit();

                        confirm_info_dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setClass(ConfirmInfoActivity.this, PasswordEntryActivity.class);
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

                RegistrationVerifyAccountRequest registrationVerifyAccountRequest = new RegistrationVerifyAccountRequest();
                registrationVerifyAccountRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));

                new RequestRegisterVerifyAccount(this, new RequestRegistrationVerifyAccountResponseTaskCompleteListener()).execute(registrationVerifyAccountRequest);

                break;

            case R.id.correct_button:
                correct_button.setBackgroundColor(getResources().getColor(R.color.normal_text));
//                user_phone.setFocusableInTouchMode(true);
                userFamilyValue.setFocusableInTouchMode(true);
                accountNumberValue.setFocusableInTouchMode(true);
                nationalCodeValue.setFocusableInTouchMode(true);

                break;

            case R.id.confirm_check_ll:


                userFamilyValue.clearFocus();
                nationalCodeValue.clearFocus();
                accountNumberValue.clearFocus();



                if (networkConnectivity.isNetworkConnected()) {



                    if (confirm_check_value) {


                        if (userFamilyIsValid && nationalCodeIsValid && accountNumberIsValid
                                && userFamilyValue.getText().toString().length() > 0
                                && nationalCodeValue.getText().toString().length() > 0
                                && accountNumberValue.getText().toString().length() > 0) {



                            RegistrationConfirmUserDataRequest registrationConfirmUserDataRequest = new RegistrationConfirmUserDataRequest();
                            registrationConfirmUserDataRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                            registrationConfirmUserDataRequest.setImei(new DeviceInfo(getApplicationContext()).getIMEI());
                            registrationConfirmUserDataRequest.setIsVerified(confirm_check_value);
                            registrationConfirmUserDataRequest.setIp("192.168.1.1");
                            registrationConfirmUserDataRequest.setDeviceId(new DeviceInfo(getApplicationContext()).getDeviceId());

                            new RequestConfirmUserData(context, new RequestConfirmUserDataTaskCompleteListener()).execute(registrationConfirmUserDataRequest);


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
                }else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }

                confirm_check_ll.requestFocus();

                break;
        }
    }


    public class RequestRegistrationVerifyAccountResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyAccountResponse>>
    {
        public RequestRegistrationVerifyAccountResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationVerifyAccountResponse> verifyAccountResponseMessage)
        {
            loading_rl.setVisibility(View.GONE);
            if (verifyAccountResponseMessage.getService().getResultStatus() != null) {

                if (verifyAccountResponseMessage.getService().getTransferMoneyComment().length() > 0 ) {
                    Intent intent = new Intent();
                    intent.setClass(ConfirmInfoActivity.this, RegVerifyAccountNoActivity.class);
                    intent.putExtra(Constants.TRANSFER_MONEY_COMMENT, verifyAccountResponseMessage.getService().getTransferMoneyComment());
                    startActivity(intent);
                }
            }
            else {
                Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onTaskPreRun() {
            loading_rl.setVisibility(View.VISIBLE);
        }
    }

    public class RequestFetchUserDataTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationFetchUserDataResponse>>
    {
        public RequestFetchUserDataTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationFetchUserDataResponse> registrationFetchUserDataResponseMessage)
        {
            loading_rl.setVisibility(View.GONE);
            if (registrationFetchUserDataResponseMessage.getService().getResultStatus() != null) {
                cellNumberValue.setText(registrationFetchUserDataResponseMessage.getService().getCellNumber());
                userFamilyValue.setText(registrationFetchUserDataResponseMessage.getService().getFulName());
                accountNumberValue.setText(registrationFetchUserDataResponseMessage.getService().getAccountNumber());
                nationalCodeValue.setText(registrationFetchUserDataResponseMessage.getService().getNationalCode());
            }

        }

        @Override
        public void onTaskPreRun() {
            loading_rl.setVisibility(View.VISIBLE);
        }
    }



    public class RequestConfirmUserDataTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationConfirmUserDataResponse>>
    {

        public RequestConfirmUserDataTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationConfirmUserDataResponse> registrationConfirmUserDataResponseMessage)
        {
            loading_rl.setVisibility(View.GONE);

            registrationConfirmUserDataResponse = registrationConfirmUserDataResponseMessage;
            if (registrationConfirmUserDataResponseMessage.getService().getResultStatus() != null) {
//                confirm_check_img.setImageResource(R.drawable.tick_icon);
                confirm_check.setChecked(true);
                correct_button.setVisibility(View.GONE);
                correct_button_rl.setVisibility(View.GONE);
//                user_phone.setFocusable(false);
                userFamilyValue.setFocusable(false);
                accountNumberValue.setFocusable(false);
                nationalCodeValue.setFocusable(false);
                confirm_layout.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onTaskPreRun() {
            loading_rl.setVisibility(View.VISIBLE);
        }
    }

}
