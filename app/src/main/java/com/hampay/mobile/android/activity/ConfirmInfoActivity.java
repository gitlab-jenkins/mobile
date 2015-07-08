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
import com.hampay.mobile.android.util.DeviceInfo;

public class ConfirmInfoActivity extends ActionBarActivity implements View.OnClickListener {

    ButtonRectangle correct_button;
    RelativeLayout correct_button_rl;
    ButtonRectangle keeOn_without_button;
    ButtonRectangle keeOn_with_button;

    Dialog confirm_info_dialog;
    LinearLayout confirm_check_ll;
    boolean confirm_check_value = false;
    CheckBox confirm_check;
    FacedEditText user_phone;
    FacedEditText user_family;
    FacedEditText user_account_no;
    FacedEditText user_national_code;
    LinearLayout confirm_layout;

    SharedPreferences prefs;

    Context context;

    RelativeLayout loading_rl;

    private ResponseMessage<RegistrationConfirmUserDataResponse> registrationConfirmUserDataResponse;

    public void contactUs(View view){
        new HamPayDialog(this).showContactUsDialog();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_info);

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        prefs = getPreferences(MODE_PRIVATE);

        context = this;

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
//                confirm_check_value = !confirm_check_value;

                if (check) {

                    RegistrationConfirmUserDataRequest registrationConfirmUserDataRequest = new RegistrationConfirmUserDataRequest();
                    registrationConfirmUserDataRequest.setUserIdToken(prefs.getString("UserIdToken", ""));
                    registrationConfirmUserDataRequest.setImei(new DeviceInfo(getApplicationContext()).getIMEI());
                    registrationConfirmUserDataRequest.setIsVerified(confirm_check_value);
                    registrationConfirmUserDataRequest.setIp("192.168.1.1");
                    registrationConfirmUserDataRequest.setDeviceId(new DeviceInfo(getApplicationContext()).getDeviceId());

                    new RequestConfirmUserData(context, new RequestConfirmUserDataTaskCompleteListener()).execute(registrationConfirmUserDataRequest);

                } else {
//                    confirm_check_img.setImageDrawable(null);
                    confirm_check.setChecked(false);
                    correct_button.setBackgroundColor(getResources().getColor(R.color.register_btn_color));
                    correct_button.setVisibility(View.VISIBLE);
                    correct_button_rl.setVisibility(View.VISIBLE);
                    confirm_layout.setVisibility(View.GONE);
                }
            }
        });
        confirm_check_ll.setOnClickListener(this);

        user_phone = (FacedEditText)findViewById(R.id.user_phone);
        user_family = (FacedEditText)findViewById(R.id.user_family);
        user_account_no = (FacedEditText)findViewById(R.id.user_account_no);
        user_national_code = (FacedEditText)findViewById(R.id.user_national_code);


        RegistrationFetchUserDataRequest registrationFetchUserDataRequest = new RegistrationFetchUserDataRequest();
        registrationFetchUserDataRequest.setUserIdToken(prefs.getString("UserIdToken", ""));
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
                        confirm_info_dialog.dismiss();

                        RegistrationVerifyAccountRequest registrationVerifyAccountRequest = new RegistrationVerifyAccountRequest();
                        registrationVerifyAccountRequest.setUserIdToken(prefs.getString("UserIdToken", ""));

                        new RequestRegisterVerifyAccount(context, new RequestRegistrationVerifyAccountResponseTaskCompleteListener()).execute(registrationVerifyAccountRequest);

                    }
                });


                uncheck_account.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                registrationVerifyAccountRequest.setUserIdToken(prefs.getString("UserIdToken", ""));

                new RequestRegisterVerifyAccount(this, new RequestRegistrationVerifyAccountResponseTaskCompleteListener()).execute(registrationVerifyAccountRequest);

                break;

            case R.id.correct_button:
                correct_button.setBackgroundColor(getResources().getColor(R.color.normal_text));
//                user_phone.setFocusableInTouchMode(true);
                user_family.setFocusableInTouchMode(true);
                user_account_no.setFocusableInTouchMode(true);
                user_national_code.setFocusableInTouchMode(true);

                break;

            case R.id.confirm_check_ll:
                confirm_check_value = !confirm_check_value;

                if (confirm_check_value) {

                    RegistrationConfirmUserDataRequest registrationConfirmUserDataRequest = new RegistrationConfirmUserDataRequest();
                    registrationConfirmUserDataRequest.setUserIdToken(prefs.getString("UserIdToken", ""));
                    registrationConfirmUserDataRequest.setImei(new DeviceInfo(getApplicationContext()).getIMEI());
                    registrationConfirmUserDataRequest.setIsVerified(confirm_check_value);
                    registrationConfirmUserDataRequest.setIp("192.168.1.1");
                    registrationConfirmUserDataRequest.setDeviceId(new DeviceInfo(getApplicationContext()).getDeviceId());

                    new RequestConfirmUserData(context, new RequestConfirmUserDataTaskCompleteListener()).execute(registrationConfirmUserDataRequest);

                }
                else {
//                    confirm_check_img.setImageDrawable(null);
                    confirm_check.setChecked(false);
                    correct_button.setBackgroundColor(getResources().getColor(R.color.register_btn_color));
                    correct_button.setVisibility(View.VISIBLE);
                    correct_button_rl.setVisibility(View.VISIBLE);
                    confirm_layout.setVisibility(View.GONE);
                }
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
                    intent.putExtra("TransferMoneyComment", verifyAccountResponseMessage.getService().getTransferMoneyComment());
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
                user_phone.setText(registrationFetchUserDataResponseMessage.getService().getCellNumber());
                user_family.setText(registrationFetchUserDataResponseMessage.getService().getFulName());
                user_account_no.setText(registrationFetchUserDataResponseMessage.getService().getAccountNumber());
                user_national_code.setText(registrationFetchUserDataResponseMessage.getService().getNationalCode());
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
                user_family.setFocusable(false);
                user_account_no.setFocusable(false);
                user_national_code.setFocusable(false);
                confirm_layout.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onTaskPreRun() {
            loading_rl.setVisibility(View.VISIBLE);
        }
    }

}
