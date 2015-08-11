package com.hampay.mobile.android.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.BankListRequest;
import com.hampay.common.core.model.request.BusinessListRequest;
import com.hampay.common.core.model.request.BusinessPaymentRequest;
import com.hampay.common.core.model.request.BusinessSearchRequest;
import com.hampay.common.core.model.request.ChangeMemorableWordRequest;
import com.hampay.common.core.model.request.ChangePassCodeRequest;
import com.hampay.common.core.model.request.ContactUsRequest;
import com.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import com.hampay.common.core.model.request.IndividualPaymentRequest;
import com.hampay.common.core.model.request.RegistrationConfirmUserDataRequest;
import com.hampay.common.core.model.request.RegistrationEntryRequest;
import com.hampay.common.core.model.request.RegistrationFetchUserDataRequest;
import com.hampay.common.core.model.request.RegistrationMemorableWordEntryRequest;
import com.hampay.common.core.model.request.RegistrationPassCodeEntryRequest;
import com.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import com.hampay.common.core.model.request.RegistrationVerifyAccountRequest;
import com.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import com.hampay.common.core.model.request.RegistrationVerifyTransferMoneyRequest;
import com.hampay.common.core.model.request.TACAcceptRequest;
import com.hampay.common.core.model.request.TACRequest;
import com.hampay.common.core.model.request.TransactionListRequest;
import com.hampay.common.core.model.request.UserProfileRequest;
import com.hampay.common.core.model.request.VerifyAccountRequest;
import com.hampay.common.core.model.request.VerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import com.hampay.common.core.model.response.BusinessPaymentResponse;
import com.hampay.common.core.model.response.ContactUsResponse;
import com.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import com.hampay.common.core.model.response.IndividualPaymentResponse;
import com.hampay.common.core.model.response.TACAcceptResponse;
import com.hampay.common.core.model.response.dto.UserProfileDTO;
import com.hampay.mobile.android.Helper.DatabaseHelper;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.AppSliderActivity;
import com.hampay.mobile.android.activity.GuideDetailActivity;
import com.hampay.mobile.android.activity.HamPayLoginActivity;
import com.hampay.mobile.android.activity.MainActivity;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestBankList;
import com.hampay.mobile.android.async.RequestBusinessPayment;
import com.hampay.mobile.android.async.RequestChangeMemorableWord;
import com.hampay.mobile.android.async.RequestChangePassCode;
import com.hampay.mobile.android.async.RequestConfirmUserData;
import com.hampay.mobile.android.async.RequestContactHampayEnabled;
import com.hampay.mobile.android.async.RequestFetchUserData;
import com.hampay.mobile.android.async.RequestHamPayBusiness;
import com.hampay.mobile.android.async.RequestIndividualPayment;
import com.hampay.mobile.android.async.RequestLogout;
import com.hampay.mobile.android.async.RequestMemorableWordEntry;
import com.hampay.mobile.android.async.RequestPassCodeEntry;
import com.hampay.mobile.android.async.RequestRegisterVerifyAccount;
import com.hampay.mobile.android.async.RequestRegistrationEntry;
import com.hampay.mobile.android.async.RequestRegistrationSendSmsToken;
import com.hampay.mobile.android.async.RequestRegistrationVerifyTransferMoney;
import com.hampay.mobile.android.async.RequestSearchHamPayBusiness;
import com.hampay.mobile.android.async.RequestTAC;
import com.hampay.mobile.android.async.RequestTACAccept;
import com.hampay.mobile.android.async.RequestUserProfile;
import com.hampay.mobile.android.async.RequestUserTransaction;
import com.hampay.mobile.android.async.RequestVerifyAccount;
import com.hampay.mobile.android.async.RequestVerifyMobile;
import com.hampay.mobile.android.async.RequestVerifyTransferMoney;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.model.FailedLoginResponse;
import com.hampay.mobile.android.model.LogoutData;
import com.hampay.mobile.android.model.LogoutResponse;
import com.hampay.mobile.android.model.RecentPay;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.webservice.WebServices;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by amir on 7/8/15.
 */
public class HamPayDialog {

    SharedPreferences.Editor editor;

    Activity activity;
    Dialog dialog;

    DatabaseHelper dbHelper;

