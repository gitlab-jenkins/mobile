package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationConfirmUserDataRequest;
import com.hampay.common.core.model.request.RegistrationFetchUserDataRequest;
import com.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import com.hampay.common.core.model.response.RegistrationConfirmUserDataResponse;
import com.hampay.common.core.model.response.RegistrationFetchUserDataResponse;
import com.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedEditText;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.util.DeviceInfo;
import com.hampay.mobile.android.webservice.WebServices;

public class ConfirmInfoActivity extends ActionBarActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_info);

        prefs = getPreferences(MODE_PRIVATE);


        keeOn_without_cardView = (CardView)findViewById(R.id.keeOn_without_cardView);
        keeOn_with_cardView = (CardView)findViewById(R.id.keeOn_with_cardView);
        confirm_layout = (LinearLayout)findViewById(R.id.confirm_layout);
        confirm_check_ll = (LinearLayout)findViewById(R.id.confirm_check_ll);
        confirm_check_img = (ImageView)findViewById(R.id.confirm_check_img);
        confirm_check_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirm_check_value = !confirm_check_value;

                if (confirm_check_value) {

                    RegistrationConfirmUserDataRequest registrationConfirmUserDataRequest = new RegistrationConfirmUserDataRequest();
                    registrationConfirmUserDataRequest.setUserIdToken(prefs.getString("UserIdToken", ""));
                    registrationConfirmUserDataRequest.setImei(new DeviceInfo(getApplicationContext()).getIMEI());
                    registrationConfirmUserDataRequest.setIsVerified(confirm_check_value);
                    registrationConfirmUserDataRequest.setIp("192.168.1.1");
                    registrationConfirmUserDataRequest.setDeviceId(new DeviceInfo(getApplicationContext()).getDeviceId());

                    new HttpRegistrationConfirmUserDataResponse().execute(registrationConfirmUserDataRequest);
                }else {
                    confirm_check_img.setImageDrawable(null);
                    correct_CardView.setVisibility(View.VISIBLE);
                    user_phone.setFocusable(true);
                    user_family.setFocusable(true);
                    user_account_no.setFocusable(true);
                    user_national_code.setFocusable(true);
                    confirm_layout.setVisibility(View.GONE);
                }

//                confirm_check_value = !confirm_check_value;
//                if (confirm_check_value){
//                    confirm_check_img.setImageResource(R.drawable.tick_icon);
//                    correct_CardView.setVisibility(View.GONE);
//                    user_phone.setFocusableInTouchMode(true);
//                    user_family.setFocusableInTouchMode(true);
//                    user_account_no.setFocusableInTouchMode(true);
//                    user_national_code.setFocusableInTouchMode(true);
//                    confirm_layout.setVisibility(View.VISIBLE);
//
//                }else {
//                    confirm_check_img.setImageDrawable(null);
//                    correct_CardView.setVisibility(View.VISIBLE);
//                    user_phone.setFocusable(false);
//                    user_family.setFocusable(false);
//                    user_account_no.setFocusable(false);
//                    user_national_code.setFocusable(false);
//                    confirm_layout.setVisibility(View.GONE);
//                }

            }
        });

        user_phone = (FacedEditText)findViewById(R.id.user_phone);
        user_family = (FacedEditText)findViewById(R.id.user_family);
        user_account_no = (FacedEditText)findViewById(R.id.user_account_no);
        user_national_code = (FacedEditText)findViewById(R.id.user_national_code);




        RegistrationFetchUserDataRequest registrationFetchUserDataRequest = new RegistrationFetchUserDataRequest();
        registrationFetchUserDataRequest.setUserIdToken(prefs.getString("UserIdToken", ""));

        new HttpRegistrationFetchUserDataResponse().execute(registrationFetchUserDataRequest);

        correct_CardView = (CardView)findViewById(R.id.correct_CardView);
        correct_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_phone.setFocusable(true);
                user_family.setFocusable(true);
                user_account_no.setFocusable(true);
                user_national_code.setFocusable(true);

//                Rect displayRectangle = new Rect();
//                Activity parent = (Activity) ConfirmInfoActivity.this;
//                Window window = parent.getWindow();
//                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//
//                View view = getLayoutInflater().inflate(R.layout.dialog_confirm_info, null);
//
//                FacedTextView check_account = (FacedTextView) view.findViewById(R.id.check_account);
//                FacedTextView uncheck_account = (FacedTextView) view.findViewById(R.id.uncheck_account);
//
//                check_account.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        confirm_info_dialog.dismiss();
//
//                    }
//                });
//
//
//                uncheck_account.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        confirm_info_dialog.dismiss();
//                        Intent intent = new Intent();
//                        intent.setClass(ConfirmInfoActivity.this, VerifyAccountNoActivity.class);
//                        startActivity(intent);
//                    }
//                });
//
//                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
//                confirm_info_dialog = new Dialog(ConfirmInfoActivity.this);
//                confirm_info_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                confirm_info_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                confirm_info_dialog.setContentView(view);
//                confirm_info_dialog.setTitle(null);
//                confirm_info_dialog.setCanceledOnTouchOutside(false);
//
//                confirm_info_dialog.show();

            }
        });
    }


    private ResponseMessage<RegistrationFetchUserDataResponse> registrationFetchUserDataResponse;

    public class HttpRegistrationFetchUserDataResponse extends AsyncTask<RegistrationFetchUserDataRequest, Void, String> {

        @Override
        protected String doInBackground(RegistrationFetchUserDataRequest... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            registrationFetchUserDataResponse = webServices.registrationFetchUserDataResponse(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (registrationFetchUserDataResponse.getService().getResultStatus() != null) {
                user_phone.setText(registrationFetchUserDataResponse.getService().getCellNumber());
                user_family.setText(registrationFetchUserDataResponse.getService().getFulName());
                user_account_no.setText(registrationFetchUserDataResponse.getService().getAccountNumber());
                user_national_code.setText(registrationFetchUserDataResponse.getService().getNationalCode());
            }
        }
    }

    private ResponseMessage<RegistrationConfirmUserDataResponse> registrationConfirmUserDataResponse;

    public class HttpRegistrationConfirmUserDataResponse extends AsyncTask<RegistrationConfirmUserDataRequest, Void, String> {

        @Override
        protected String doInBackground(RegistrationConfirmUserDataRequest... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            registrationConfirmUserDataResponse = webServices.registrationConfirmUserDataResponse(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (registrationConfirmUserDataResponse.getService().getResultStatus() != null) {

                confirm_check_img.setImageResource(R.drawable.tick_icon);
                correct_CardView.setVisibility(View.GONE);
                user_phone.setFocusableInTouchMode(false);
                user_family.setFocusableInTouchMode(false);
                user_account_no.setFocusableInTouchMode(false);
                user_national_code.setFocusableInTouchMode(false);
                confirm_layout.setVisibility(View.VISIBLE);

            }
        }
    }


}
