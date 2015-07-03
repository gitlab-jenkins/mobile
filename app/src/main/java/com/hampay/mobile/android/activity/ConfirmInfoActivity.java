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

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationConfirmUserDataRequest;
import com.hampay.common.core.model.request.RegistrationFetchUserDataRequest;
import com.hampay.common.core.model.response.RegistrationConfirmUserDataResponse;
import com.hampay.common.core.model.response.RegistrationFetchUserDataResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestConfirmUserData;
import com.hampay.mobile.android.async.RequestFetchUserData;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.util.DeviceInfo;

public class ConfirmInfoActivity extends ActionBarActivity implements View.OnClickListener {

    CardView correct_CardView;
    CardView keeOn_without_cardView;
    CardView keeOn_with_cardView;

    Dialog confirm_info_dialog;
    LinearLayout confirm_check_ll;
    boolean confirm_check_value = false;
    ImageView confirm_check_img;
    FacedEditText user_phone;
    FacedEditText user_family;
    FacedEditText user_account_no;
    FacedEditText user_national_code;
    LinearLayout confirm_layout;

    SharedPreferences prefs;

    Context context;

    RelativeLayout loading_rl;

    private ResponseMessage<RegistrationConfirmUserDataResponse> registrationConfirmUserDataResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_info);

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        prefs = getPreferences(MODE_PRIVATE);

        context = this;

        keeOn_without_cardView = (CardView)findViewById(R.id.keeOn_without_cardView);
        keeOn_without_cardView.setOnClickListener(this);

        keeOn_with_cardView = (CardView)findViewById(R.id.keeOn_with_cardView);
        keeOn_with_cardView.setOnClickListener(this);
        confirm_layout = (LinearLayout)findViewById(R.id.confirm_layout);
        confirm_check_ll = (LinearLayout)findViewById(R.id.confirm_check_ll);
        confirm_check_img = (ImageView)findViewById(R.id.confirm_check_img);
        confirm_check_ll.setOnClickListener(this);

        user_phone = (FacedEditText)findViewById(R.id.user_phone);
        user_family = (FacedEditText)findViewById(R.id.user_family);
        user_account_no = (FacedEditText)findViewById(R.id.user_account_no);
        user_national_code = (FacedEditText)findViewById(R.id.user_national_code);


        RegistrationFetchUserDataRequest registrationFetchUserDataRequest = new RegistrationFetchUserDataRequest();
        registrationFetchUserDataRequest.setUserIdToken(prefs.getString("UserIdToken", ""));
        correct_CardView = (CardView)findViewById(R.id.correct_CardView);
        correct_CardView.setOnClickListener(this);

        new RequestFetchUserData(context, new RequestFetchUserDataTaskCompleteListener()).execute(registrationFetchUserDataRequest);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.keeOn_without_cardView:

                Rect displayRectangle = new Rect();
                Activity parent = (Activity) ConfirmInfoActivity.this;
                Window window = parent.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                View view = getLayoutInflater().inflate(R.layout.dialog_confirm_info, null);

                FacedTextView check_account = (FacedTextView) view.findViewById(R.id.check_account);
                FacedTextView uncheck_account = (FacedTextView) view.findViewById(R.id.uncheck_account);

                check_account.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirm_info_dialog.dismiss();

                        Intent intent = new Intent();
                        intent.setClass(ConfirmInfoActivity.this, RegVerifyAccountNoActivity.class);
                        startActivity(intent);

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

            case R.id.keeOn_with_cardView:
                Intent intent = new Intent();
                intent.setClass(ConfirmInfoActivity.this, RegVerifyAccountNoActivity.class);
                startActivity(intent);
                break;

            case R.id.correct_CardView:

                user_phone.setFocusableInTouchMode(true);
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
                    confirm_check_img.setImageDrawable(null);
                    correct_CardView.setVisibility(View.VISIBLE);
                    confirm_layout.setVisibility(View.GONE);
                }
                break;
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
                confirm_check_img.setImageResource(R.drawable.tick_icon);
                correct_CardView.setVisibility(View.GONE);
                user_phone.setFocusable(false);
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