    public HamPayDialog(Activity activity){

        this.activity = activity;

        editor = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE).edit();

        dbHelper = new DatabaseHelper(activity);

    }


    public void showDisMatchPasswordDialog(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_dismatch_password, null);

        FacedTextView retry_password = (FacedTextView) view.findViewById(R.id.retry_password);

        retry_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showCommunicateDialog(final int type, final String cellNumber){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_communicate_confirm, null);

        FacedTextView communicate_confirm = (FacedTextView) view.findViewById(R.id.communicate_confirm);
        FacedTextView communicate_disconfirm = (FacedTextView) view.findViewById(R.id.communicate_disconfirm);

        communicate_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (type == 0){
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", cellNumber, null)));
                }else if (type == 1){
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", cellNumber, null)));
                }
            }
        });

        communicate_disconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    public void showNoResultSearchDialog(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_no_result, null);

        FacedTextView retry_search = (FacedTextView) view.findViewById(R.id.retry_search);

        retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showIncorrectPrice(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_incorrect, null);

        FacedTextView retry_price = (FacedTextView) view.findViewById(R.id.retry_price);

        retry_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }



    public void showContactUsDialog(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_contact_us, null);

        FacedTextView send_message = (FacedTextView) view.findViewById(R.id.send_message);
        FacedTextView call_message = (FacedTextView) view.findViewById(R.id.call_message);

        ContactUsRequest contactUsRequest = new ContactUsRequest();
        contactUsRequest.setRequestUUID("");
        new HttpContactUs().execute(contactUsRequest);

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (contactUsMail.length() != 0) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", contactUsMail, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
                    emailIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.insert_message));
                    activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.hampay_contact)));
                }
            }
        });

        call_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (contactUsPhone.length() != 0) {

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactUsPhone));
                    activity.startActivity(intent);
                }
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    public void showHelpDialog(final String help_url){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_help, null);

        final FacedTextView contact_us = (FacedTextView) view.findViewById(R.id.contact_us);
        FacedTextView user_help = (FacedTextView) view.findViewById(R.id.user_help);

        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showContactUsDialog();
            }
        });

        user_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, help_url);
                activity.startActivity(intent);
            }
        });

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        layoutParams.x = 25;
        layoutParams.y = 20;
        
        dialog.show();
    }


    String contactUsMail = "";
    String contactUsPhone = "";
    ResponseMessage<ContactUsResponse> contactUsResponseResponseMessage = null;

    public class HttpContactUs extends AsyncTask<ContactUsRequest, Void, String> {

        @Override
        protected String doInBackground(ContactUsRequest... params) {

            WebServices webServices = new WebServices();
            contactUsResponseResponseMessage = webServices.contactUsResponse(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (contactUsResponseResponseMessage != null){
                contactUsMail = contactUsResponseResponseMessage.getService().getEmailAddress();
                contactUsPhone = contactUsResponseResponseMessage.getService().getPhoneNumber();
            }


        }
    }

    TACAcceptRequest tacAcceptRequest;
    RequestTACAccept requestTACAccept;

    public void showTACAcceptDialog(String accept_term) {

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tac_accept, null);

        FacedTextView tac_term = (FacedTextView) view.findViewById(R.id.tac_term);
        FacedTextView tac_accept = (FacedTextView) view.findViewById(R.id.tac_accept);
        FacedTextView tac_reject = (FacedTextView) view.findViewById(R.id.tac_reject);

        Pattern p = Pattern.compile(Constants.WEB_URL_REGEX);
        Matcher m = p.matcher(accept_term);
        while (m.find()) {
            final String urlStr = m.group();

            Spannable WordtoSpan = new SpannableString(accept_term);

            ClickableSpan privacySpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    if(urlStr.toLowerCase().contains("http://")) {
                        i.setData(Uri.parse(urlStr));
                    }else {
                        i.setData(Uri.parse("http://" + urlStr));
                    }
                    activity.startActivity(i);
                }
            };

            WordtoSpan.setSpan(privacySpan, accept_term.indexOf(urlStr), accept_term.indexOf(urlStr) + urlStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tac_term.setText(WordtoSpan);

            tac_term.setMovementMethod(LinkMovementMethod.getInstance());

        }


        tacAcceptRequest = new TACAcceptRequest();

        tac_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        tac_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestTACAccept = new RequestTACAccept(activity, new RequestTACAcceptResponseTaskCompleteListener());
                requestTACAccept.execute(tacAcceptRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        view.setMinimumHeight((int) (displayRectangle.height() * 0.5f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    private ResponseMessage<TACAcceptResponse> tACAcceptResponse;
    private UserProfileDTO userProfileDTO;

    public class RequestTACAcceptResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<TACAcceptResponse>>
    {
        public RequestTACAcceptResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<TACAcceptResponse> tacAcceptResponseMessage)
        {
            if (tacAcceptResponseMessage != null) {

                if (tacAcceptResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();

                    userProfileDTO = tacAcceptResponseMessage.getService().getUserProfile();

                    Intent intent = new Intent();
                    intent.setClass(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(Constants.USER_PROFILE_DTO, userProfileDTO);
                    editor.putBoolean(Constants.FORCE_USER_PROFILE, true);
                    editor.commit();
                    activity.finish();
                    activity.startActivity(intent);


                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

//                Intent intent = new Intent(activity, MainActivity.class);
//                intent.putExtra("userProfileDTO", userProfileDTO);
//                activity.startActivity(intent);

//                if (tacAcceptResponseMessage.getService().getShouldAcceptTAC()){
//
//                    (new HamPayDialog(activity)).showTACAcceptDialog(tacAcceptResponseMessage.getService().getTac());
//
//                }

                }else {
                    requestTACAccept = new RequestTACAccept(activity, new RequestTACAcceptResponseTaskCompleteListener());
                    showFailTACAcceeptRequestDialog(requestTACAccept, tacAcceptRequest,
                            tacAcceptResponseMessage.getService().getResultStatus().getCode(),
                            tacAcceptResponseMessage.getService().getResultStatus().getDescription());
                }
            }
            else {
                showFailTACAcceeptRequestDialog(requestTACAccept, tacAcceptRequest,
                        "2000",
                        activity.getString(R.string.msg_fail_tac_accept_request));
            }

        }

        @Override
        public void onTaskPreRun() { }
    }


    public void showExitDialog(final LogoutData logoutData){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_exit_app, null);

        FacedTextView exit_app_yes = (FacedTextView) view.findViewById(R.id.exit_app_yes);
        FacedTextView exit_app_no = (FacedTextView) view.findViewById(R.id.exit_app_no);

        exit_app_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();

                Intent intent = new Intent();
                intent.setClass(activity, HamPayLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);

                new RequestLogout(activity, new RequestLogoutResponseTaskCompleteListener()).execute(logoutData);

            }
        });

        exit_app_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    public class RequestLogoutResponseTaskCompleteListener implements AsyncTaskCompleteListener<LogoutResponse>
    {
        public RequestLogoutResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(LogoutResponse logoutResponse)
        {
            if (logoutResponse != null) {

            }
            else {

            }

        }

        @Override
        public void onTaskPreRun() {
        }
    }

    public void showRemovePasswordDialog(){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_remove_password, null);

        FacedTextView re_registeration = (FacedTextView) view.findViewById(R.id.re_registeration);
        FacedTextView cancel_remove_password = (FacedTextView) view.findViewById(R.id.cancel_remove_password);

        re_registeration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.clear().commit();
                editor.commit();

                dialog.dismiss();
                Intent intent = new Intent(activity, AppSliderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.finish();
                activity.startActivity(intent);
            }
        });

        cancel_remove_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    public void showExitRegistrationDialog(){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_exit_registration, null);

        FacedTextView exit_registration_yes = (FacedTextView) view.findViewById(R.id.exit_registration_yes);
        FacedTextView exit_registration_no = (FacedTextView) view.findViewById(R.id.exit_registration_no);

        exit_registration_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(activity, AppSliderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.finish();
                activity.startActivity(intent);
            }
        });

        exit_registration_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    public void showLoginFailDialog(FailedLoginResponse failedLoginResponse){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_login_fail, null);

        FacedTextView failedLoginText = (FacedTextView)view.findViewById(R.id.failedLoginText);

        failedLoginText.setText("کد خطای: " + failedLoginResponse.getCode()
                        + "\n"
//                + failedLoginResponse.getMessage()
                        + failedLoginResponse.getMessage()
//                + activity.getString(R.string.msg_fail_hampay_login
        );


        FacedTextView login_retry = (FacedTextView) view.findViewById(R.id.login_retry);
        FacedTextView remove_password = (FacedTextView) view.findViewById(R.id.remove_password);

        login_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        remove_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showRemovePasswordDialog();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    public void showIncorrectSMSVerification(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_incorrect_sms_verication, null);

        FacedTextView retry_sms_verification = (FacedTextView) view.findViewById(R.id.retry_sms_verification);

        retry_sms_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }



    RequestIndividualPayment requestIndividualPayment;
    IndividualPaymentRequest individualPaymentRequest;

    public void individualPaymentConfirmDialog(final IndividualPaymentConfirmResponse individualPaymentConfirmResponse,
                                               final Long amountValue,
                                               final String userMessage){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pay_one, null);
        FacedTextView pay_one_confirm = (FacedTextView) view.findViewById(R.id.pay_one_confirm);
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
        FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                individualPaymentRequest = new IndividualPaymentRequest();
                individualPaymentRequest.setAmount(amountValue);
                individualPaymentRequest.setCellNumber(individualPaymentConfirmResponse.getCellNumber());
                individualPaymentRequest.setMessage(userMessage);
                requestIndividualPayment = new RequestIndividualPayment(activity,
                        new RequestIndividualPaymentTaskCompleteListener(individualPaymentRequest, individualPaymentConfirmResponse.getFullName()));
                requestIndividualPayment.execute(individualPaymentRequest);
            }
        });


        dis_confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        pay_one_confirm.setText(activity.getString(R.string.pay_one_confirm, amountValue.toString(),
                individualPaymentConfirmResponse.getFullName(),
                individualPaymentConfirmResponse.getBankName()));

        view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    public void individualPaymentDialog(final IndividualPaymentResponse individualPaymentResponse,
                                        final IndividualPaymentRequest individualPaymentRequest,
                                        final String contactName){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pay_one_ref, null);

        FacedTextView pay_one_confirm_ref = (FacedTextView) view.findViewById(R.id.pay_one_confirm_ref);
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);


        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.setResult(1024);
                activity.finish();
                activity.onBackPressed();
            }
        });

        pay_one_confirm_ref.setText(activity.getString(R.string.pay_one_ref, individualPaymentResponse.getRefCode()));

        RecentPay recentPay = new RecentPay();

        if (!dbHelper.getExistRecentPay(individualPaymentRequest.getCellNumber())) {

            recentPay = new RecentPay();
            recentPay.setName(contactName);
            recentPay.setPhone(individualPaymentRequest.getCellNumber());
            recentPay.setMessage(individualPaymentRequest.getMessage());
            dbHelper.createRecentPAy(recentPay);

        }else {
            recentPay = new RecentPay();
            recentPay = dbHelper.getRecentPay(individualPaymentRequest.getCellNumber());

            recentPay.setMessage(individualPaymentRequest.getMessage());
            dbHelper.updateRecentPay(recentPay);
        }

        view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    public class RequestIndividualPaymentTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<IndividualPaymentResponse>> {

        String contactName;
        IndividualPaymentRequest individualPaymentRequest;

        public RequestIndividualPaymentTaskCompleteListener(IndividualPaymentRequest individualPaymentRequest, String contactName){
            this.contactName = contactName;
            this.individualPaymentRequest = individualPaymentRequest;
        }

        @Override
        public void onTaskComplete(ResponseMessage<IndividualPaymentResponse> individualPaymentResponseMessage) {

            if (individualPaymentResponseMessage != null){
                if (individualPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    individualPaymentDialog(individualPaymentResponseMessage.getService(),
                            individualPaymentRequest,
                            contactName);
                }else {
                    showFailPaymentDialog(individualPaymentResponseMessage.getService().getResultStatus().getCode(),
                            individualPaymentResponseMessage.getService().getResultStatus().getDescription());
                }
            }else {
                new HamPayDialog(activity).showFailPaymentDialog("2000",
                        activity.getString(R.string.msg_fail_payment));
            }
        }

        @Override
        public void onTaskPreRun() { }
    }



    RequestBusinessPayment requestBusinessPayment;
    BusinessPaymentRequest businessPaymentRequest;

    public void businessPaymentConfirmDialog(final BusinessPaymentConfirmResponse businessPaymentConfirmResponse,
                                             final Long amountValue,
                                             final String userMessage){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pay_one, null);
        FacedTextView pay_one_confirm = (FacedTextView) view.findViewById(R.id.pay_one_confirm);
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
        FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                businessPaymentRequest = new BusinessPaymentRequest();
                businessPaymentRequest.setAmount(amountValue);
                businessPaymentRequest.setBusinessCode(businessPaymentConfirmResponse.getBusinessCode());
                businessPaymentRequest.setMessage(userMessage);
                requestBusinessPayment = new RequestBusinessPayment(activity,
                        new RequestBusinessPaymentTaskCompleteListener(businessPaymentRequest, businessPaymentConfirmResponse.getFullName()));
                requestBusinessPayment.execute(businessPaymentRequest);
            }
        });


        dis_confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        pay_one_confirm.setText(activity.getString(R.string.pay_one_confirm, amountValue.toString(),
                businessPaymentConfirmResponse.getFullName(),
                businessPaymentConfirmResponse.getBankName()));

        view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    public class RequestBusinessPaymentTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BusinessPaymentResponse>> {

        String businessName;
        BusinessPaymentRequest businessPaymentRequest;

        public RequestBusinessPaymentTaskCompleteListener(BusinessPaymentRequest businessPaymentRequest, String businessName){
            this.businessName = businessName;
            this.businessPaymentRequest = businessPaymentRequest;
        }

        @Override
        public void onTaskComplete(ResponseMessage<BusinessPaymentResponse> businessPaymentResponseMessage) {

            if (businessPaymentResponseMessage != null){
                if (businessPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    new HamPayDialog(activity).businessPaymentDialog(businessPaymentResponseMessage.getService(),
                            businessPaymentRequest,
                            businessName);
                }else {
                    showFailPaymentDialog(businessPaymentResponseMessage.getService().getResultStatus().getCode(),
                            businessPaymentResponseMessage.getService().getResultStatus().getDescription());
                }
            }else {
                new HamPayDialog(activity).showFailPaymentDialog("2000",
                        activity.getString(R.string.msg_fail_payment));
            }
        }

        @Override
        public void onTaskPreRun() { }
    }


    public void businessPaymentDialog(final BusinessPaymentResponse businessPaymentResponse,
                                      final BusinessPaymentRequest businessPaymentRequest,
                                      final String contactName){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pay_one_ref, null);

        FacedTextView pay_one_confirm_ref = (FacedTextView) view.findViewById(R.id.pay_one_confirm_ref);
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);


        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
                activity.onBackPressed();
            }
        });

        pay_one_confirm_ref.setText(activity.getString(R.string.pay_one_ref, businessPaymentResponse.getRefCode()));


        view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    public void showPreventRootDeviceDialog(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_prevent_root_device, null);

        FacedTextView prevent_rooted_device = (FacedTextView) view.findViewById(R.id.prevent_rooted_device);

        prevent_rooted_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailBankListDialog(final RequestBankList requestBankList,
                                       final BankListRequest bankListRequest,
                                       final String code,
                                       final String message){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_bank_list, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_fetch_bank_list = (FacedTextView) view.findViewById(R.id.retry_fetch_bank_list);

        retry_fetch_bank_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestBankList.execute(bankListRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailRegistrationEntryDialog(final RequestRegistrationEntry requestRegistrationEntry,
                                                final RegistrationEntryRequest registrationEntryRequest,
                                                final String code,
                                                final String message){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_entry, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_registration_entry = (FacedTextView) view.findViewById(R.id.retry_registration_entry);

        retry_registration_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestRegistrationEntry.execute(registrationEntryRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailRegistrationSendSmsTokenDialog(final RequestRegistrationSendSmsToken requestRegistrationSendSmsToken,
                                                       final RegistrationSendSmsTokenRequest registrationEntryRequest,
                                                       final String code,
                                                       final String message){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_send_sms_token, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_registration_sms_token = (FacedTextView) view.findViewById(R.id.retry_registration_sms_token);

        retry_registration_sms_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestRegistrationSendSmsToken.execute(registrationEntryRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailRegistrationVerifyMobileDialog(final RequestVerifyMobile requestVerifyMobile,
                                                       final RegistrationVerifyMobileRequest registrationVerifyMobileRequest,
                                                       final String code,
                                                       final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_verify_mobile_request, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_registration_verify_mobile = (FacedTextView) view.findViewById(R.id.retry_registration_verify_mobile);

        retry_registration_verify_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestVerifyMobile.execute(registrationVerifyMobileRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailConfirmUserDataDialog(final RequestConfirmUserData requestConfirmUserData,
                                              final RegistrationConfirmUserDataRequest registrationConfirmUserDataRequest,
                                              final String code,
                                              final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_confirm_user_data, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_confirm_user_data = (FacedTextView) view.findViewById(R.id.retry_confirm_user_data);

        retry_confirm_user_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestConfirmUserData.execute(registrationConfirmUserDataRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailFetchUserDataDialog(final RequestFetchUserData requestFetchUserData,
                                            final RegistrationFetchUserDataRequest registrationFetchUserDataRequest,
                                            final String code,
                                            final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_fetch_user_data, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_confirm_user_data = (FacedTextView) view.findViewById(R.id.retry_confirm_user_data);

        FacedTextView retry_fetch_user_data = (FacedTextView) view.findViewById(R.id.retry_fetch_user_data);

        retry_fetch_user_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestFetchUserData.execute(registrationFetchUserDataRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }



    public void showFailRegisterVerifyAccountDialog(final RequestRegisterVerifyAccount requestRegisterVerifyAccount,
                                                    final RegistrationVerifyAccountRequest registrationVerifyAccountRequest,
                                                    final String code,
                                                    final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_register_verify_account, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_verify_account = (FacedTextView) view.findViewById(R.id.retry_verify_account);

        retry_verify_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestRegisterVerifyAccount.execute(registrationVerifyAccountRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailVerifyAccountDialog(final RequestVerifyAccount requestVerifyAccount,
                                                    final VerifyAccountRequest verifyAccountRequest,
                                                    final String code,
                                                    final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_register_verify_account, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_verify_account = (FacedTextView) view.findViewById(R.id.retry_verify_account);

        retry_verify_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestVerifyAccount.execute(verifyAccountRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailUserProfileDialog(final RequestUserProfile requestUserProfile,
                                            final UserProfileRequest userProfileRequest,
                                            final String code,
                                            final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_user_profile, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_user_profile = (FacedTextView) view.findViewById(R.id.retry_user_profile);

        retry_user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestUserProfile.execute(userProfileRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailRequestVerifyTransferMoneyDialog(final RequestRegistrationVerifyTransferMoney requestRegistrationVerifyTransferMoney,
                                                  final RegistrationVerifyTransferMoneyRequest registrationVerifyTransferMoneyRequest,
                                                  final String code,
                                                  final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_verify_transfer_money, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_verify_transfer_money = (FacedTextView) view.findViewById(R.id.retry_verify_transfer_money);

        retry_verify_transfer_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestRegistrationVerifyTransferMoney.execute(registrationVerifyTransferMoneyRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailVerifyTransferMoneyDialog(final RequestVerifyTransferMoney requestVerifyTransferMoney,
                                                         final VerifyTransferMoneyRequest verifyTransferMoneyRequest,
                                                         final String code,
                                                         final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_verify_transfer_money, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_verify_transfer_money = (FacedTextView) view.findViewById(R.id.retry_verify_transfer_money);

        retry_verify_transfer_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestVerifyTransferMoney.execute(verifyTransferMoneyRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailPasswordEntryDialog(final RequestPassCodeEntry requestPassCodeEntry,
                                            final RegistrationPassCodeEntryRequest registrationPassCodeEntryRequest,
                                            final String code,
                                            final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_password_entry, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_pass_code_entry = (FacedTextView) view.findViewById(R.id.retry_pass_code_entry);

        retry_pass_code_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestPassCodeEntry.execute(registrationPassCodeEntryRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailMemorableEntryDialog(final RequestMemorableWordEntry requestMemorableWordEntry,
                                             final RegistrationMemorableWordEntryRequest registrationMemorableWordEntryRequest,
                                             final String code,
                                             final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_memorable_entry, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_memorable_entry = (FacedTextView) view.findViewById(R.id.retry_memorable_entry);

        retry_memorable_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestMemorableWordEntry.execute(registrationMemorableWordEntryRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailTCRequestDialog(final RequestTAC requestTAC,
                                        final TACRequest tacRequest,
                                        final String code,
                                        final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_tac_request, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_tac_request = (FacedTextView) view.findViewById(R.id.retry_tac_request);

        retry_tac_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestTAC.execute(tacRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailTACAcceeptRequestDialog(final RequestTACAccept requestTACAccept,
                                                final TACAcceptRequest tacAcceptRequest,
                                                final String code,
                                                final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_tac_accept_request, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_tac_accept_request = (FacedTextView) view.findViewById(R.id.retry_tac_accept_request);

        retry_tac_accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestTACAccept.execute(tacAcceptRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showSuccessChangeSettingDialog(final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_success_change_setting, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseMessage.setText(message);

        FacedTextView success_change_setting = (FacedTextView) view.findViewById(R.id.success_change_setting);

        success_change_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailChangePassCodeDialog(final RequestChangePassCode requestChangePassCode,
                                             final ChangePassCodeRequest changePassCodeRequest,
                                             final String code,
                                             final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_change_pass_code, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_change_pass_code = (FacedTextView) view.findViewById(R.id.retry_change_pass_code);

        retry_change_pass_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestChangePassCode.execute(changePassCodeRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }



    public void showFailChangeMemorableWordDialog(final RequestChangeMemorableWord requestChangeMemorableWord,
                                                  final ChangeMemorableWordRequest changeMemorableWordRequest,
                                                  final String code,
                                                  final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_change_memorable_word, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_change_memorable_word = (FacedTextView) view.findViewById(R.id.retry_change_memorable_word);

        retry_change_memorable_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestChangeMemorableWord.execute(changeMemorableWordRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailUserTransactionDialog(final RequestUserTransaction requestUserTransaction,
                                              final TransactionListRequest transactionListRequest,
                                              final String code,
                                              final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_user_transaction, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_user_transation = (FacedTextView) view.findViewById(R.id.retry_user_transation);

        retry_user_transation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestUserTransaction.execute(transactionListRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailBusinessListDialog(final RequestHamPayBusiness requestHamPayBusiness,
                                           final BusinessListRequest businessListRequest,
                                           final String code,
                                           final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_business_list, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_business_list = (FacedTextView) view.findViewById(R.id.retry_business_list);

        retry_business_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestHamPayBusiness.execute(businessListRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailBusinessSearchListDialog(final RequestSearchHamPayBusiness requestSearchHamPayBusiness,
                                                 final BusinessSearchRequest businessSearchRequest,
                                                 final String code,
                                                 final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_business_search_list, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_business_search_list = (FacedTextView) view.findViewById(R.id.retry_business_search_list);

        retry_business_search_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestSearchHamPayBusiness.execute(businessSearchRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailContactsHamPayEnabledDialog(final RequestContactHampayEnabled requestContactHampayEnabled,
                                                    final ContactsHampayEnabledRequest contactsHampayEnabledRequest,
                                                    final String code,
                                                    final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_contacts_enabled, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_contacts_enabled = (FacedTextView) view.findViewById(R.id.retry_contacts_enabled);

        retry_contacts_enabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestContactHampayEnabled.execute(contactsHampayEnabledRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailPaymentDialog(final String code,
                                      final String message){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_payment, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_payment = (FacedTextView) view.findViewById(R.id.retry_payment);

        retry_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailPaymentPermissionDialog(String message){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_payment_permission, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseMessage.setText(activity.getString(R.string.msg_fail_payment_permission, message));


        FacedTextView payment_permission = (FacedTextView) view.findViewById(R.id.payment_permission);

        payment_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showIncorrectAmountDialog(Long MaxXferAmount, Long MinXferAmount){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_payment_permission, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        NumberFormat nf = NumberFormat.getInstance();

        responseMessage.setText(activity.getString(R.string.msg_incorrect_amount, nf.format(MaxXferAmount) + "",  nf.format(MinXferAmount) + ""));

        FacedTextView payment_permission = (FacedTextView) view.findViewById(R.id.payment_permission);

        payment_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


}
